package com.incomm.vms.fileprocess.service;

import com.incomm.vms.fileprocess.cache.FileAggregateSummaryStore;
import com.incomm.vms.fileprocess.model.FileAggregateDTO;
import com.incomm.vms.fileprocess.model.FileAggregateSummary;
import com.incomm.vms.fileprocess.model.LineItemDetail;
import com.incomm.vms.fileprocess.model.OrderDetailAggregate;
import com.incomm.vms.fileprocess.model.OrderDetailCount;
import com.incomm.vms.fileprocess.model.PostbackInfo;
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
import java.util.Optional;
import java.util.stream.Collectors;

import static com.incomm.vms.fileprocess.config.Constants.CORRELATION_ID;
import static com.incomm.vms.fileprocess.config.Constants.FILE_NAME;

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

    public void saveConsumedDetail(LineItemDetail lineItemDetail, Map<String, String> headers) {
        String correlationId = headers.get(CORRELATION_ID);
        String fileName = headers.get(FILE_NAME);
        LOGGER.debug("Saving consumer DTO with header {} correlationId {}", headers, correlationId);
        FileAggregateSummaryStore.updateConsumedRecordCount(correlationId, lineItemDetail, fileName);
        if (isConsumptionComplete(correlationId)) {
            aggregateSummary(correlationId);
        }
    }

    public void addConsumerCountForFailedRecord(Map<String, String> headers) {
        String correlationId = headers.get(CORRELATION_ID);
        String fileName = headers.get(FILE_NAME);
        LOGGER.debug("Saving consumer DTO with header {} correlationId {}", headers, correlationId);
        FileAggregateSummaryStore.updateConsumedFailedRecordCount(correlationId, fileName);
        if (isConsumptionComplete(correlationId)) {
            aggregateSummary(correlationId);
        }
    }

    protected void aggregateSummary(String correlationId) {
        FileAggregateSummary summary = FileAggregateSummaryStore.getSummaryStore(correlationId);
        LOGGER.info("Will update product detail for {} correlationId:{}", summary, correlationId);

        if (!summary.getLineItemDetails().isEmpty()) {
            List<String> panCodes = summary.getLineItemDetails()
                    .stream()
                    .map(x -> x.getPanCode())
                    .collect(Collectors.toList());
            LOGGER.info("Total orders to aggregate: {} for correlationId: {}", panCodes.size(), correlationId);
            List<OrderDetailAggregate> aggregateList = orderAggregateRepository.getLineItemSummary(panCodes);

            aggregateList.stream().forEach(orderDetailAggregate -> updateOrder(orderDetailAggregate));

            summary.getLineItemDetails().stream()
                    .filter(lineItemDetail -> lineItemDetail.isDeleteCard() == true)
                    .forEach(x -> deleteCard(x));
        }
        // clean up cache once completed
        completeProcessing(correlationId);
    }

    @Transactional
    private void updateOrder(OrderDetailAggregate aggregate) {
        orderLineItemRepository.update(aggregate);
        OrderDetailCount detailCount = orderLineItemRepository.getDetailCount(aggregate.getOrderId(), aggregate.getPartnerId());
        orderDetailRepository.update(detailCount, aggregate.getOrderId(), aggregate.getPartnerId());
    }

    @Transactional
    private void deleteCard(LineItemDetail lineItemDetail) {
        String result = deleteCardRepository.deleteCard(lineItemDetail.getPanCode());
        LOGGER.info("Result of the delete card is {} ", result);
    }

    private void sendPostback(LineItemDetail lineItemDetail) {
        Optional<PostbackInfo> optionalPostback = orderDetailRepository.findPostbackInfo(lineItemDetail.getOrderId(),
                lineItemDetail.getPartnerId());
        if (optionalPostback.isPresent()) {
            if (optionalPostback.get().getReponse().equalsIgnoreCase("true") ||
                    optionalPostback.get().getReponse().equalsIgnoreCase("1")) {

            }
        }

    }

    private boolean isConsumptionComplete(String correlationId) {
        FileAggregateSummary summary = FileAggregateSummaryStore.getSummaryStore(correlationId);
        LOGGER.info("summary.getTotalConsumedRecordCount(): {} for correlationId:{}", summary.getTotalConsumedRecordCount(), correlationId);
        LOGGER.info("summary.getTotalProducedRecordCount(): {} for correlationId:{}\"", summary.getTotalProducedRecordCount(), correlationId);
        return summary.getTotalConsumedRecordCount() >= summary.getTotalProducedRecordCount();
    }

    private void completeProcessing(String correlationId) {
        FileAggregateSummaryStore.evictCache(correlationId);
    }

}
