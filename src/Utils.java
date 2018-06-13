import javax.swing.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Utils {
    public static String DateToStr(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        return format.format(date);
    }

    public static void updateJList(DefaultListModel model, ArrayList<PCB> list) {
        model.removeAllElements();
        for(int i = 0, size = list.size(); i < size; i++){
            model.addElement(list.get(i).getProcessName());
        }
    }

    public static void updateBackupJList(DefaultListModel model, ArrayList<PCB> list) {
        model.removeAllElements();
        if(list.size() > 0) {
            for(int i = 1, size = list.size(); i < size; i++){
                model.addElement(list.get(i).getProcessName());
            }
        }

    }
}
