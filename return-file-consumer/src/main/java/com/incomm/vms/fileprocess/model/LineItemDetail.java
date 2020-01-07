package com.incomm.vms.fileprocess.model;

import com.google.gson.Gson;
import lombok.Data;

@Data
public class LineItemDetail {
    private String panCode;
    private String partnerId;
    private String parentOId;
    private String orderId;
    private String lineItemId;
    private Boolean isDeleteCard;

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
