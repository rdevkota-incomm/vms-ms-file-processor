package com.incomm.vms.fileprocess.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class KafkaJsonSerializer implements Serializer {
    private final static Logger LOGGER = LoggerFactory.getLogger(KafkaJsonSerializer.class);

    @Override
    public void configure(Map configs, boolean isKey) {
    }

    @Override
    public byte[] serialize(String s, Object o) {
        byte[] retVal = null;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            retVal = objectMapper.writeValueAsBytes(o);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return retVal;
    }

    @Override
    public void close() {
    }
}
