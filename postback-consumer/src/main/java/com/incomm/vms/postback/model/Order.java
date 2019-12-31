package com.incomm.vms.postback.model;

import com.google.gson.Gson;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class Order {
    private String orderId;
    private String partnerId;
    private String ofacFlag;
    private String responseCode;
    private String responseMessage;
    private String status;
    private String shippingMethod;

    private List<LineItem> lineItem;

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}



