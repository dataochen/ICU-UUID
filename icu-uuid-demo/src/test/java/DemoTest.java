
import org.junit.Test;
import org.junit.runner.RunWith;
import org.osicu.Application;
import org.osicu.client.IdGenerateClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author dataochen
 * @Description
 * @date: 2021/2/7 18:05
 */

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class DemoTest {
    @Autowired
    private IdGenerateClient idGenerateClient;

    @Test
    public void nextId() throws Exception {
        long start = System.currentTimeMillis();
        for (int i = 0; i < 100; i++) {
            long l = idGenerateClient.nextId();
            System.out.println(l);
        }
        long end = System.currentTimeMillis();
        System.out.println("============cost"+(end-start));
    }

    @Test
    public void printRange() {
        idGenerateClient.printRange();
    }
}
