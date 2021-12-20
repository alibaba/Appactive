package io.appactive.support.thread;

import io.appactive.support.log.LogUtil;


public class SafeWrappers {

    /**
     * 封装 runnable，防止异常不被捕获
     *
     * @param runnable
     * @return
     */
    public static Runnable safeRunnable(final Runnable runnable) {
        return new Runnable() {
            @Override
            public void run() {
                try {
                    runnable.run();
                } catch (Throwable e) {
                    LogUtil.error("SafeWrappers-safeRunnable-error"+ e.getMessage(),e);

                }
            }
        };
    }
}
