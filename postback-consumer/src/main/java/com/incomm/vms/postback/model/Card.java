package com.incomm.vms.postback.model;

import com.google.gson.Gson;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Card {
    private String proxyNumber;
    private String pin;
    private String encryptedString;
    private String serialNumber;
    private String status;
    private String trackingNumber;
    private String shippingDateTime;
    private String cardNumber;
    private String hashCardNumber;
    private String expirationDate;
    private String custCode;
    private String prodCode;
    private String cardType;
    private String panCode;
    private String encryptCardNumber;
    private String printerResponse;

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
