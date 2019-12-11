package com.incomm.vms.fileprocess.model;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.List;

public class FileAggregateSummary implements Serializable {

    private int totalProducedRecordCount;
    private int totalConsumedRecordCount;
    private long completionTime;
    private String fileName;

    private List<String> listOfPanCodes;
    private List<String> listOfDeletePanCodes;

    public int getTotalProducedRecordCount() {
        return totalProducedRecordCount;
    }

    public void setTotalProducedRecordCount(int totalProducedRecordCount) {
        this.totalProducedRecordCount = totalProducedRecordCount;
    }

    public int getTotalConsumedRecordCount() {
        return totalConsumedRecordCount;
    }

    public void setTotalConsumedRecordCount(int totalConsumedRecordCount) {
        this.totalConsumedRecordCount = totalConsumedRecordCount;
    }

    public List<String> getListOfPanCodes() {
        return listOfPanCodes;
    }

    public void setListOfPanCodes(List<String> listOfPanCodes) {
        this.listOfPanCodes = listOfPanCodes;
    }

    public void setPanCode(String panCode) {
        this.listOfPanCodes.add(panCode);
    }

    public List<String> getListOfDeletePanCodes() {
        return listOfDeletePanCodes;
    }

    public void setListOfDeletePanCodes(List<String> listOfDeletePanCodes) {
        this.listOfDeletePanCodes = listOfDeletePanCodes;
    }

    public void setDeletePanCode(String panCode) {
        this.listOfDeletePanCodes.add(panCode);
    }

    public long getCompletionTime() {
        return completionTime;
    }

    public void setCompletionTime(long completionTime) {
        this.completionTime = completionTime;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
