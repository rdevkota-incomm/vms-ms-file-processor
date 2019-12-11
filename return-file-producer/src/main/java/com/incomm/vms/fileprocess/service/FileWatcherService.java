package com.incomm.vms.fileprocess.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

@Service
public class FileWatcherService {
    private final static Logger LOGGER = LoggerFactory.getLogger(FileWatcherService.class);

    @Autowired
    private FileProcessingService fileProcessingService;

    @Value("${vms.printer-ack.monitor.folder}")
    private String monitorFolder;

//    @Autowired
//    public FileWatcherService(FileProcessingService fileProcessingService) {
//        this.fileProcessingService = fileProcessingService;
//    }

    public void monitorFolder() throws IOException, InterruptedException {
        LOGGER.info("File is being monitored under {} folder", monitorFolder);
        WatchService watchService = FileSystems.getDefault().newWatchService();
        Path filePath = Paths.get(monitorFolder);

        filePath.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);

        WatchKey key;
        while ((key = watchService.take()) != null) {
            for (WatchEvent<?> event : key.pollEvents()) {
                LOGGER.info("New File has been received {}", event.context().toString());
                try {
                    fileProcessingService.parseCsvFile(filePath, event.context().toString());
                } catch(Exception e) {
                    LOGGER.error("Issue processing file {}", e.getLocalizedMessage(), e);
                }
            }
            key.reset();
        }

    }
}
