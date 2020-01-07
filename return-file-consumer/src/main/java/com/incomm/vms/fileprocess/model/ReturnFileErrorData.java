package com.incomm.vms.fileprocess.model;

import com.google.gson.Gson;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReturnFileErrorData {
    private String fileName;
    private String parentOrderId;
    private String childOrderId;
    private String serialNumber;
    private String rejectCode;
    private String rejectReason;
    private String pan;
    private String errorMessage;

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
