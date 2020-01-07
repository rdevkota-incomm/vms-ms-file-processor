package com.incomm.vms.fileprocess.model;

import com.google.gson.Gson;
import lombok.Data;

@Data
public class FileProcessRequest {
    private String fileName;
    private String filePath;
    private String fileType;

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
