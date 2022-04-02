package io.renren.modules.app.config;

import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author hxx
 * @date 2022/4/2 12:12
 */
@Setter
@EnableAsync
@Configuration
@ConfigurationProperties("async-thread-pool")
public class SpringAsyncConfig implements AsyncConfigurer {
    public static Logger log = LoggerFactory.getLogger(SpringAsyncConfig.class);
    /**核心线程数*/
    private int corePoolSize;
    /**最大线程数*/
    private int maximumPoolSize;
    /**线程空闲时间*/
    private int keepAliveTime;
    /**阻塞队列容量*/
    private int queueCapacity;
    /**构建线程工厂*/
    private ThreadFactory threadFactory=new ThreadFactory() {
        //CAS算法
        private final AtomicInteger at=new AtomicInteger(maximumPoolSize);
        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r,
                    "async-thread-"+at.getAndIncrement());
        }
    };
    @Override
    @Bean("customizedApplicationTaskExecutor")
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maximumPoolSize);
        executor.setKeepAliveSeconds(keepAliveTime);
        executor.setQueueCapacity(queueCapacity);
        executor.setRejectedExecutionHandler((Runnable r,
                                              ThreadPoolExecutor exe) -> log.warn("当前任务线程池队列已满."));
        executor.initialize();
        return executor;
    }

    @Override
    public AsyncUncaughtExceptionHandler
    getAsyncUncaughtExceptionHandler() {
        return (ex, method, params) -> log.error("线程池执行任务发生未知异常.", ex);
    }}



