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

    @Test
    public void test04() {
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("1");
        arrayList.add("2");
        arrayList.add("3");
        System.out.println(arrayList.toString());
        ArrayList<String> tempList = (ArrayList<String>) arrayList.subList(1,3);
        System.out.println(arrayList.toString());
    }
}
