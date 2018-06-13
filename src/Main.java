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
            Schedule.showQueueProcess(schedule.getReadyQueue());
            while (Schedule.FLAG == true) {
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
                // 一个时间片内执行指令的方法
                try {
                    schedule.runInstruction();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // 时间轮转结束后，判断 PCB 中的指令是否已经运行完毕了
                if(pcb.getInstructionList().size() == 0){
                    schedule.getOtherWaitQueue().add(pcb);
                    schedule.getBackupReadyQueue().remove(pcb);
                } else {
                    schedule.getBackupReadyQueue().remove(pcb);
                    schedule.getBackupReadyQueue().add(pcb);
                }
                // 一个时间片结束后，打印现在各个队列的进程名称
                schedule.showEveryQueue();
            }
            System.out.println("进程调度模拟结束");
            schedule.showEveryQueue();
        }

        // 暂停调度
//        {
//            Schedule.FLAG = false;
//        }
    }
}