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

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import ch.qos.logback.classic.AsyncAppender;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.filter.LevelFilter;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy;
import ch.qos.logback.core.util.FileSize;
import ch.qos.logback.core.util.OptionHelper;
import org.slf4j.LoggerFactory;

import static ch.qos.logback.core.spi.FilterReply.ACCEPT;
import static ch.qos.logback.core.spi.FilterReply.DENY;

public class LoggerBuilder {

    public static Logger getLogger(String name) {
        Logger logger = LOGGER_NAME_OBJ_MAP.get(name);
        if (logger != null) {
            return logger;
        }
        synchronized (LoggerBuilder.class) {
            logger = LOGGER_NAME_OBJ_MAP.get(name);
            if (logger != null) {
                return logger;
            }
            // 变更类加载
            ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
            ClassLoader classLoader = LoggerBuilder.class.getClassLoader();
            try {
                Thread.currentThread().setContextClassLoader(classLoader);
                logger = build(name);
            } catch (Exception e){
                System.out.println("build log fail,e:"+e.getMessage());
                e.printStackTrace();
            }finally {
                Thread.currentThread().setContextClassLoader(contextClassLoader);
            }

            if (logger == null){
                logger = new LoggerContext().getLogger(name);
            }
            LOGGER_NAME_OBJ_MAP.put(name, logger);
        }

        return logger;
    }

    private static final Map<String, Logger> LOGGER_NAME_OBJ_MAP = new HashMap<String, Logger>();

    private static final String LOG_PATH_PROPERTY_KEY = "appactive.log.path";

    private static final String STANDARD_LOG_NAME_END_FIX = "_standard";

    private static final String FILE_LOG_PATTERN = "%d %p %t %c - %m %n";

    private static final String STANDARD_FILE_LOG_PATTERN = "%m %n";

    private static String getLogPath(){
        String logDir = System.getProperty(LOG_PATH_PROPERTY_KEY);
        if (logDir != null && !logDir.isEmpty()) {
            return logDir;
        }
        String agentHome = System.getProperty("user.home");
        File test = new File(agentHome);
        if (test.canRead() && test.canWrite()) {
            logDir = String.format("%s/logs/appactive", agentHome);
            System.setProperty(LOG_PATH_PROPERTY_KEY, logDir);
            return logDir;
        }
        throw new RuntimeException("appactive-sdk log directory init failed.");
    }



    private static Logger build(String name) {
        LoggerContext context = getContext();
        Logger logger = context.getLogger("appactive-" + name);
        //设置不向上级打印信息
        logger.setAdditive(false);
        AsyncAppender infoAppender = getAsyncAppender(getAppender(name,null));
        logger.addAppender(infoAppender);

        logger.addAppender(getConsoleAppender("appactive-console-"+name ,null));
        // 格式化的日志不需要这个错误日志收集
        if(!name.endsWith(STANDARD_LOG_NAME_END_FIX)){
            AsyncAppender errorAppender = getAsyncAppender(getAppender(name + "_error",Level.ERROR));
            logger.addAppender(errorAppender);
        }

        logger.setLevel(Level.INFO);

        return logger;
    }

    private static AsyncAppender getAsyncAppender(Appender appender) {
        LoggerContext context = getContext();
        AsyncAppender asyncAppender = new AsyncAppender();
        asyncAppender.setContext(context);

        //<!-- 不丢失日志.默认的,如果队列的80%已满,则会丢弃TRACT、DEBUG、INFO级别的日志 -->
        //		<discardingThreshold>0</discardingThreshold>
        asyncAppender.setDiscardingThreshold(10);
        //<!-- 更改默认的队列的深度,该值会影响性能.默认值为256 -->
        //<queueSize>512</queueSize>
        asyncAppender.setQueueSize(1024);
        //<neverBlock>true</neverBlock>
        asyncAppender.setNeverBlock(true);
        //<!-- 添加附加的appender,最多只能添加一个 -->
        //<appender-ref ref="supportAppender" />
        asyncAppender.addAppender(appender);
        asyncAppender.start();
        return asyncAppender;
    }

