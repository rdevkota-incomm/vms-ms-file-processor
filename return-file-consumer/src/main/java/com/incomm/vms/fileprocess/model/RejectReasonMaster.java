package com.incomm.vms.fileprocess.model;

import com.google.gson.Gson;

public class RejectReasonMaster {
    private String rejectCode;
    private String successFailureFlag;
    private String rejectReason;

    public String getRejectCode() {
        return rejectCode;
    }

    public void setRejectCode(String rejectCode) {
        this.rejectCode = rejectCode;
    }

    public String getSuccessFailureFlag() {
        return successFailureFlag;
    }

    public void setSuccessFailureFlag(String successFailureFlag) {
        this.successFailureFlag = successFailureFlag;
    }

    public String getRejectReason() {
        return rejectReason;
    }

    public void setRejectReason(String rejectReason) {
        this.rejectReason = rejectReason;
    }

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
