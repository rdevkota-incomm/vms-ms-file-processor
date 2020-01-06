package com.incomm.vms.fileprocess.model;

import com.google.gson.Gson;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RejectReasonMaster {
    private String rejectCode;
    private String successFailureFlag;
    private String rejectReason;

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
