package com.incomm.vms.fileprocess.controller;

import com.incomm.vms.fileprocess.cache.FileAggregateSummaryStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping(value = "/consumer", produces = {"application/json"})
public class ConsumerController {
    private final static Logger LOGGER = LoggerFactory.getLogger(ConsumerController.class);
    @Autowired
    private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    @GetMapping("/getall")
    @ResponseBody
    public ConcurrentHashMap getAllConsumtionDetails() {
        return FileAggregateSummaryStore.getAllSummaryStore();
    }

    @GetMapping("/resetCache")
    public ConcurrentHashMap resetSummaryStore() {
        FileAggregateSummaryStore.getAllSummaryStore().clear();
        FileAggregateSummaryStore.syncCache();
        return FileAggregateSummaryStore.getAllSummaryStore();
    }

    @GetMapping("/stop")
    public String stop() {
        kafkaListenerEndpointRegistry.getListenerContainers().forEach((container) -> {
            if (container.isRunning()) {
                container.stop();
                LOGGER.info("Container {} is stopped .. ", container.getContainerProperties());
            }
        });
        return "Stopped";
    }

    @GetMapping("/start")
    public String start() {
        kafkaListenerEndpointRegistry.getListenerContainers().forEach((container) -> {
            if (!container.isRunning()) {
                container.start();
                LOGGER.info("Container {} is started .. ", container.getContainerProperties());
            }
        });

        return "Started";
    }

}
