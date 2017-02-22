package cn.gdgst.palmtest.imagecache;

import com.orhanobut.logger.Logger;

import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 功能描述：网络请求线程池类
 * @author android_ls
 */
public class DefaultThreadPool {
    /**
     * 用于保存等待执行的任务的阻塞队列。(有序的先进先出阻塞队列)
     */
    private static ArrayBlockingQueue<Runnable> mBlockingQueue = new ArrayBlockingQueue<Runnable>(15, true);

    /**
     * 线程池
     */
    private static AbstractExecutorService mThreadPoolExecutor
            = new ThreadPoolExecutor(5, 7, 10, TimeUnit.SECONDS, mBlockingQueue,
            new ThreadPoolExecutor.DiscardOldestPolicy());

    private static DefaultThreadPool instance = null;

    public static DefaultThreadPool getInstance() {
        if (instance == null) {
            instance = new DefaultThreadPool();
        }
        return instance;
    }

    /**
     * 执行任务
     * @param r
     */
    public void execute(Runnable r) {
        mThreadPoolExecutor.execute(r);
    }

    /**
     * 关闭，并等待任务执行完成，不接受新任务
     */
    public static void shutdown() {
        if (mThreadPoolExecutor != null) {
            mThreadPoolExecutor.shutdown();
            Logger.i(DefaultThreadPool.class.getName()+"DefaultThreadPool shutdown");
        }
    }

    /**
     * 关闭，立即关闭，并挂起所有正在执行的线程，不接受新任务
     */
    public static void shutdownRightnow() {
        if (mThreadPoolExecutor != null) {
            mThreadPoolExecutor.shutdownNow();
            try {
                // 设置超时极短，强制关闭所有任务
                mThreadPoolExecutor.awaitTermination(1, TimeUnit.MICROSECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Logger.i(DefaultThreadPool.class.getName()+ "DefaultThreadPool shutdownRightnow");
        }
    }

}
