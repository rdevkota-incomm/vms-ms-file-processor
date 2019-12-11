package com.incomm.vms.fileprocess.service;

import com.incomm.vms.fileprocess.cache.FileAggregateSummaryStore;
import com.incomm.vms.fileprocess.model.FileAggregateDTO;
import com.incomm.vms.fileprocess.model.FileAggregateSummary;
import com.incomm.vms.fileprocess.model.OrderDetailAggregate;
import com.incomm.vms.fileprocess.model.OrderDetailCount;
import com.incomm.vms.fileprocess.repository.DeleteCardRepository;
import com.incomm.vms.fileprocess.repository.OrderAggregateRepository;
import com.incomm.vms.fileprocess.repository.OrderDetailRepository;
import com.incomm.vms.fileprocess.repository.OrderLineItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static com.incomm.vms.fileprocess.config.Constants.*;

@Service
public class FileAggregationService {
    private final static Logger LOGGER = LoggerFactory.getLogger(FileAggregationService.class);

    @Autowired
    private DeleteCardRepository deleteCardRepository;
    @Autowired
    private OrderAggregateRepository orderAggregateRepository;
    @Autowired
    private OrderLineItemRepository orderLineItemRepository;
    @Autowired
    private OrderDetailRepository orderDetailRepository;

    public void saveTotalProducedCount(FileAggregateDTO fileAggregateDTO) {
        String correlationId = fileAggregateDTO.getCorrelationId();
        LOGGER.debug("Saving aggregate DTO {}", fileAggregateDTO.toString());
        FileAggregateSummaryStore.updateProducedRecordCount(correlationId, fileAggregateDTO.getFileName(),
                fileAggregateDTO.getTotalRecordCount());
        if (isConsumptionComplete(correlationId)) {
            aggregateSummary(correlationId);
        }
    }

    public void saveConsumedDetail(String panCode, Boolean deleteRequired, Map<String, String> headers) {
        String correlationId = headers.get(CORRELATION_ID);
        String fileName = headers.get(FILE_NAME);
        LOGGER.debug("Saving consumer DTO with header {}", headers);
        FileAggregateSummaryStore.updateConsumedRecordCount(correlationId, panCode, deleteRequired, fileName);
        if (isConsumptionComplete(correlationId)) {
            aggregateSummary(correlationId);
        }
    }

    public void addConsumerCountForFailedRecord(Map<String, String> headers) {
        String correlationId = headers.get(CORRELATION_ID);
        String fileName = headers.get(FILE_NAME);
        LOGGER.debug("Saving consumer DTO with header {}", headers);
        FileAggregateSummaryStore.updateConsumedFailedRecordCount(correlationId, fileName);
        if (isConsumptionComplete(correlationId)) {
            aggregateSummary(correlationId);
        }
    }

    protected void aggregateSummary(String correlationId) {
        FileAggregateSummary summary = FileAggregateSummaryStore.getSummaryStore(correlationId);
        LOGGER.info("Will update product detail .. ");
        // get aggregate info
        if (!summary.getListOfPanCodes().isEmpty()) {
            LOGGER.info("Getting Pan codes ");
            List<OrderDetailAggregate> aggregateList = orderAggregateRepository.getLineItemSummary(summary.getListOfPanCodes());
            aggregateList.stream().forEach(x -> updateOrder(x));
        }

        // delete records
        if (!summary.getListOfDeletePanCodes().isEmpty()) {
            LOGGER.info("Deleting Pan codes ");
            deleteCardRepository.deleteCards(summary.getListOfDeletePanCodes());
        }
        // clean up cache once completed
        completeProcessing(correlationId);
    }

    @Transactional
    private void updateOrder(OrderDetailAggregate orderDetailAggregate) {
        orderLineItemRepository.update(orderDetailAggregate);
        OrderDetailCount detailCount = orderLineItemRepository.getDetailCount(
                orderDetailAggregate.getOrderId(),
                orderDetailAggregate.getPartnerId());
        orderDetailRepository.update(detailCount, orderDetailAggregate.getOrderId(),
                orderDetailAggregate.getPartnerId());
    }

    private boolean isConsumptionComplete(String correlationId) {
        FileAggregateSummary summary = FileAggregateSummaryStore.getSummaryStore(correlationId);
        LOGGER.info("summary.getTotalConsumedRecordCount(): {}", summary.getTotalConsumedRecordCount());
        LOGGER.info("summary.getTotalProducedRecordCount(): {}", summary.getTotalProducedRecordCount());
        return summary.getTotalConsumedRecordCount() >= summary.getTotalProducedRecordCount();
    }

    private void completeProcessing(String correlationId) {
        FileAggregateSummaryStore.evictCache(correlationId);
    }

}