    private static ConsoleAppender getConsoleAppender(String name,Level level) {
        ConsoleAppender appender = new ConsoleAppender();
        appender.setName(name);
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        appender.setContext(context);
        //这里设置级别过滤器
        if(level != null){
            // 这里设置级别过滤器
            LevelFilter levelFilter = getLevelFilter(Level.ERROR);
            levelFilter.start();
            appender.addFilter(levelFilter);
        }

        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        //设置上下文，每个logger都关联到logger上下文，默认上下文名称为default。
        //但可以使用<contextName>设置成其他名字，用于区分不同应用程序的记录。一旦设置，不能修改。
        encoder.setContext(context);
        //设置格式
        encoder.setPattern("%d %p [%t] - %msg%n");
        encoder.start();
        //加入下面两个节点
        appender.setEncoder(encoder);
        appender.start();
        return appender;
    }

        /**
         * 通过传入的名字和级别，动态设置appender
         *
         * @param name
         * @return
         */
    private static RollingFileAppender getAppender(String name, Level level) {
        LoggerContext context = getContext();
        //这里是可以用来设置appender的，在xml配置文件里面，是这种形式：
        // <appender name="error" class="ch.qos.logback.core.rolling.RollingFileAppender">
        RollingFileAppender appender = new RollingFileAppender();


        //设置上下文，每个logger都关联到logger上下文，默认上下文名称为default。
        // 但可以使用<contextName>设置成其他名字，用于区分不同应用程序的记录。一旦设置，不能修改。
        appender.setContext(context);
        //appender的name属性
        appender.setName("FILE-" + name);
        //设置文件名

        String logPath = getLogPath() + "/";
        appender.setFile(OptionHelper.substVars(logPath + name + ".log", context));

        appender.setAppend(true);

        appender.setPrudent(false);

        if(level != null){
            // 这里设置级别过滤器
            LevelFilter levelFilter = getLevelFilter(Level.ERROR);
            levelFilter.start();
            appender.addFilter(levelFilter);
        }

        //设置文件创建时间及大小的类
        SizeAndTimeBasedRollingPolicy policy = new SizeAndTimeBasedRollingPolicy();
        policy.setContext(context);
        //文件名格式
        String fp = OptionHelper.substVars(logPath + "history/%d{yyyy-MM-dd}/" + name + "-%d{yyyy-MM-dd}.%i.log",
            context);
        //最大日志文件大小
        policy.setMaxFileSize(FileSize.valueOf("50MB"));
        //设置文件名模式
        policy.setFileNamePattern(fp);
        //设置最大历史记录为3条
        policy.setMaxHistory(3);
        //总大小限制
        policy.setTotalSizeCap(FileSize.valueOf("2GB"));
        //设置父节点是appender
        policy.setParent(appender);
        //设置上下文，每个logger都关联到logger上下文，默认上下文名称为default。
        // 但可以使用<contextName>设置成其他名字，用于区分不同应用程序的记录。一旦设置，不能修改。
        policy.setContext(context);
        policy.start();

        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        //设置上下文，每个logger都关联到logger上下文，默认上下文名称为default。
        // 但可以使用<contextName>设置成其他名字，用于区分不同应用程序的记录。一旦设置，不能修改。
        encoder.setContext(context);
        //设置格式 格式化日志的logger的输出格式不同
        if(!name.endsWith(STANDARD_LOG_NAME_END_FIX)){
            encoder.setPattern(FILE_LOG_PATTERN);
        }else {
            encoder.setPattern(STANDARD_FILE_LOG_PATTERN);
        }
        encoder.start();

        //加入下面两个节点
        appender.setRollingPolicy(policy);
        appender.setEncoder(encoder);
        appender.start();
        return appender;
    }

    /**
     * 通过level设置过滤器
     *
     * @param level
     * @return
     */
    private static LevelFilter getLevelFilter(Level level) {
        LevelFilter levelFilter = new LevelFilter();
        levelFilter.setLevel(level);
        levelFilter.setOnMatch(ACCEPT);
        levelFilter.setOnMismatch(DENY);
        return levelFilter;
    }


    private static final LoggerContext LOGGER_CONTEXT = new LoggerContext();

    private static LoggerContext getContext(){
        return LOGGER_CONTEXT;
    }

}
