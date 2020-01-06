package com.incomm.vms.fileprocess.model;

import com.google.gson.Gson;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderDetailAggregate {
    private int totalCount;
    private int printerAcknowledgedCount;
    private int rejectCount;
    private String rejectReason;
    private String orderId;
    private String partnerId;
    private String lineItemId;

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
