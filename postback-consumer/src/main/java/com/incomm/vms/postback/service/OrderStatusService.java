package com.incomm.vms.postback.service;

import com.incomm.vms.postback.model.Card;
import com.incomm.vms.postback.model.LineItem;
import com.incomm.vms.postback.model.Order;
import com.incomm.vms.postback.repository.OrderDetailRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class OrderStatusService {
    private static Logger LOGGER = LoggerFactory.getLogger(OrderStatusService.class);

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    public Order getOrderDetails(String orderId, String partnerId) {
        LOGGER.info("Getting Order Details for orderId:{} partnerId:{}", orderId, partnerId);
        Order order = orderDetailRepository.getOrderStatus(orderId, partnerId);
        if (!StringUtils.isEmpty(order.getResponseCode())) {
            List<LineItem> lineItems = order.getLineItem();
            for (LineItem lineItem : lineItems) {
                List<Card> cards = orderDetailRepository.getCardDetails(orderId, partnerId, lineItem.getLineItemId());
                lineItem.setCards(cards);
            }
            order.setLineItem(lineItems);
        }
        return order;
    }
}
