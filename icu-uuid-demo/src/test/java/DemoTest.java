
import org.junit.Test;
import org.junit.runner.RunWith;
import org.osicu.Application;
import org.osicu.client.IdGenerateClient;
import org.osicu.impl.ThreadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.CountDownLatch;

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
        System.out.println("============cost" + (end - start));
    }

    @Test
    public void concurrencyNextId() throws InterruptedException {
        long start = System.currentTimeMillis();
        int total = 1000000;int threadNum=2;
        CountDownLatch countDownLatch = new CountDownLatch(threadNum);
        for (int i = 0; i < threadNum; i++) {
            ThreadUtil.executeAsync(() -> {
                long l = 0;
                try {
                    while (l < total / threadNum) {
                        long l1 = idGenerateClient.nextId();
                        System.out.println(l1);
                        l++;
                    }
                    countDownLatch.countDown();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        countDownLatch.await();
        long end = System.currentTimeMillis();
        Thread.sleep(1000);
        System.out.println("==========cost " + (end - start));
    }

    @Test
    public void printRange() {
        idGenerateClient.printRange();
    }
}
