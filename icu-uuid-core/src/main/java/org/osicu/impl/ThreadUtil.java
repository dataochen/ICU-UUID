package org.osicu.impl;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.Objects;
import java.util.concurrent.*;

/**
 * @author chendatao
 */
public class ThreadUtil {
    private static ThreadPoolExecutor threadPoolExecutor;

    private ThreadUtil() {
    }

    public static ThreadPoolExecutor newSingletonInstance() {
        if (Objects.isNull(threadPoolExecutor)) {
            synchronized (ThreadUtil.class) {
                if (Objects.isNull(threadPoolExecutor)) {
                    ThreadFactory build = new ThreadFactoryBuilder().setNameFormat("osIcuThreadPool-%d").build();
                    threadPoolExecutor = new ThreadPoolExecutor(10, 10, 3000, TimeUnit.MICROSECONDS,
                            new ArrayBlockingQueue<>(1024), build, (r, executor) -> {
                        throw new RejectedExecutionException("osIcu Id线程池饱满，已拒绝新线程，请留意下系统性能 " + r.toString());
                    });
                }
            }
        }
        return threadPoolExecutor;
    }

    public static void executeAsync(Runnable runnable) {
        ThreadPoolExecutor threadPoolExecutor = newSingletonInstance();
        threadPoolExecutor.execute(runnable);
    }

}
