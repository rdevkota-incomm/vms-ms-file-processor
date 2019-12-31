package com.incomm.vms.fileprocess.service;

import com.incomm.vms.fileprocess.model.PostBackDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import static com.incomm.vms.fileprocess.config.Constants.POST_BACK_TASK_EXECUTOR_POOL;


@Service
public class PostBackProducerService {
    private final static Logger LOGGER = LoggerFactory.getLogger(PostBackProducerService.class);

    @Value("${vms.post-back.topic}")
    private String postBackTopic;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Async(POST_BACK_TASK_EXECUTOR_POOL)
    protected void send(PostBackDetail postBackDetail) {
        try {
            kafkaTemplate.send(postBackTopic, postBackDetail.toString());
            LOGGER.info("Postback message send complete PostBackDetail: {} ", postBackDetail);
        } catch (Exception e) {
            LOGGER.error("Postback Failed for PostBackDetail: {} ", postBackDetail, e);
        }
    }

}
