import java.util.ArrayList;
import java.util.List;

/**
 * 进程控制块类
 */
public class PCB {
    private String processName; // 进程名称
    private ArrayList<Instruction> instructionList; // 进程需要运行的指令链表
    private int currentInstructionId; // 当前进程正在运行的指令下标

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public List<Instruction> getInstructionList() {
        return instructionList;
    }

    public void setInstructionList(ArrayList<Instruction> instructionList) {
        this.instructionList = instructionList;
    }

    public int getCurrentInstructionId() {
        return currentInstructionId;
    }

    public void setCurrentInstructionId(int currentInstructionId) {
        this.currentInstructionId = currentInstructionId;
    }
}