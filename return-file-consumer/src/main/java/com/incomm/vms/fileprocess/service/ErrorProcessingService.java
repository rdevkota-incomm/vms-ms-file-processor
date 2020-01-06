package com.incomm.vms.fileprocess.service;

import com.incomm.vms.fileprocess.model.ReturnFileDTO;
import com.incomm.vms.fileprocess.model.ReturnFileErrorData;
import com.incomm.vms.fileprocess.repository.ReturnFileErrorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.incomm.vms.fileprocess.config.Constants.SERIAL_NUMBER_NOT_FOUND;

@Service
public class ErrorProcessingService {

    @Autowired
    private ReturnFileErrorRepository returnFileErrorRepository;

    public void processSerialNumberNotFoundError(ReturnFileDTO returnFileRecord, String fileName) {
        ReturnFileErrorData returnFileErrorData = ReturnFileErrorData.builder()
                .fileName(fileName)
                .parentOrderId(returnFileRecord.getParentOrderId())
                .childOrderId(returnFileRecord.getChildOrderId())
                .serialNumber(returnFileRecord.getSerialNumber())
                .pan("")
                .rejectReason("Rejected Reason - " + SERIAL_NUMBER_NOT_FOUND)
                .build();

        returnFileErrorRepository.save(returnFileErrorData);
    }
}
