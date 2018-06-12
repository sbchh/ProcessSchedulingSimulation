import org.junit.Test;

import java.io.File;

/**
 * 程序的主类
 */

public class Main {

    public static void main(String[] args) {
        /**
         * 就绪队列在读取文件时添加 PCB，后备就绪队列和其他等待队列在运行完一个时间片后添加 PCB，输入输出等待队列在时间片内添加 PCB。
         * 就绪队列在使用一次就就移除 PCB，后备就绪队列和 PCB 运行完毕移除该 PCB，输入输出等待队列在时间片内移除 PCB。
         */

        Schedule schedule = new Schedule();
        // 打开文件
        {
            String filePath = "D:\\Workspace\\Java\\Intellij IDEA\\ProcessSchedulingSimulation\\src\\prc.txt";
            File file = new File(filePath);
            schedule.setProcessFile(file); // 读取指定的进程文件
        }

        // 设置调度时间片大小
        {
            schedule.setTimeSlice(10);
        }

        // 开始调度
        {
            Schedule.FLAG = true;
            schedule.setReadyQueue(); // 设置就绪队列PCB
            while (Schedule.FLAG == true) {
                // 否则，运行该 PCB 中的指令
                PCB pcb = null;
                // 就绪队列中还有 PCB
                if(schedule.getReadyQueue() != null) {
                    pcb = schedule.getReadyQueue().get(0);
                    schedule.getReadyQueue().remove(0); // 就绪队列中 PCB 的移除
                }
                // 就绪队列中的 PCB 全部运行过一次，都被放在了 后备就绪队列中
                pcb = schedule.getBackupReadyQueue().get(0);
                schedule.setCurrentRunPCB(pcb); // 选取就绪队列的队首 PCB 作为当前执行的PCB
                // 一个时间片内轮转的方法
                try {
                    schedule.poll();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // 一个时间片结束后，打印现在各个队列的进程名称
                schedule.showEveryQueue();

                // 时间轮转结束后，判断 PCB 中的指令是否已经运行完毕了
                if(schedule.getReadyQueue().get(0).getInstructionList() == null){
                    // 如果是，则将该 PCB 放入其他等待队列，并将该 PCB 从后背就绪队列中移除
                    schedule.getOtherWaitQueue();
                    schedule.getBackupReadyQueue().remove(0); // 后备就绪队列中 PCB 的移除
                } else {
                    // 如果没，则将该 PCB 放入后备就绪队列，等待下一个时间片的调度
                    schedule.getBackupReadyQueue().add(pcb); // 当前的 PCB 继续添加到后备就绪队列
                }
            }
        }

        // 暂停调度
        {
            Schedule.FLAG = false;
        }
    }

    @Test
    public void test() {
        Schedule schedule = new Schedule();
        String filePath = "D:\\Workspace\\Java\\Intellij IDEA\\ProcessSchedulingSimulation\\src\\prc.txt";
        File file = new File(filePath);
        schedule.setProcessFile(file);
        schedule.setReadyQueue();
        Schedule.showQueueProcess(schedule.getReadyQueue());
    }
}