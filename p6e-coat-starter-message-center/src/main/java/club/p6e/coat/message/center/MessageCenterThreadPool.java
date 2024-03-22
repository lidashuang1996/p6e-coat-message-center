package club.p6e.coat.message.center;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import java.util.concurrent.LinkedBlockingQueue;
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

    private ThreadPoolExecutor threadPool;

    public MessageCenterThreadPool() {
        this.threadPool = new ThreadPoolExecutor(
                5,
                Integer.MAX_VALUE,
                60L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>()
        );
    }

    public void submit(Runnable runnable) {
        this.threadPool.submit(runnable);
    }

    public synchronized void setThreadPool(ThreadPoolExecutor threadPool) {
        closeThreadPool();
        this.threadPool = threadPool;
    }

    public void closeThreadPool() {
        if (threadPool != null) {
            threadPool.shutdown();
            threadPool = null;
        }
    }

}
