package com.incomm.vms.fileprocess;

import com.incomm.vms.fileprocess.service.FileWatcherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@SpringBootApplication
@ComponentScan("com.incomm.vms.fileprocess")
@EnableAsync
public class FileProcessApplication implements CommandLineRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileProcessApplication.class);

    @Autowired
    private FileWatcherService fileWatcherService;

    public static void main(String[] args) {
        LOGGER.info("ReturnFileApplication is being started");
        SpringApplication.run(FileProcessApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        LOGGER.info("ReturnFileApplication is started and ready to process file");
    }

    @Bean("fileProcessTaskExecutor")
    public TaskExecutor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(100);
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setThreadNamePrefix("file-producer-");
        return executor;
    }
}
