package com.incomm.vms.postback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@ComponentScan("com.incomm.vms.postback")
public class PostBackApplication {
    private static Logger LOGGER = LoggerFactory.getLogger(PostBackApplication.class);

    public static void main(String[] args) {
        LOGGER.info("PostBackApplication is being started");
        SpringApplication.run(PostBackApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
