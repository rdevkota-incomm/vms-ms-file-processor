package com.incomm.vms.fileprocess.service;

import com.incomm.vms.fileprocess.model.LineItemDetail;
import com.incomm.vms.fileprocess.model.RejectReasonMaster;
import com.incomm.vms.fileprocess.model.ReturnFileDTO;
import com.incomm.vms.fileprocess.repository.CardIssuanceStatusRepository;
import com.incomm.vms.fileprocess.repository.DeleteCardRepository;
import com.incomm.vms.fileprocess.repository.FileProcessReasonRepository;
import com.incomm.vms.fileprocess.repository.LineItemDetailRepository;
import com.incomm.vms.fileprocess.repository.ReturnFileDataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

import static com.incomm.vms.fileprocess.config.Constants.*;

@Service
public class MessageProcessingService {
    private final static Logger LOGGER = LoggerFactory.getLogger(MessageProcessingService.class);
    private static final String SUCCESS_FLAG = "Y";

    @Autowired
    private ErrorProcessingService errorProcessingService;
    @Autowired
    private OrderAggregationService fileAggregationService;
    @Autowired
    private LineItemDetailRepository lineItemDetailRepository;
    @Autowired
    private FileProcessReasonRepository fileProcessReasonRepository;
    @Autowired
    private CardIssuanceStatusRepository cardIssuanceStatusRepository;
    @Autowired
    private ReturnFileDataRepository returnFileDataRepository;
    @Autowired
    private DeleteCardRepository deleteCardRepository;

    @Value("${vms.instance-code}")
    private String instanceCode;

    protected void processMessage(ReturnFileDTO returnFilePayload) {
        Map<String, String> messageHeaders = returnFilePayload.getHeaders();
        String fileName = messageHeaders.get(FILE_NAME);
        String correlationId = messageHeaders.get(CORRELATION_ID);
        String recordNumber = messageHeaders.get(RECORD_NUMBER);

        String serialNumber = returnFilePayload.getSerialNumber();

        Optional<LineItemDetail> lineItemDetail = lineItemDetailRepository.findLineItem(instanceCode, serialNumber);
        LOGGER.info("Retrieved LineItemDetail:{} for serialNumber:{} file:{}  correlationId:{}", lineItemDetail.toString(),
                serialNumber, fileName, correlationId);

        if (!lineItemDetail.isPresent()) {
            errorProcessingService.processSerialNumberNotFoundError(returnFilePayload, fileName);

            LOGGER.error("Error retrieving LineItemDetail for serialNumber:{} file: {}  correlationId:{}", serialNumber,
                    fileName, correlationId);
        } else {
            String panCode = lineItemDetail.get().getPanCode();

            RejectReasonMaster fileProcessReason = fileProcessReasonRepository.findByRejectReason(returnFilePayload.getRejectReason());

            LOGGER.info("Updating LineItemDetail with serialNumber:{} panCode:{} fileProcessReason:{} file:{}  correlationId:{}",
                    serialNumber, panCode, fileProcessReason.toString(), fileName, correlationId);

            lineItemDetailRepository.updateStatus(serialNumber, panCode, fileProcessReason);
            if (SUCCESS_FLAG.equalsIgnoreCase(fileProcessReason.getSuccessFailureFlag())) {
                LOGGER.info("Updating Card Status panCode:{} file:{}  correlationId:{}", panCode, fileName, correlationId);
                cardIssuanceStatusRepository.updateCardStatus(instanceCode, panCode);
            }

            LOGGER.info("Creating record in vms_returnfile_data table recordNumber:{} file:{}  correlationId:{}",
                    recordNumber, fileName, correlationId);

            returnFileDataRepository.createRecord(instanceCode, fileName, recordNumber, returnFilePayload, lineItemDetail.get());

            if (isDeleteRequired(lineItemDetail.get(), fileProcessReason)) {
                LOGGER.info("Deleting card for panCode:{} file:{}  correlationId:{}", panCode, fileName, correlationId);
                deleteCardRepository.deleteCard(lineItemDetail.get().getPanCode());
            }

            fileAggregationService.completeProcessing(lineItemDetail.get(), fileName, correlationId);
        }
        LOGGER.info("Done processing message for recordNumber:{} file:{} correlationId:{} ", recordNumber, fileName, correlationId);
    }

    private boolean isDeleteRequired(LineItemDetail lineItemDetail, RejectReasonMaster fileProcessReason) {
        if (!SUCCESS_FLAG.equalsIgnoreCase(fileProcessReason.getSuccessFailureFlag()) &&
                !lineItemDetail.getPartnerId().equalsIgnoreCase("Replace_Partner_ID")) {
            return true;
        }
        return false;
    }
}
