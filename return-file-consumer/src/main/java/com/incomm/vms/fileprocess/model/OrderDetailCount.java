package com.incomm.vms.fileprocess.model;

import com.google.gson.Gson;

public class OrderDetailCount {
    private int totalCount;
    private int rejectedCountSummation;
    private int statusCountSummation;

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getRejectedCountSummation() {
        return rejectedCountSummation;
    }

    public void setRejectedCountSummation(int rejectedCountSummation) {
        this.rejectedCountSummation = rejectedCountSummation;
    }

    public int getStatusCountSummation() {
        return statusCountSummation;
    }

    public void setStatusCountSummation(int statusCountSummation) {
        this.statusCountSummation = statusCountSummation;
    }

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
