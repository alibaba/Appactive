/*
 * Copyright 1999-2022 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
