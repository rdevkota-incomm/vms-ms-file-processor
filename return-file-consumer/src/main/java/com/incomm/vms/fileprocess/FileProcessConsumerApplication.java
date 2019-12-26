package com.incomm.vms.fileprocess;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import static com.incomm.vms.fileprocess.config.Constants.POST_BACK_TASK_EXECUTOR_POOL;

@SpringBootApplication
@ComponentScan("com.incomm.vms.fileprocess")
@EnableTransactionManagement
@EnableJdbcRepositories(basePackages = "com.incomm.vms.fileprocess.repository")
@EnableCaching
@EnableScheduling
public class FileProcessConsumerApplication {
    private static Logger LOGGER = LoggerFactory.getLogger(FileProcessConsumerApplication.class);

    public static void main(String[] args) {
        LOGGER.info("FileProcessConsumerApplication is being started");
        SpringApplication.run(FileProcessConsumerApplication.class, args);
    }

    @Bean(POST_BACK_TASK_EXECUTOR_POOL)
    public TaskExecutor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(100);
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setThreadNamePrefix("post-back-");
        return executor;
    }

}
