package com.incomm.vms.fileprocess.model;

import com.google.gson.Gson;
import org.springframework.data.redis.core.RedisHash;

import javax.persistence.Id;
import java.io.Serializable;

import static com.incomm.vms.fileprocess.config.Constants.AGGREGATE_SUMMARY_CACHE_KEY;

@RedisHash(AGGREGATE_SUMMARY_CACHE_KEY)
public class SummaryStoreCache implements Serializable {
    @Id
    private String id;
    private String summaryStore;

    public SummaryStoreCache(String id, String summaryStore) {
        this.id = id;
        this.summaryStore = summaryStore;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSummaryStore() {
        return summaryStore;
    }

    public void setSummaryStore(String summaryStore) {
        this.summaryStore = summaryStore;
    }

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
