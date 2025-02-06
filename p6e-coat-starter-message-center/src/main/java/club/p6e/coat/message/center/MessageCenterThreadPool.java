package club.p6e.coat.message.center;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * MessageCenterThreadPool
 *
 * @author lidashuang
 * @version 1.0
 */
@Component
@ConditionalOnMissingBean(
        value = MessageCenterThreadPool.class,
        ignored = MessageCenterThreadPool.class
)
public class MessageCenterThreadPool {

    /**
     * Thread Pool Executor Object
     */
    private ThreadPoolExecutor executor;

    /**
     * Construct Initialization
     */
    public MessageCenterThreadPool() {
        setThreadPool(new ThreadPoolExecutor(
                5,
                Integer.MAX_VALUE,
                60L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>()
        ));
    }

    /**
     * Submit Task To Thread Pool
     *
     * @param runnable Runnable Object
     */
    public void submit(Runnable runnable) {
        this.executor.submit(runnable);
    }

    /**
     * Set Thread Pool
     *
     * @param executor Thread Pool
     */
    public synchronized void setThreadPool(ThreadPoolExecutor executor) {
        closeThreadPool();
        this.executor = executor;
    }

    /**
     * Close Thread Pool
     */
    public void closeThreadPool() {
        if (this.executor != null) {
            this.executor.shutdown();
            this.executor = null;
        }
    }

}
