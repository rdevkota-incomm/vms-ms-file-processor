package com.incomm.vms.fileprocess.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/consumer", produces = {"application/json"})
public class ConsumerController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConsumerController.class);
    @Autowired
    private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    @GetMapping(path = "/stop", produces = "application/json; charset=UTF-8")
    public String stop() {
        kafkaListenerEndpointRegistry.getListenerContainers().forEach(container -> {
            if (container.isRunning()) {
                container.stop();
                LOGGER.info("Container {} is stopped .. ", container.getContainerProperties());
            }
        });
        return "Started";
    }

    @GetMapping(path = "/start", produces = "application/json; charset=UTF-8")
    public String start() {
        kafkaListenerEndpointRegistry.getListenerContainers().forEach(container -> {
            if (!container.isRunning()) {
                container.start();
                LOGGER.info("Container {} is started .. ", container.getContainerProperties());
            }
        });

        return "Started";
    }

}
