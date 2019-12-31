package com.incomm.vms.postback.service;

import com.incomm.vms.postback.model.Card;
import com.incomm.vms.postback.model.LineItem;
import com.incomm.vms.postback.model.Order;
import com.incomm.vms.postback.repository.OrderDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

import static com.incomm.vms.postback.config.Constants.PRE_AUTH_VERIFICATION_SUCCESS_FLAG;

@Service
public class OrderStatusService {

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    public Order getOrderDetails(String orderId, String partnerId) {
        Order order = orderDetailRepository.getOrderStatus(orderId, partnerId);
        if (PRE_AUTH_VERIFICATION_SUCCESS_FLAG.equalsIgnoreCase(order.getOfacFlag())) {
            if(!StringUtils.isEmpty(order.getResponseCode())) {
                List<LineItem> lineItems = order.getLineItem();
                for (LineItem item : lineItems) {
                    List<Card> cards = orderDetailRepository.getCardDetails(orderId, partnerId, item.getLineItemID());
                    item.setCards(cards);
                }
                order.setLineItem(lineItems);
            }
        }
        return order;
    }

}
