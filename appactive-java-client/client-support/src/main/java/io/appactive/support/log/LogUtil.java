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

package io.appactive.support.log;

import ch.qos.logback.classic.Logger;

public class LogUtil {

    /**
     * 根节点的logger
     */
    private static final Logger logger = LoggerBuilder.getLogger("log");

    public static Logger getLogger(){
        return logger;
    }

    public static void info(String format, Object... argArray) {
        logger.info(format, argArray);
    }

    public static void info(String msg, Throwable t) {
        logger.info(msg, t);
    }

    public static void warn(String format, Object... argArray) {
        logger.warn(format, argArray);
    }

    public static void warn(String msg, Throwable t) {
        logger.warn(msg, t);
    }

    public static void trace(String format, Object... argArray) {
        logger.trace(format, argArray);
    }

    public static void trace(String msg, Throwable t) {
        logger.trace(msg, t);
    }

    public static void debug(String format, Object... argArray) {
        logger.debug(format, argArray);
    }

    public static void debug(String msg, Throwable t) {
        logger.debug(msg, t);
    }

    public static void error(String format, Object... argArray) {
        logger.error(format, argArray);
    }

    public static void error(String msg, Throwable t) {
        logger.error(msg, t);
    }

    public static void main(String[] args) {
        logger.debug("test root");
        logger.info("test root");
        logger.info("test 1234");
        logger.warn("test root");
        logger.error("test root");
    }

}
