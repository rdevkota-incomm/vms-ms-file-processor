package com.incomm.vms.fileprocess.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

public class PostBackService implements Runnable {
    private final static Logger LOGGER = LoggerFactory.getLogger(PostBackService.class);

    private RestTemplate restTemplate;
    private String url;
    private String message;
    private String fileName;
    private String correlationId;

    public PostBackService(RestTemplate restTemplate, String url, String message, String fileName, String correlationId) {
        this.restTemplate = restTemplate;
        this.url = url;
        this.message = message;
        this.fileName = fileName;
        this.correlationId = correlationId;
    }

    @Override
    public void run() {
        try {
            String response = restTemplate.getForObject(url, String.class);
            LOGGER.info("Postback Complete with response:{} for url:{} file:{} correlationId:{}", response, url, fileName, correlationId);
        } catch (Exception e) {
            LOGGER.error("Postback Failed with error:{} for url:{} file:{} correlationId:{}", e.getLocalizedMessage(), url, fileName, correlationId, e);
        }
    }

}
