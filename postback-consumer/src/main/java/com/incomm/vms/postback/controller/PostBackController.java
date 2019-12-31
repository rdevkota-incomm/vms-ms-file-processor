package com.incomm.vms.postback.controller;

import com.incomm.vms.postback.model.PostBackDetail;
import com.incomm.vms.postback.service.PostBackService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping(value = "/postback", produces = {"application/json"})
public class PostBackController {
    private final static Logger LOGGER = LoggerFactory.getLogger(PostBackController.class);
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private PostBackService postBackService;

    @PostMapping(path = "/process", produces = "application/json; charset=UTF-8")
    public ResponseEntity processMessage(@RequestBody PostBackDetail postBackDetail) {
        String response = postBackService.submit(restTemplate, postBackDetail);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

}
