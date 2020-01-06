package com.incomm.vms.postback.service;

import com.incomm.vms.postback.model.Order;
import com.incomm.vms.postback.model.PostBackDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

import static com.incomm.vms.postback.config.Constants.CORRELATION_ID;
import static com.incomm.vms.postback.config.Constants.POST_BACK_TASK_EXECUTOR_POOL;

@Service
public class PostBackService {
    private final static Logger LOGGER = LoggerFactory.getLogger(PostBackService.class);

    @Autowired
    private OrderStatusService orderStatusService;

    @Async(POST_BACK_TASK_EXECUTOR_POOL)
    public String submit(RestTemplate restTemplate, PostBackDetail postBackDetail) throws RestClientException, UnknownHostException {
        HttpHeaders headers = new HttpHeaders();
        headers.set("x-incfs-ip", InetAddress.getLocalHost().toString());
        headers.set("x-incfs-date", String.valueOf(new Date()));
        headers.set("x-incfs-correlationid", postBackDetail.getHeaders().get(CORRELATION_ID));
        headers.add("x-incfs-partnerid", postBackDetail.getPartnerId());
        // TODO
        // channels
//        headers.add("x-incfs-channel", channel);
//        headers.add("x-incfs-channel-identifier", channel);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Order order = orderStatusService.getOrderDetails(postBackDetail.getOrderId(), postBackDetail.getPartnerId());

        HttpEntity<String> entity = new HttpEntity<>(order.toString(), headers);

        ResponseEntity<String> response = restTemplate.exchange(postBackDetail.getUrl(), HttpMethod.GET, entity, String.class);

        LOGGER.info("PostBackService Complete with response:{} for PostBackDetail: {}", response, postBackDetail);

        return response.getStatusCode().toString();

    }

}
