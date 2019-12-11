package com.incomm.vms.fileprocess.model;

import com.google.gson.Gson;

public class OrderDetailAggregate {
    private int totalCount;
    private int printerAcknowledgedCount;
    private int rejectCount;
    private String rejectReason;
    private String orderId;
    private String partnerId;
    private String lineItemId;

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getPrinterAcknowledgedCount() {
        return printerAcknowledgedCount;
    }

    public void setPrinterAcknowledgedCount(int printerAcknowledgedCount) {
        this.printerAcknowledgedCount = printerAcknowledgedCount;
    }

    public int getRejectCount() {
        return rejectCount;
    }

    public void setRejectCount(int rejectCount) {
        this.rejectCount = rejectCount;
    }

    public String getRejectReason() {
        return rejectReason;
    }

    public void setRejectReason(String rejectReason) {
        this.rejectReason = rejectReason;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(String partnerId) {
        this.partnerId = partnerId;
    }

    public String getLineItemId() {
        return lineItemId;
    }

    public void setLineItemId(String lineItemId) {
        this.lineItemId = lineItemId;
    }

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
