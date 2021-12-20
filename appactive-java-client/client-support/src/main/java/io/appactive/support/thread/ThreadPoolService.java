package io.appactive.support.thread;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import io.appactive.support.log.LogUtil;
import io.appactive.support.thread.inner.DefaultThreadFactory;


public class ThreadPoolService {

    private static final ExecutorService SERVICE_LISTENER_THREAD_POOL = createServiceListenerThreadPool();

    /**
     * 获取周期性调用的单线程执行器
     *
     * @param threadName
     * @return
     */
    public static ScheduledExecutorService createSingleThreadScheduledExecutor(String threadName) {
        return new ScheduledThreadPoolExecutor(1, new DefaultThreadFactory(threadName),
            getLoggerRejectedExecutionHandler());
    }

    /**
     * 获取 client main thread
     *
     * @param runnable
     * @return
     */
    public static Thread createClientMainThread(Runnable runnable) {
        return new DefaultThreadFactory("main").newThread(runnable);
    }

    /**
     * 获取服务监听处理线程池
     *
     * @return
     */
    private static ExecutorService createServiceListenerThreadPool() {
        return new ThreadPoolExecutor(1, 3, 30, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(1000), new
            DefaultThreadFactory("service-listener"),
            getLoggerRejectedExecutionHandler());
    }

    private static RejectedExecutionHandler getLoggerRejectedExecutionHandler() {
        return new RejectedExecutionHandler() {
            @Override
            public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                LogUtil.warn("Task rejected, task class: {}", r.getClass());
            }
        };
    }

    public static ExecutorService getServiceListenerThreadPool() {
        return SERVICE_LISTENER_THREAD_POOL;
    }


}
