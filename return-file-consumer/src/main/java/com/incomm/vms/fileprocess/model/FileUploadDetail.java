package com.incomm.vms.fileprocess.model;

import com.google.gson.Gson;

public class FileUploadDetail {
    private String instanceCode;
    private String fileName;
    private int totalRecCount;
    private int successRecCount;
    private int failureRecCount;
    private String failureDesc;
    private String fileStatus;
    private String user;
    private String insertDate;
    private String updateDate;

    public String getInstanceCode() {
        return instanceCode;
    }

    public void setInstanceCode(String instanceCode) {
        this.instanceCode = instanceCode;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getTotalRecCount() {
        return totalRecCount;
    }

    public void setTotalRecCount(int totalRecCount) {
        this.totalRecCount = totalRecCount;
    }

    public int getSuccessRecCount() {
        return successRecCount;
    }

    public void setSuccessRecCount(int successRecCount) {
        this.successRecCount = successRecCount;
    }

    public int getFailureRecCount() {
        return failureRecCount;
    }

    public void setFailureRecCount(int failureRecCount) {
        this.failureRecCount = failureRecCount;
    }

    public String getFailureDesc() {
        return failureDesc;
    }

    public void setFailureDesc(String failureDesc) {
        this.failureDesc = failureDesc;
    }

    public String getFileStatus() {
        return fileStatus;
    }

    public void setFileStatus(String fileStatus) {
        this.fileStatus = fileStatus;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getInsertDate() {
        return insertDate;
    }

    public void setInsertDate(String insertDate) {
        this.insertDate = insertDate;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
