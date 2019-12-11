package com.incomm.vms.fileprocess.service;

import com.incomm.vms.fileprocess.model.ReturnFileAggregateDTO;
import com.incomm.vms.fileprocess.model.ReturnFileDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class ProducerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProducerService.class);

    @Value("${vms.printer-awk.topic}")
    private String printerAckTopic;
    @Value("${vms.printer-awk-aggregate.topic}")
    private String printerAckAggregateTopic;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void produceMessage(ReturnFileDTO payload) {
        LOGGER.info("Producing payload='{}'", payload);
        kafkaTemplate.send(printerAckTopic, payload.toString());
    }

    public void produceAggregateMessage(ReturnFileAggregateDTO payload) {
        LOGGER.info("Producing payload='{}'", payload);
        kafkaTemplate.send(printerAckAggregateTopic, payload.toString());
    }
}
