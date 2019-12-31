package com.incomm.vms.fileprocess.service;

import com.incomm.vms.fileprocess.model.LineItemDetail;
import com.incomm.vms.fileprocess.model.OrderDetailAggregate;
import com.incomm.vms.fileprocess.model.OrderDetailCount;
import com.incomm.vms.fileprocess.model.PostBackDetail;
import com.incomm.vms.fileprocess.repository.LineItemDetailRepository;
import com.incomm.vms.fileprocess.repository.OrderAggregateRepository;
import com.incomm.vms.fileprocess.repository.OrderDetailRepository;
import com.incomm.vms.fileprocess.repository.OrderLineItemRepository;
import com.incomm.vms.fileprocess.utility.DistinctPredicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.incomm.vms.fileprocess.config.Constants.CORRELATION_ID;
import static com.incomm.vms.fileprocess.config.Constants.FILE_NAME;

@Service
public class OrderAggregationService {
    private final static Logger LOGGER = LoggerFactory.getLogger(OrderAggregationService.class);

    @Autowired
    private OrderAggregateRepository orderAggregateRepository;
    @Autowired
    private OrderLineItemRepository orderLineItemRepository;
    @Autowired
    private OrderDetailRepository orderDetailRepository;
    @Autowired
    private LineItemDetailRepository lineItemDetailRepository;
    @Autowired
    private PostBackProducerService postBackProducerService;

    protected void completeProcessing(LineItemDetail lineItemDetail, String fileName, String correlationId) {
        LOGGER.debug("Saving consumer DTO with file:{} correlationId:{}", fileName, correlationId);
        if (isConsumptionComplete(lineItemDetail, fileName, correlationId)) {
            aggregateSummary(lineItemDetail, fileName, correlationId);
        }
    }

    protected void aggregateSummary(LineItemDetail lineItemDetail, String fileName, String correlationId) {
        List<OrderDetailAggregate> aggregateList = orderAggregateRepository.getLineItemSummary(lineItemDetail);
        aggregateList.stream().forEach(orderDetailAggregate -> updateOrder(orderDetailAggregate, fileName, correlationId));

        // filter out by combination of order Id and partner Id so that message will not be sent
        // multiple times
        aggregateList.stream()
                .filter(DistinctPredicate.distinctByKey(x -> x.getOrderId()))
                .filter((DistinctPredicate.distinctByKey(y -> y.getPartnerId())))
                .forEach(orderDetailAggregate -> handlePostBack(orderDetailAggregate, fileName, correlationId));
    }

    @Transactional
    private void updateOrder(OrderDetailAggregate aggregate, String fileName, String correlationId) {
        LOGGER.info("Updating vms_order_lineitem with aggregate:{} for file:{} correlationId:{}", aggregate, fileName, correlationId);
        orderLineItemRepository.update(aggregate);

        OrderDetailCount detailCount = orderLineItemRepository.getDetailCount(aggregate.getOrderId(), aggregate.getPartnerId());

        LOGGER.info("Updating vms_order_details with aggregate{} for file:{} correlationId:{}", aggregate, fileName, correlationId);
        orderDetailRepository.update(detailCount, aggregate.getOrderId(), aggregate.getPartnerId());
    }

    private boolean isConsumptionComplete(LineItemDetail lineItemDetail, String fileName, String correlationId) {
        OrderDetailAggregate aggregate = lineItemDetailRepository.getDetailAggregateCount(lineItemDetail);
        LOGGER.info("Retrieved aggregate:{} for file:{} correlationId:{}", aggregate.toString(), fileName, correlationId);
        return aggregate.getTotalCount() == aggregate.getPrinterAcknowledgedCount();
    }

    private void handlePostBack(OrderDetailAggregate aggregate, String fileName, String correlationId) {
        Optional<PostBackDetail> optionalPostBack = orderDetailRepository.findPostBackInfo(aggregate.getOrderId(), aggregate.getPartnerId());
        if (optionalPostBack.isPresent()) {
            PostBackDetail postBackDetail = optionalPostBack.get();
            LOGGER.info("Retrieved PostBackDetail:{} for file:{} correlationId:{}", postBackDetail.toString(), fileName, correlationId);
            submitPostBack(fileName, correlationId, postBackDetail);
        } else {
            LOGGER.warn("No PostBack detail is available in the database for OrderId:{} PartnerId:{} for file:{} correlationId:{}",
                    aggregate.getOrderId(), aggregate.getPartnerId(), fileName, correlationId);
        }

    }

    private void submitPostBack(String fileName, String correlationId, PostBackDetail postBackDetail) {
        if (!StringUtils.isEmpty(postBackDetail.getUrl())) {
            if (postBackDetail.getResponse().equalsIgnoreCase("true") || postBackDetail.getResponse().equals("1")) {
                postBackDetail.setHeaders(setPostBackHeaders(fileName, correlationId));
                postBackProducerService.send(postBackDetail);
                LOGGER.info("PostBack Submitted for file:{} correlationId:{}", fileName, correlationId);
            }
        }
    }

    private Map<String, String> setPostBackHeaders(String fileName, String correlationId) {
        Map<String, String> headers = new HashMap<>();
        headers.put(FILE_NAME, fileName);
        headers.put(CORRELATION_ID, correlationId);
        return headers;
    }

}
