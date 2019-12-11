package com.incomm.vms.fileprocess.service;

import com.incomm.vms.fileprocess.model.LineItemDetail;
import com.incomm.vms.fileprocess.model.RejectReasonMaster;
import com.incomm.vms.fileprocess.model.ReturnFileDTO;
import com.incomm.vms.fileprocess.repository.CardIssuanceStatusRepository;
import com.incomm.vms.fileprocess.repository.DeleteCardRepository;
import com.incomm.vms.fileprocess.repository.FileProcessReasonRepository;
import com.incomm.vms.fileprocess.repository.LineItemDetailRepository;
import com.incomm.vms.fileprocess.repository.OrderDetailRepository;
import com.incomm.vms.fileprocess.repository.OrderLineItemRepository;
import com.incomm.vms.fileprocess.repository.ReturnFileDataRepository;
import com.incomm.vms.fileprocess.repository.UploadDetailRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

import static com.incomm.vms.fileprocess.config.Constants.*;

@Service
public class DataProcessingService {
    private final static Logger LOGGER = LoggerFactory.getLogger(DataProcessingService.class);
    public static final String SUCCESS_FLAG = "Y";

    @Autowired
    private ErrorProcessingService errorProcessingService;
    @Autowired
    private FileAggregationService fileAggregationService;
    @Autowired
    private UploadDetailRepository uploadDetailRepository;
    @Autowired
    private LineItemDetailRepository lineItemDetailRepository;
    @Autowired
    private FileProcessReasonRepository fileProcessReasonRepository;
    @Autowired
    private CardIssuanceStatusRepository cardIssuanceStatusRepository;
    @Autowired
    private ReturnFileDataRepository returnFileDataRepository;
    @Autowired
    private OrderLineItemRepository orderLineItemRepository;
    @Autowired
    private OrderDetailRepository orderDetailRepository;
    @Autowired
    private DeleteCardRepository deleteCardRepository;

    @Value("${vms.instance-code}")
    private String instanceCode;

    public void processRecords(ReturnFileDTO returnFileRecord) {
        Map<String, String> messageHeaders = returnFileRecord.getHeaders();
        String fileName = messageHeaders.get(FILE_NAME);
        String correlationId = messageHeaders.get(CORRELATION_ID);
        String recordNumber = messageHeaders.get(RECORD_NUMBER);

        LOGGER.info("Processing record for file: {}  correlationId {}", fileName, correlationId);

        Optional<LineItemDetail> lineItemDetail = lineItemDetailRepository.findLineItem(instanceCode, returnFileRecord.getSerialNumber());

        if (!lineItemDetail.isPresent()) {
            errorProcessingService.processSerilNumberNotFoundError(returnFileRecord, fileName);
            fileAggregationService.addConsumerCountForFailedRecord(messageHeaders);
        } else {
            String panCode = lineItemDetail.get().getPanCode();
            RejectReasonMaster fileProcessReason = fileProcessReasonRepository.findByRejectReason(returnFileRecord.getRejectReason());
            lineItemDetailRepository.update(returnFileRecord.getSerialNumber(), panCode, fileProcessReason);

            if (SUCCESS_FLAG.equalsIgnoreCase(fileProcessReason.getSuccessFailureFlag())) {
                cardIssuanceStatusRepository.update(instanceCode, panCode);
            }

            returnFileDataRepository.save(instanceCode, fileName, recordNumber, returnFileRecord, lineItemDetail.get());

            boolean deletePanCode = isDeletePanCode(lineItemDetail, fileProcessReason);
            fileAggregationService.saveConsumedDetail(panCode, deletePanCode, messageHeaders);
        }
        LOGGER.info("Done processing message filename: {} recordNumber: {} correlationId: {} ", fileName, recordNumber, correlationId);
    }

    private boolean isDeletePanCode(Optional<LineItemDetail> lineItemDetail, RejectReasonMaster fileProcessReason) {
        if (!SUCCESS_FLAG.equalsIgnoreCase(fileProcessReason.getSuccessFailureFlag()) &&
                !lineItemDetail.get().getPartnerId().equalsIgnoreCase("Replace_Partner_ID")) {
            return true;
        }
        return false;
    }
}
