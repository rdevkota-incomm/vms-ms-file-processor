package com.incomm.vms.fileprocess.model;

import com.google.gson.Gson;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OrderDetailCount {
    private int totalCount;
    private int rejectedCountSummation;
    private int statusCountSummation;

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
