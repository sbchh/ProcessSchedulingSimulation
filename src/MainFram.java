import java.awt.EventQueue;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Date;

public class MainFram extends JFrame {

    private JPanel contentPane;
    JButton buttonStart;
    JButton buttonStop;
    private JTextField textFieldCurrentProcess;
    private DefaultListModel listOtherWaitQueueModel;
    private DefaultListModel listReadyQueueModel;
    private DefaultListModel listBackupReadyQueueModel;
    private DefaultListModel listInputWaitQueueModel;
    private DefaultListModel listOutputWaitQueueModel;
    private JTextField textFieldTimeSlice;

    private Schedule schedule = new Schedule();
    private boolean flag = false;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    MainFram frame = new MainFram();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the frame.
     */
    public MainFram() {
        setForeground(new Color(0, 0, 0));
        setTitle("时间片轮转调度模拟器");
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 808, 470);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        Toolkit kit = Toolkit.getDefaultToolkit(); // 定义工具包
        Dimension screenSize = kit.getScreenSize(); // 获取屏幕的尺寸
        int screenWidth = screenSize.width/2; // 获取屏幕的宽
        int screenHeight = screenSize.height/2; // 获取屏幕的高
        int height = this.getHeight(); int width = this.getWidth();
        setLocation(screenWidth-width/2, screenHeight-height/2);

        JButton button = new JButton("打开文件");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser(); //设置选择器
                int returnVal = chooser.showOpenDialog(button); //是否打开文件选择框
                String filepath = null;
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    filepath = chooser.getSelectedFile().getAbsolutePath(); //获取文件的绝对路径
                    JOptionPane.showMessageDialog(null, "你选择的文件绝对路径为\n" + filepath);
                    // 将选择的文件设置到调度器的文件属性中
                    File file = new File(filepath);
                    schedule.setProcessFile(file);
                }else{
                    JOptionPane.showMessageDialog(null, "没有选择文件");
                }
            }
        });
        button.setBackground(new Color(204, 255, 255));
        button.setFont(new Font("微软雅黑", Font.BOLD, 18));
        button.setForeground(Color.DARK_GRAY);
        button.setBounds(26, 42, 113, 27);
        contentPane.add(button);

        buttonStart = new JButton("开始调度");
        buttonStart.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        buttonStart.setEnabled(false);
                        flag = true;
                        buttonStart.setText("调度中...");
                        schedule.setReadyQueue(); // 设置就绪队列PCB
                        // 输出到日志文件
                        String fileDir = schedule.getProcessFile().getParent() + "/" + Utils.DateToStr(new Date()) +"-logs.txt";
                        File file = new File(fileDir);
                        PrintStream out = null;
                        try {
                            out = new PrintStream(new FileOutputStream(file));
                        } catch (FileNotFoundException e1) {
                            e1.printStackTrace();
                        }
                        Schedule.showQueueProcess(schedule.getReadyQueue(), out);
                        while (flag == true) {
                            // 否则，运行该 PCB 中的指令
                            PCB pcb = null;
                            // 就绪队列中还有 PCB，调度的第 1 轮
                            if(schedule.getReadyQueue().size() != 0) {
                                pcb = schedule.getReadyQueue().get(0);
                                schedule.getReadyQueue().remove(0); // 从就绪队列中移除 PCB
                                schedule.getBackupReadyQueue().add(pcb);
                            } else{
                                // 循环结束条件
                                // 就绪队列中的 PCB 全部运行过一次，PCB 都被放在了后备就绪队列中
                                if(schedule.getBackupReadyQueue().size() == 0 && schedule.getReadyQueue().size() == 0) {
                                    break;
                                }
                                pcb = schedule.getBackupReadyQueue().get(0);
                            }
                            schedule.setCurrentRunPCB(pcb); // 选取就绪队列的队首 PCB 作为当前执行的 PCB
                            // 显示进程名称
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                textFieldCurrentProcess.setText(schedule.getCurrentRunPCB().getProcessName());
                                    try {
                                        Thread.sleep(1000);
                                    } catch (InterruptedException e1) {
                                        e1.printStackTrace();
                                    }
                                }
                            }).start();
                            // 一个时间片内执行指令的方法
                            try {
                                schedule.runInstruction(out);
                            } catch (InterruptedException e2) {
                                e2.printStackTrace();
                            }

                            // 时间轮转结束后，判断 PCB 中的指令是否已经运行完毕了
                            if(pcb.getInstructionList().size() == 0){
                                schedule.getOtherWaitQueue().add(pcb);
                                schedule.getBackupReadyQueue().remove(pcb);
                            } else {
                                schedule.getBackupReadyQueue().remove(pcb);
                                schedule.getBackupReadyQueue().add(pcb);
                            }
//                            new Thread(new Runnable() {
//                                @Override
//                                public void run() {
                                    // 更新每个队列控件
                                    Utils.updateJList(listReadyQueueModel, schedule.getReadyQueue());
                                    Utils.updateBackupJList(listBackupReadyQueueModel, schedule.getBackupReadyQueue());
                                    Utils.updateJList(listInputWaitQueueModel, schedule.getInputWaitQueue());
                                    Utils.updateJList(listOutputWaitQueueModel, schedule.getOutputWaitQueue());
                                    Utils.updateJList(listOtherWaitQueueModel, schedule.getOtherWaitQueue());
