import org.junit.Test;

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
}
