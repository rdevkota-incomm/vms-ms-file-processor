package com.incomm.vms.postback.service;

import com.google.gson.Gson;
import com.incomm.vms.common.model.PostBackDetail;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.web.client.RestTemplate;

import static com.incomm.vms.common.config.Constants.CORRELATION_ID;
import static com.incomm.vms.common.config.Constants.FILE_NAME;


public class ConsumerService extends Thread {
    private static Logger LOGGER = LoggerFactory.getLogger(ConsumerService.class);

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private PostBackService postBackService;

    @KafkaListener(topics = "${vms.post-back.topic}")
    public void consumeMessage(ConsumerRecord<?, ?> consumerRecord, Acknowledgment acknowledgment) {
        Gson gson = new Gson();
        String payload = consumerRecord.value().toString();
        PostBackDetail postBackDetail = gson.fromJson(payload, PostBackDetail.class);

        String correlationId = postBackDetail.getHeaders().get(CORRELATION_ID);
        String fileName = postBackDetail.getHeaders().get(FILE_NAME);

        LOGGER.info("Received payload: {} for correlationId:{} filename:{} ", postBackDetail, correlationId, fileName);

        try {
            postBackService.submit(restTemplate, postBackDetail);
            acknowledgment.acknowledge();
        } catch (Exception e) {
            LOGGER.error("Error processing payload {} for correlationId:{} filename:{}", postBackDetail.toString(),
                    correlationId, fileName, e);
        }
    }
}
