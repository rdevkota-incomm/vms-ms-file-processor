package com.incomm.vms.fileprocess.service;

import com.incomm.vms.fileprocess.model.RejectReasonMaster;
import com.incomm.vms.fileprocess.model.ReturnFileDTO;
import com.incomm.vms.fileprocess.model.ReturnFileErrorData;
import com.incomm.vms.fileprocess.repository.FileProcessReasonRepository;
import com.incomm.vms.fileprocess.repository.ReturnFileErrorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.incomm.vms.fileprocess.config.Constants.SERIAL_NUMBER_NOT_FOUND;

@Service
public class ErrorProcessingService {

    @Autowired
    private FileProcessReasonRepository fileProcessReasonRepository;
    @Autowired
    private ReturnFileErrorRepository returnFileErrorRepository;

    public void processError(ReturnFileDTO returnFileRecord, String fileName, String errorMessage) {
        RejectReasonMaster fileProcessReason = fileProcessReasonRepository.findByRejectReason(errorMessage);

        ReturnFileErrorData returnFileErrorData = new ReturnFileErrorData();
        returnFileErrorData.setFileName(fileName);
        returnFileErrorData.setParentOrderId(returnFileRecord.getParentOrderId());
        returnFileErrorData.setChildOrderId(returnFileRecord.getChildOrderId());
        returnFileErrorData.setSerialNumber(returnFileRecord.getSerialNumber());
        returnFileErrorData.setPan("");
        returnFileErrorData.setRejectCode(fileProcessReason.getRejectCode());
        returnFileErrorData.setRejectReason(fileProcessReason.getRejectReason());
        returnFileErrorData.setErrorMessage(errorMessage);

        returnFileErrorRepository.save(returnFileErrorData);
    }

    public void processSerilNumberNotFoundError(ReturnFileDTO returnFileRecord, String fileName) {
        ReturnFileErrorData returnFileErrorData = new ReturnFileErrorData();
        returnFileErrorData.setFileName(fileName);
        returnFileErrorData.setParentOrderId(returnFileRecord.getParentOrderId());
        returnFileErrorData.setChildOrderId(returnFileRecord.getChildOrderId());
        returnFileErrorData.setSerialNumber(returnFileRecord.getSerialNumber());
        returnFileErrorData.setPan("");
        returnFileErrorData.setRejectReason("Rejected Reason - " + SERIAL_NUMBER_NOT_FOUND);

        returnFileErrorRepository.save(returnFileErrorData);
    }
}
