package com.incomm.vms.fileprocess.controllerr;

import com.incomm.vms.fileprocess.model.FileProcessRequest;
import com.incomm.vms.fileprocess.service.FileProcessingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.nio.file.Paths;

@RestController
@RequestMapping(value = "/fileprocessor", produces = {"application/json"})
public class FileProcessController {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileProcessController.class);

    @Autowired
    private FileProcessingService fileProcessingService;

    @PostMapping(path = "/process")
    public ResponseEntity<Object> processFile(@RequestBody @Valid FileProcessRequest fileProcessRequest) {
        LOGGER.info("Received file process request {} for file:{}", fileProcessRequest, fileProcessRequest.getFileName());
        try {
            fileProcessingService.processFile(Paths.get(fileProcessRequest.getFilePath()), fileProcessRequest.getFileName());
            return new ResponseEntity<>("File processing request is accepted for: " + fileProcessRequest.toString(),
                    HttpStatus.ACCEPTED);
        } catch (Exception e) {
            LOGGER.error("Error processing file for file:{}", fileProcessRequest.getFileName(), e);
            return new ResponseEntity<>("Error processing request with exception: " + e.getLocalizedMessage(),
                    HttpStatus.NOT_ACCEPTABLE);
        }

    }
}
