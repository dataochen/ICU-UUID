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
        for (int i = 0; i < 10; i++) {
            String s = idGenerateClient.nextId();
            System.out.println(s);
        }
    }
}
