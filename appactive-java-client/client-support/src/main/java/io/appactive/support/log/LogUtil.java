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
