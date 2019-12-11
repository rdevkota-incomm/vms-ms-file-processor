package com.incomm.vms.fileprocess.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class KafkaJsonDeserializer<T> implements Deserializer {
    private final static Logger LOGGER = LoggerFactory.getLogger(KafkaJsonDeserializer.class);

    private Class<T> type;

    public KafkaJsonDeserializer(Class type) {
        this.type = type;
    }

    @Override
    public void configure(Map map, boolean b) {
    }

    @Override
    public Object deserialize(String s, byte[] bytes) {
        ObjectMapper mapper = new ObjectMapper();
        T obj = null;
        try {
            obj = mapper.readValue(bytes, type);
        } catch (Exception e) {

            LOGGER.error(e.getMessage());
        }
        return obj;
    }

    @Override
    public void close() {
    }

}
