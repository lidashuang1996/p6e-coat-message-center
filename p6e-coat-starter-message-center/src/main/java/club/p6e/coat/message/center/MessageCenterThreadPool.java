package club.p6e.coat.message.center;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author lidashuang
 * @version 1.0
 */
@Component
@ConditionalOnMissingBean(
        value = MessageCenterThreadPool.class,
        ignored = MessageCenterThreadPool.class
)
public class MessageCenterThreadPool {

    private ThreadPoolExecutor executor = new ThreadPoolExecutor(
            5,
            Integer.MAX_VALUE,
            60L,
            TimeUnit.SECONDS,
            new SynchronousQueue<>()
    );

    public void submit(Runnable runnable) {
        executor.submit(runnable);
    }

    public void setThreadPoolExecutor(ThreadPoolExecutor threadPoolExecutor) {
        executor = threadPoolExecutor;
    }

}
