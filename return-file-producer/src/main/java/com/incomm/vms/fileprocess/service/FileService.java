package com.incomm.vms.fileprocess.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.incomm.vms.fileprocess.config.Constants.FILE_SEPARATOR;

@Service
public class FileService {
    private final static Logger LOGGER = LoggerFactory.getLogger(FileService.class);

    @Value("${vms.printer-ack.delelete.folder}")
    private String deleteFolder;

    public Path moveFileToProcessingFolder(Path filePath) throws ParseException, IOException {
        LOGGER.info("Moving file:{}", filePath.toString());
        String destinationFolder = getDestinationFolder();
        File processingFolder = new File(deleteFolder + System.getProperty(FILE_SEPARATOR) + destinationFolder);
        LOGGER.info("Moving file:{} to processing folder:{}", filePath.toString(), processingFolder);
        if (!processingFolder.isDirectory()) {
            processingFolder.mkdirs();
        }
        Path newFile = Paths.get(processingFolder + System.getProperty(FILE_SEPARATOR) + filePath.getFileName().toString());
        Files.move(filePath, newFile, StandardCopyOption.REPLACE_EXISTING);
        LOGGER.info("file:{} is moved and ready for processing", newFile);

        // TODO
        // rejected file logic

        return newFile;
    }

    private String getDestinationFolder() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String lsDate = sdf.format(new java.sql.Date(new Date().getTime()));
        SimpleDateFormat txnDateFormat = new SimpleDateFormat("yyyyMMdd");
        txnDateFormat.setLenient(false);

        SimpleDateFormat tranDateFormat = new SimpleDateFormat("MMddyyyy");
        tranDateFormat.setLenient(false);
        return tranDateFormat.format(txnDateFormat.parse(lsDate));
    }

}
