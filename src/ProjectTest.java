import org.junit.Test;

import java.io.File;
import java.util.ArrayList;

public class ProjectTest {
    @Test
    public void test01() {
        char c = 'a';
        if(c == 'a') {
            System.out.println(true);
        } else {
            System.out.println(false);
        }
    }

    @Test
    public void test02() {
        ArrayList<String> strList = new ArrayList<>();
        strList.add("1");
        strList.add("2");
        strList.add("3");
        strList.add("4");
        System.out.println(strList.toString());
        strList.remove(0);
        System.out.println(strList.toString());
    }


    @Test
    public void test03() {
        File file = new File("D:\\Workspace\\Java\\Intellij IDEA\\ProcessSchedulingSimulation\\src\\prc.txt");
        System.out.println(file.getParent());
    }
}
