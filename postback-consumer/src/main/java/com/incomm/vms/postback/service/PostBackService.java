package com.incomm.vms.postback.service;

import com.incomm.vms.common.model.PostBackDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import static com.incomm.vms.common.config.Constants.POST_BACK_TASK_EXECUTOR_POOL;


@Service
public class PostBackService {
    private final static Logger LOGGER = LoggerFactory.getLogger(PostBackService.class);

    @Async(POST_BACK_TASK_EXECUTOR_POOL)
    public void submit(RestTemplate restTemplate, PostBackDetail postBackDetail) {
        try {
            String response = restTemplate.getForObject(postBackDetail.getUrl(), String.class);
            LOGGER.info("PostBackService Complete with response:{} for PostBackDetail: {}", response, postBackDetail);
        } catch (Exception e) {
            LOGGER.error("PostBackService Failed for PostBackDetail: {} ", postBackDetail, e);
        }
    }

}
