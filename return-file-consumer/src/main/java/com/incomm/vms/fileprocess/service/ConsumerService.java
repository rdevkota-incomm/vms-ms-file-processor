package com.incomm.vms.fileprocess.service;

import com.google.gson.Gson;
import com.incomm.vms.fileprocess.model.FileAggregateDTO;
import com.incomm.vms.fileprocess.model.ReturnFileDTO;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;

import static com.incomm.vms.fileprocess.config.Constants.*;

public class ConsumerService extends Thread {
    private static Logger LOGGER = LoggerFactory.getLogger(ConsumerService.class);

    @Autowired
    private DataProcessingService dataProcessingService;
    @Autowired
    private FileAggregationService fileAggregationService;

    @KafkaListener(topics = "${vms.printer-awk.topic}", id = CONSUMER_CONTAINER_ID , containerGroup = CONSUMER_CONTAINER_GROUP)
    public void consumeMessage(ConsumerRecord<?, ?> consumerRecord, Acknowledgment acknowledgment) {
        Gson gson = new Gson();
        String payload = consumerRecord.value().toString();;
        ReturnFileDTO returnFileDTO = gson.fromJson(payload, ReturnFileDTO.class);
        LOGGER.info("Received Message payload: {}", returnFileDTO.toString());
        dataProcessingService.processRecords(returnFileDTO);
        acknowledgment.acknowledge();
    }

    @KafkaListener(topics = "${vms.printer-awk-aggregate.topic}", id = CONSUMER_CONTAINER_AGG_ID, containerGroup = CONSUMER_CONTAINER_GROUP)
    public void consumeAggregateMessage(ConsumerRecord<?, ?> consumerRecord, Acknowledgment acknowledgment) {
        Gson gson = new Gson();
        String payload = consumerRecord.value().toString();
        FileAggregateDTO fileAggregateDTO = gson.fromJson(payload, FileAggregateDTO.class);
        LOGGER.info("Received Aggregate payload: {}", fileAggregateDTO.toString());
        fileAggregationService.saveTotalProducedCount(fileAggregateDTO);
        acknowledgment.acknowledge();
    }
}
