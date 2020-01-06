package com.incomm.vms.fileprocess.service;

import com.incomm.vms.fileprocess.exception.FileValidationException;
import org.springframework.stereotype.Service;

import java.nio.file.Path;

@Service
public class ValidationService {

    public void validateFile(Path file) throws FileValidationException {
        validateFileLength(file);
        checkDuplicateFile(file);
    }

    private void checkDuplicateFile(Path file) throws FileValidationException {
        // this has to go to the database to check for duplicate file
        // is it necessary
    }

    private void validateFileLength(Path file) throws FileValidationException {
        if (file.getFileName().toString().length() > 200) {
            throw new FileValidationException("Filename length  than 200 but the size is "
                    + file.getFileName().toString().length());
        }
    }
}
