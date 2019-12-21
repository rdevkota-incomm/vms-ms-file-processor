package com.incomm.vms.fileprocess.service;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
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
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.incomm.vms.fileprocess.config.Constants.*;

@Service
public class FileProcessingService {
    private final static Logger LOGGER = LoggerFactory.getLogger(FileProcessingService.class);

    @Autowired
    private ProducerService producerService;

    @Async("fileProcessTaskExecutor")
    public void parseCsvFile(Path filePath, String fileName) throws IOException {
        LOGGER.info("file:{} is being processed in path:{}", fileName, filePath);
        long start = System.currentTimeMillis();
        CsvMapper csvMapper = new CsvMapper();
        CsvSchema schema = CsvSchema.emptySchema().withHeader();
        ObjectReader oReader = csvMapper.reader(ReturnFileDTO.class).with(schema);
        UUID correlationId = UUID.randomUUID();
        int totalRecordCount = 0;
        try (Reader reader = new FileReader(filePath.toFile() + "/" + fileName)) {
            MappingIterator<ReturnFileDTO> mi = oReader.readValues(reader);
            while (mi.hasNext()) {
                ReturnFileDTO returnFileDTO = mi.next();
                totalRecordCount++;
                produceRecord(returnFileDTO, correlationId, totalRecordCount, fileName);
            }
        }
        LOGGER.info("Done processing File {} in path {} totalRecordCount:{} in {} with CorrelationId:{} file:{}",
                fileName, filePath, totalRecordCount, (System.currentTimeMillis() - start) + " MilliSec ", correlationId, fileName);

    }

    private void produceRecord(ReturnFileDTO returnFileData, UUID correlationId, int recordCount, String fileName) {
        Map<String, String> headers = new HashMap<>();
        headers.put(FILE_NAME, fileName);
        headers.put(RECORD_NUMBER, String.valueOf(recordCount));
        headers.put(CORRELATION_ID, correlationId.toString());
        returnFileData.setHeaders(headers);

        LOGGER.debug("Producing message with headers {}", headers);
        producerService.produceMessage(returnFileData);
    }
}
