/**
 * 指令类
 */
public class Instruction {
    private char name; // 指令名称
    private int runTime; // 单个指令需要运行的时间
    private int remainTime; // 单个指令剩余需要运行的时间

    public Instruction(char name, int runTime, int remainTime) {
        this.name = name;
        this.runTime = runTime;
        this.remainTime = remainTime;
    }

    public char getName() {
        return name;
    }

    public void setName(char name) {
        this.name = name;
    }

    public int getRunTime() {
        return runTime;
    }

    public void setRunTime(int runTime) {
        this.runTime = runTime;
    }

    public int getRemainTime() {
        return remainTime;
    }

    public void setRemainTime(int remainTime) {
        this.remainTime = remainTime;
    }
}