package com.incomm.vms.fileprocess.service;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.incomm.vms.fileprocess.exception.FileValidationException;
import com.incomm.vms.fileprocess.model.ReturnFileDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.incomm.vms.fileprocess.config.Constants.*;

@Service
public class FileProcessingService {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileProcessingService.class);

    @Autowired
    private ProducerService producerService;
    @Autowired
    private ValidationService validationService;
    @Autowired
    private FileService fileService;

    @Async("fileProcessTaskExecutor")
    public void processFile(Path filePath, String fileName) {
        Path file = Paths.get(filePath + System.getProperty(FILE_SEPARATOR) + fileName);
        try {
            validationService.validateFile(file);
            moveAndParseFile(file);
        } catch (FileValidationException e) {
            LOGGER.error("File validation failed for file:{}", file, e);
        }
    }

    private void moveAndParseFile(Path file) {
        try {
            Path newFile = fileService.moveFileToProcessingFolder(file);
            parseCsvFile(newFile);
        } catch (Exception e) {
            LOGGER.error("Error processing the file:{}", file, e);
        }
    }

    private void parseCsvFile(Path file) throws IOException {
        LOGGER.info("file:{} is being processed", file);
        long start = System.currentTimeMillis();
        CsvMapper csvMapper = new CsvMapper();
        CsvSchema schema = CsvSchema.emptySchema().withHeader();
        ObjectReader oReader = csvMapper.readerFor(ReturnFileDTO.class).with(schema);
        UUID correlationId = UUID.randomUUID();
        int recordCount = 0;
        try (Reader reader = new FileReader(String.valueOf(file))) {
            MappingIterator<ReturnFileDTO> mi = oReader.readValues(reader);
            while (mi.hasNext()) {
                ReturnFileDTO returnFileDTO = mi.next();
                recordCount++;
                produceRecord(returnFileDTO, correlationId, recordCount, file);
            }
        }
        LOGGER.info("Done processing File {} recordCount:{} in {} MilliSec with CorrelationId:{} ",
                file, recordCount, (System.currentTimeMillis() - start), correlationId);

    }

    private void produceRecord(ReturnFileDTO returnFileData, UUID correlationId, int recordCount, Path file) {
        Map<String, String> headers = new HashMap<>();
        headers.put(FILE_NAME, file.getFileName().toString());
        headers.put(RECORD_NUMBER, String.valueOf(recordCount));
        headers.put(CORRELATION_ID, correlationId.toString());
        returnFileData.setHeaders(headers);

        LOGGER.debug("Producing message with headers {}", headers);
        producerService.produceMessage(returnFileData);
    }
}
