package com.incomm.vms.fileprocess.model;

import com.google.gson.Gson;

public class LineItemDetail {
    private String panCode;
    private String partnerId;
    private String parentOId;
    private String orderId;
    private String lineItemId;

    public String getPanCode() {
        return panCode;
    }

    public void setPanCode(String panCode) {
        this.panCode = panCode;
    }

    public String getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(String partnerId) {
        this.partnerId = partnerId;
    }

    public String getParentOId() {
        return parentOId;
    }

    public void setParentOId(String parentOId) {
        this.parentOId = parentOId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
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
