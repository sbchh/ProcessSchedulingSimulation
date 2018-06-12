import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * PCB 调度类
 */
public class Schedule {
    private File processFile; // 进程描述文件
    private int timeSlice; // 时间片
    private ArrayList<PCB> readyQueue; // 就绪队列
    private ArrayList<PCB> backupReadyQueue; // 后备就绪队列
    private ArrayList<PCB> inputWaitQueue; // 输入等待队列
    private ArrayList<PCB> outputWaitQueue; // 输出等待队列
    private ArrayList<PCB> otherWaitQueue; // 其他等待队列
    private PCB currentRunPCB; // 当前执行的 PCB
    public static boolean FLAG = false; // 调度的标志

    public File getProcessFile() {
        return processFile;
    }

    public void setProcessFile(File processFile) {
        this.processFile = processFile;
    }

    public int getTimeSlice() {
        return timeSlice;
    }

    public void setTimeSlice(int timeSlice) {
        this.timeSlice = timeSlice;
    }

    public ArrayList<PCB> getReadyQueue() {
        return readyQueue;
    }

    public void setReadyQueue() {
        try {
            // 读文件操作
            FileReader fileReader = new FileReader(processFile);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String lineContent = null;
            ArrayList<PCB> pcbList = new ArrayList<>();
            PCB pcb = null;
            ArrayList<Instruction> instructionList = null;
            Instruction instruction = null;
            int i = 0; // 迭代
            // 循环遍历进程描述文件的每一行
            while ((lineContent = bufferedReader.readLine()) != null) {
                // 设置就绪队列中每个 PCB 的属性
                if(lineContent.charAt(0) == 'P') {
                    // P 开头，标志着新的进程加入
                    pcb = new PCB();
                    instructionList = new ArrayList<>();
                    pcb.setProcessName(lineContent); // 设置该 PCB 的进程名称
                } else if(lineContent.charAt(0) == 'H') {
                    // H 指令，标志着 PCB 执行结束
                    i++; // 累加器加 1
                    pcb.setInstructionList(instructionList);// 将指令链表设置到 PCB 中
                    pcbList.add(pcb); // 将 PCB 设置到就绪队列中
                } else {
                    // I、C、O、W 指令
                    int time = Integer.parseInt(lineContent.substring(1));
                    instruction = new Instruction(lineContent.charAt(0), time, time);
                    instructionList.add(instruction); // 将指令设置到指令链表
                }
            }
            readyQueue = pcbList; // 设置就绪队列的值
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<PCB> getBackupReadyQueue() {
        return backupReadyQueue;
    }

    public void setBackupReadyQueue(ArrayList<PCB> backupReadyQueue) {
        this.backupReadyQueue = backupReadyQueue;
    }

    public ArrayList<PCB> getInputWaitQueue() {
        return inputWaitQueue;
    }

    public void setInputWaitQueue(ArrayList<PCB> inputWaitQueue) {
        this.inputWaitQueue = inputWaitQueue;
    }

    public ArrayList<PCB> getOutputWaitQueue() {
        return outputWaitQueue;
    }

    public void setOutputWaitQueue(ArrayList<PCB> outputWaitQueue) {
        this.outputWaitQueue = outputWaitQueue;
    }

    public ArrayList<PCB> getOtherWaitQueue() {
        return otherWaitQueue;
    }

    public void setOtherWaitQueue(ArrayList<PCB> otherWaitQueue) {
        this.otherWaitQueue = otherWaitQueue;
    }

    public PCB getCurrentRunPCB() {
        return currentRunPCB;
    }

    public void setCurrentRunPCB(PCB currentRunPCB) {
        this.currentRunPCB = currentRunPCB;
    }

    // 时间片轮转算法
    public void poll() throws InterruptedException {
        // 处理当前的 PCB 指令
        int time = 0; // 指令剩余运行需要的时间
        int remainTime = timeSlice; // 时间片内剩余的时间
        char name; // 当前指令的名称
        System.out.println("当前运行的进程为:" + currentRunPCB.getProcessName());
        // 一个时间片内运行指令
        while (remainTime > 0) {
            System.out.println("运行 "+currentRunPCB.getProcessName() + " 进程中...");
            time = currentRunPCB.getInstructionList().get(0).getRemainTime();
            name = currentRunPCB.getInstructionList().get(0).getName();
            System.out.println("运行 " +name+ " 指令中...");
            if(name == 'I') {
                inputWaitQueue.add(currentRunPCB); // 添加到输入等待队列
            } else if(name == 'O') {
                outputWaitQueue.add(currentRunPCB); // 添加到输出等待队列
            }
            if(time <= remainTime) {
                // 当前 PCB 的指令可以在时间片规定的时间完成
                currentRunPCB.getInstructionList().remove(0); // 移除已经完成的队首指令
                remainTime -= time; // 剩余的时间运行下一个指令
                Thread.sleep(time * 100); // 模拟指令占用的时间，10 为 1s，即 1000ms
                System.out.println("运行 " + name + "指令结束，但是时间片内的时间还有剩余，在剩余的时间开始运行下一指令");
                // IO指令运行完毕，在输入输出等待只做移除
                if(name == 'I') {
                    inputWaitQueue.remove(currentRunPCB);
                } else if(name == 'O') {
                    outputWaitQueue.remove(currentRunPCB);
                }
                continue;
            } else {
                // 当前 PCB 指令不能在时间片内规定的时间完成
                int tempTime = currentRunPCB.getInstructionList().get(0).getRemainTime() - remainTime; // 计算该指令剩余需要运行的时间
                currentRunPCB.getInstructionList().get(0).setRemainTime(tempTime);
                Thread.sleep(remainTime * 100);
                System.out.println("运行 " + name + "指令结束, 一个时间片内的时间无法完成该指令的运行，将在下一个时间片内继续运行该指令剩余所需的时间");
                // IO指令并未运行完毕，移除它在队首的位置，该 PCB 继续在输入输出队列排队
                if (name == 'I') {
                    inputWaitQueue.remove(currentRunPCB);
                    inputWaitQueue.add(currentRunPCB);
                } else if(name == 'O') {
                    outputWaitQueue.remove(currentRunPCB);
                    outputWaitQueue.remove(currentRunPCB);
                }
            }
        }
    }

    // 打印各个队列的进程名称
    public void showEveryQueue() {
        // 就绪队列
        Schedule.showQueueProcess(readyQueue);
        // 后备就绪队列
        Schedule.showQueueProcess(backupReadyQueue);
        // 输入等待队列
        Schedule.showQueueProcess(inputWaitQueue);
        // 输出等待队列
        Schedule.showQueueProcess(outputWaitQueue);
        // 其他等待队列
        Schedule.showQueueProcess(otherWaitQueue);
    }

    // 打印队列中的进程名称
    public static void showQueueProcess (ArrayList<PCB> list) {
        for (int i = 0, size = list.size(); i < size; i++) {
            System.out.println(list.get(i).getProcessName());
        }
    }
}