//                                }
//                            }).start();

                            // 一个时间片结束后，打印现在各个队列的进程名称
                            schedule.showEveryQueue(out);
                        }
                        textFieldCurrentProcess.setText("无");
                        out.println("进程调度模拟结束");
                        schedule.showEveryQueue(out);
                        buttonStart.setText("开始调度");
                    }
                }).start();
            }
        });
        buttonStart.setBackground(new Color(204, 255, 255));
        buttonStart.setForeground(Color.DARK_GRAY);
        buttonStart.setFont(new Font("微软雅黑", Font.BOLD, 18));
        buttonStart.setBounds(180, 44, 113, 27);
        contentPane.add(buttonStart);

        buttonStop = new JButton("暂停调度");
        buttonStop.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // 将调度器的标志设置为 false，并将开始按钮启用
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        flag = false;
                        buttonStart.setEnabled(true);
                        buttonStart.setText("开始调度");
                    }
                }).start();
            }
        });
        buttonStop.setBackground(new Color(204, 255, 255));
        buttonStop.setForeground(Color.DARK_GRAY);
        buttonStop.setFont(new Font("微软雅黑", Font.BOLD, 18));
        buttonStop.setBounds(341, 44, 113, 27);
        contentPane.add(buttonStop);

        JLabel label = new JLabel("时间片大小：");
        label.setForeground(Color.DARK_GRAY);
        label.setFont(new Font("微软雅黑", Font.BOLD, 18));
        label.setBounds(487, 50, 113, 18);
        contentPane.add(label);

        textFieldTimeSlice = new JTextField();
        textFieldTimeSlice.setFont(new Font("微软雅黑", Font.BOLD, 18));
        textFieldTimeSlice.setText("10");
        schedule.setTimeSlice(10);
        textFieldTimeSlice.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                schedule.setTimeSlice(Integer.parseInt(textFieldTimeSlice.getText()));
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                schedule.setTimeSlice(Integer.parseInt(textFieldTimeSlice.getText()));

            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                schedule.setTimeSlice(Integer.parseInt(textFieldTimeSlice.getText()));

            }
        });
        textFieldTimeSlice.setBounds(594, 47, 86, 24);
        contentPane.add(textFieldTimeSlice);
        textFieldTimeSlice.setColumns(10);

        JLabel label_1 = new JLabel("当前运行进程");
        label_1.setFont(new Font("微软雅黑", Font.BOLD, 18));
        label_1.setForeground(Color.DARK_GRAY);
        label_1.setBounds(26, 103, 122, 18);
        contentPane.add(label_1);

        textFieldCurrentProcess = new JTextField();
        textFieldCurrentProcess.setText("无");
        textFieldCurrentProcess.setFont(new Font("微软雅黑", Font.BOLD, 18));
        textFieldCurrentProcess.setBounds(143, 102, 86, 24);
        contentPane.add(textFieldCurrentProcess);
        textFieldCurrentProcess.setColumns(10);

        JLabel label_2 = new JLabel("就绪队列");
        label_2.setForeground(Color.DARK_GRAY);
        label_2.setFont(new Font("微软雅黑", Font.BOLD, 18));
        label_2.setBounds(40, 177, 86, 18);
        contentPane.add(label_2);

        listReadyQueueModel = new DefaultListModel();
        JList listReadyQueue = new JList(listReadyQueueModel);
        listReadyQueue.setFont(new Font("微软雅黑", Font.BOLD, 18));
        listReadyQueue.setBounds(28, 208, 111, 166);
        contentPane.add(listReadyQueue);

        JLabel label_3 = new JLabel("后备就绪队列");
        label_3.setForeground(Color.DARK_GRAY);
        label_3.setFont(new Font("微软雅黑", Font.BOLD, 18));
        label_3.setBounds(180, 177, 122, 18);
        contentPane.add(label_3);

        listBackupReadyQueueModel = new DefaultListModel();
        JList listBackupReadyQueue = new JList(listBackupReadyQueueModel);
        listBackupReadyQueue.setFont(new Font("微软雅黑", Font.BOLD, 18));
        listBackupReadyQueue.setBounds(180, 208, 111, 166);
        contentPane.add(listBackupReadyQueue);

        JLabel label_4 = new JLabel("输入等待队列");
        label_4.setForeground(Color.DARK_GRAY);
        label_4.setFont(new Font("微软雅黑", Font.BOLD, 18));
        label_4.setBounds(329, 177, 128, 18);
        contentPane.add(label_4);

        listInputWaitQueueModel = new DefaultListModel();
        JList listInputWaitQueue = new JList(listInputWaitQueueModel);
        listInputWaitQueue.setFont(new Font("微软雅黑", Font.BOLD, 18));
        listInputWaitQueue.setBounds(329, 208, 111, 166);
        contentPane.add(listInputWaitQueue);

        JLabel label_5 = new JLabel("输出等待队列");
        label_5.setForeground(Color.DARK_GRAY);
        label_5.setFont(new Font("微软雅黑", Font.BOLD, 18));
        label_5.setBounds(487, 177, 128, 18);
        contentPane.add(label_5);

        listOutputWaitQueueModel = new DefaultListModel();
        JList listOutputWaitQueue = new JList(listOutputWaitQueueModel);
        listOutputWaitQueue.setFont(new Font("微软雅黑", Font.BOLD, 18));
        listOutputWaitQueue.setBounds(486, 208, 111, 166);
        contentPane.add(listOutputWaitQueue);

        JLabel label_6 = new JLabel("其他等待队列");
        label_6.setForeground(Color.DARK_GRAY);
        label_6.setFont(new Font("微软雅黑", Font.BOLD, 18));
        label_6.setBounds(647, 177, 128, 18);
        contentPane.add(label_6);

        listOtherWaitQueueModel = new DefaultListModel();
        JList listOtherWaitQueue = new JList(listOtherWaitQueueModel);
        listOtherWaitQueue.setFont(new Font("微软雅黑", Font.BOLD, 18));
        listOtherWaitQueue.setBounds(647, 208, 111, 166);
        contentPane.add(listOtherWaitQueue);
    }
}
