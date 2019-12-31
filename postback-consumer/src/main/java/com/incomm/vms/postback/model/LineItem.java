package com.incomm.vms.postback.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class LineItem {
    private String lineItemID;
    private String responseCode;
    private String responseMessage;
    private String status;
    private List<Card> cards;
}
