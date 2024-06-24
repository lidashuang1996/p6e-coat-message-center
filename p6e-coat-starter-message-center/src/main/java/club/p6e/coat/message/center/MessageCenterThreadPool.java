package club.p6e.coat.message.center;

import org.springframework.stereotype.Component;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 消息中心线程池配置
 *
 * @author lidashuang
 * @version 1.0
 */
@Component
public class MessageCenterThreadPool {

    /**
     * 线程池对象
     */
    private ThreadPoolExecutor threadPool;

    /**
     * 构造方法初始化线程池对象
     */
    public MessageCenterThreadPool() {
        this.threadPool = new ThreadPoolExecutor(
                5,
                Integer.MAX_VALUE,
                60L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>()
        );
    }

    /**
     * 提交任务到线程池
     *
     * @param runnable 任务对象
     */
    public void submit(Runnable runnable) {
        this.threadPool.submit(runnable);
    }

    /**
     * 设置线程池对象
     *
     * @param threadPool 线程池对象
     */
    @SuppressWarnings("ALL")
    public synchronized void setThreadPool(ThreadPoolExecutor threadPool) {
        closeThreadPool();
        this.threadPool = threadPool;
    }

    /**
     * 关闭线程池对象
     */
    public void closeThreadPool() {
        if (threadPool != null) {
            threadPool.shutdown();
            threadPool = null;
        }
    }

}
