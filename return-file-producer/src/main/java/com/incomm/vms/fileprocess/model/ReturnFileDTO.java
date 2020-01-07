package com.incomm.vms.fileprocess.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.Gson;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
public class ReturnFileDTO implements Serializable {

    private Map<String, String> headers;

    @JsonProperty("CUSTOMER")
    private String customer;
    @JsonProperty("SHIPSUFFIX")
    private String shipSuffix;
    @JsonProperty("PARENT_ORDER_ID")
    private String parentOrderId;
    @JsonProperty("CHILD_ORDER_ID")
    private String childOrderId;
    @JsonProperty("SERIALNUMBER")
    private String serialNumber;
    @JsonProperty("REJECT_CODE")
    private String rejectCode;
    @JsonProperty("REJECT_REASON")
    private String rejectReason;
    @JsonProperty("FILE_DATE")
    private String fileDate;
    @JsonProperty("CARD_TYPE")
    private String cardType;
    @JsonProperty("CLIENT_ORDER_ID")
    private String clientOrderId;

    @JsonIgnore
    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

}
