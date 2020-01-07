package com.incomm.vms.fileprocess.service;

import com.google.gson.Gson;
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
    private MessageProcessingService messageProcessingService;

//    @KafkaListener(topics = "${vms.printer-awk.topic}", id = CONSUMER_CONTAINER_ID, containerGroup = CONSUMER_CONTAINER_GROUP)
    @KafkaListener(topics = "${vms.printer-awk.topic}", id = CONSUMER_CONTAINER_ID)
    public void consumeMessage(ConsumerRecord<?, ?> consumerRecord, Acknowledgment acknowledgment) {
        ReturnFileDTO returnFileDTO = deserializePayload(consumerRecord);
        String correlationId = returnFileDTO.getHeaders().get(CORRELATION_ID);
        String fileName = returnFileDTO.getHeaders().get(FILE_NAME);

        LOGGER.info("Received payload: {} for correlationId:{} filename:{} ", returnFileDTO.toString(), correlationId, fileName);

        try {
            messageProcessingService.processMessage(returnFileDTO);
            acknowledgment.acknowledge();
        } catch (Exception e) {
            LOGGER.error("Error processing payload {} with error: {} for correlationId:{} filename:{}", returnFileDTO.toString(),
                    e.getLocalizedMessage(), correlationId, fileName, e);
        }
    }

    private ReturnFileDTO deserializePayload(ConsumerRecord<?, ?> consumerRecord) {
        Gson gson = new Gson();
        String payload = consumerRecord.value().toString();
        return gson.fromJson(payload, ReturnFileDTO.class);
    }
}
