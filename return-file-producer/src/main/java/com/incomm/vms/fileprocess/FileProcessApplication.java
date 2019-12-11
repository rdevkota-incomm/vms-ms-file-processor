package com.incomm.vms.fileprocess;

import com.incomm.vms.fileprocess.service.FileWatcherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@ComponentScan("com.incomm.vms.fileprocess")
@EnableTransactionManagement
@EnableJdbcRepositories(basePackages = "com.incomm.vms.fileprocess.repository")
@EnableScheduling
public class FileProcessApplication implements CommandLineRunner {
    private static Logger LOGGER = LoggerFactory.getLogger(FileProcessApplication.class);
    @Autowired
    private FileWatcherService fileWatcherService;

    public static void main(String[] args) {
        LOGGER.info("ReturnFileApplication is being started");
        SpringApplication.run(FileProcessApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        LOGGER.info("ReturnFileApplication is started and folder is being monitored");
        fileWatcherService.monitorFolder();
    }
}
