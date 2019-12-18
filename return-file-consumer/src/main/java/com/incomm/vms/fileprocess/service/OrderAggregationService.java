package com.incomm.vms.fileprocess.service;

import com.incomm.vms.fileprocess.model.LineItemDetail;
import com.incomm.vms.fileprocess.model.OrderDetailAggregate;
import com.incomm.vms.fileprocess.model.OrderDetailCount;
import com.incomm.vms.fileprocess.model.PostBackInfo;
import com.incomm.vms.fileprocess.repository.DeleteCardRepository;
import com.incomm.vms.fileprocess.repository.LineItemDetailRepository;
import com.incomm.vms.fileprocess.repository.OrderAggregateRepository;
import com.incomm.vms.fileprocess.repository.OrderDetailRepository;
import com.incomm.vms.fileprocess.repository.OrderLineItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class OrderAggregationService {
    private final static Logger LOGGER = LoggerFactory.getLogger(OrderAggregationService.class);
    private ExecutorService executor = Executors.newFixedThreadPool(10);

    @Autowired
    private DeleteCardRepository deleteCardRepository;
    @Autowired
    private OrderAggregateRepository orderAggregateRepository;
    @Autowired
    private OrderLineItemRepository orderLineItemRepository;
    @Autowired
    private OrderDetailRepository orderDetailRepository;
    @Autowired
    private LineItemDetailRepository lineItemDetailRepository;
    @Autowired
    private RestTemplate  restTemplate;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    public void completeProcessing(LineItemDetail lineItemDetail, String fileName, String correlationId) {
        LOGGER.debug("Saving consumer DTO with file:{} correlationId:{}", fileName, correlationId);
        if (isConsumptionComplete(lineItemDetail, fileName, correlationId)) {
            aggregateSummary(lineItemDetail, fileName, correlationId);
        }
    }

    protected void aggregateSummary(LineItemDetail lineItemDetail, String fileName, String correlationId) {
        List<OrderDetailAggregate> aggregateList = orderAggregateRepository.getLineItemSummary(lineItemDetail);
        aggregateList.stream().forEach(orderDetailAggregate -> updateOrder(orderDetailAggregate, fileName, correlationId));

        aggregateList.stream().forEach(orderDetailAggregate -> sendPostBack(orderDetailAggregate, fileName, correlationId));
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

    private void sendPostBack(OrderDetailAggregate aggregate, String fileName, String correlationId) {
        Optional<PostBackInfo> optionalPostBack = orderDetailRepository.findPostBackInfo(aggregate.getOrderId(), aggregate.getPartnerId());
        if (optionalPostBack.isPresent()) {
            PostBackInfo postBackInfo = optionalPostBack.get();
            LOGGER.info("Retrieved PostBackInfo:{} for file:{} correlationId:{}", postBackInfo.toString(), fileName, correlationId);

            if (!StringUtils.isEmpty(postBackInfo.getUrl())) {
                if (postBackInfo.getReponse().equalsIgnoreCase("true") ||
                        postBackInfo.getReponse().equalsIgnoreCase("1")) {
                    executor.submit(new PostBackService(restTemplate, postBackInfo.getUrl(), "some message", fileName, correlationId));
                    LOGGER.info("PostBack Submitted for file:{} correlationId:{}", fileName, correlationId);
                }
            }
        } else {
            LOGGER.warn("No PostBack detail is available in the database for OrderId:{} PartnerId:{} for file:{} correlationId:{}",
                    aggregate.getOrderId(), aggregate.getPartnerId(), fileName, correlationId);
        }

    }

}
