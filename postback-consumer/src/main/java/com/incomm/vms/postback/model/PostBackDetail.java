package com.incomm.vms.postback.model;

import com.google.gson.Gson;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class PostBackDetail {
    private String orderId;
    private String partnerId;
    private String status;
    private String url;
    private String response;

    private Map<String, String> headers;

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
