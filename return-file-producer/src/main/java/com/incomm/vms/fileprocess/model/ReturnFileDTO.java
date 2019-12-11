package com.incomm.vms.fileprocess.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.Gson;

import java.io.Serializable;
import java.util.Map;

public class ReturnFileDTO implements Serializable {

    private Map<String, String> headers;
    private String customer;
    private String shipSuffix;
    private String parentOrderId;
    private String childOrderId;
    private String serialNumber;
    private String rejectCode;
    private String rejectReason;
    private String fileDate;
    private String cardType;
    private String clientOrderId;

    public String getCustomer() {
        return customer;
    }

    @JsonProperty("CUSTOMER")
    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getShipSuffix() {
        return shipSuffix;
    }

    @JsonProperty("SHIPSUFFIX")
    public void setShipSuffix(String shipSuffix) {
        this.shipSuffix = shipSuffix;
    }

    public String getParentOrderId() {
        return parentOrderId;
    }

    @JsonProperty("PARENT_ORDER_ID")
    public void setParentOrderId(String parentOrderId) {
        this.parentOrderId = parentOrderId;
    }

    public String getChildOrderId() {
        return childOrderId;
    }

    @JsonProperty("CHILD_ORDER_ID")
    public void setChildOrderId(String childOrderId) {
        this.childOrderId = childOrderId;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    @JsonProperty("SERIALNUMBER")
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getRejectCode() {
        return rejectCode;
    }

    @JsonProperty("REJECT_CODE")
    public void setRejectCode(String rejectCode) {
        this.rejectCode = rejectCode;
    }

    public String getRejectReason() {
        return rejectReason;
    }

    @JsonProperty("REJECT_REASON")
    public void setRejectReason(String rejectReason) {
        this.rejectReason = rejectReason;
    }

    public String getFileDate() {
        return fileDate;
    }

    @JsonProperty("FILE_DATE")
    public void setFileDate(String fileDate) {
        this.fileDate = fileDate;
    }

    public String getCardType() {
        return cardType;
    }

    @JsonProperty("CARD_TYPE")
    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getClientOrderId() {
        return clientOrderId;
    }

    @JsonProperty("CLIENT_ORDER_ID")
    public void setClientOrderId(String clientOrderId) {
        this.clientOrderId = clientOrderId;
    }

    @JsonIgnore
    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

}
