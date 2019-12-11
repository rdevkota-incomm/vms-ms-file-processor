package com.incomm.vms.fileprocess.cache;

import com.google.gson.Gson;
import com.incomm.vms.fileprocess.model.FileAggregateSummary;
import com.incomm.vms.fileprocess.model.LineItemDetail;
import com.incomm.vms.fileprocess.model.SummaryStoreCache;
import com.incomm.vms.fileprocess.repository.SummaryStoreRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import static com.incomm.vms.fileprocess.config.Constants.AGGREGATE_SUMMARY_CACHE_KEY;

public class FileAggregateSummaryStore {
    private static final int DEFAULT_TOTAL_PRODUCED_RECORD_COUNT = 1000000;
    private static Logger LOGGER = LoggerFactory.getLogger(FileAggregateSummaryStore.class);
    private static SummaryStoreRepository summaryStoreRepository;

    public static void setSummaryStoreRepository(SummaryStoreRepository summaryStoreRepository) {
        FileAggregateSummaryStore.summaryStoreRepository = summaryStoreRepository;
    }

    private static ConcurrentHashMap<String, FileAggregateSummary> summaryStore;

    static {
        summaryStore = new ConcurrentHashMap<>();
    }

    public static void updateProducedRecordCount(String correlationId, String fileName, int totalRecordCount) {
        LOGGER.info("Total message count:{} for file with correlationId:{} filename:{}", totalRecordCount,
                correlationId, fileName);
        summaryStore.computeIfPresent(correlationId, (k, v) -> {
            v.setCompletionTime(getSystemTime());
            v.setFileName(fileName);
            v.setTotalProducedRecordCount(totalRecordCount);
            return v;
        });

        summaryStore.computeIfAbsent(correlationId, v -> {
            FileAggregateSummary summary = new FileAggregateSummary();
            summary.setLineItemDetails(new ArrayList<>());
            summary.setFileName(fileName);
            summary.setCompletionTime(getSystemTime());
            summary.setTotalProducedRecordCount(totalRecordCount);
            summary.setTotalConsumedRecordCount(0);
            return summary;
        });
        syncCache();
    }

    public static void updateConsumedFailedRecordCount(String correlationId, String fileName) {
        summaryStore.computeIfPresent(correlationId, (k, v) -> {
            v.setCompletionTime(getSystemTime());
            v.setCompletionTime(getSystemTime());
            int consumedCount = v.getTotalConsumedRecordCount();
            v.setTotalConsumedRecordCount(consumedCount + 1);
            return v;
        });

        summaryStore.computeIfAbsent(correlationId, v -> {
            FileAggregateSummary summary = new FileAggregateSummary();
            summary.setLineItemDetails(new ArrayList<>());
            summary.setCompletionTime(getSystemTime());
            summary.setFileName(fileName);
            summary.setTotalProducedRecordCount(DEFAULT_TOTAL_PRODUCED_RECORD_COUNT);
            summary.setTotalConsumedRecordCount(1);
            return summary;
        });

        syncCache();
    }

    public static void updateConsumedRecordCount(String correlationId, LineItemDetail lineItemDetail, String fileName) {
        summaryStore.computeIfPresent(correlationId, (k, v) -> {
            int consumedCount = v.getTotalConsumedRecordCount();
            v.setTotalConsumedRecordCount(consumedCount + 1);
            v.setCompletionTime(getSystemTime());
            v.setLineItemDetail(lineItemDetail);
            return v;
        });

        summaryStore.computeIfAbsent(correlationId, v -> {
            FileAggregateSummary summary = new FileAggregateSummary();
            summary.setLineItemDetails(new ArrayList<>());
            summary.setCompletionTime(getSystemTime());
            summary.setFileName(fileName);
            summary.setTotalConsumedRecordCount(1);
            summary.setTotalProducedRecordCount(DEFAULT_TOTAL_PRODUCED_RECORD_COUNT);
            summary.setLineItemDetail(lineItemDetail);

            return summary;
        });
        syncCache();
    }

    public static FileAggregateSummary getSummaryStore(String correlationId) {
        return summaryStore.get(correlationId);
    }

    public static void setSummaryStore(ConcurrentHashMap<String, FileAggregateSummary> summaryStore) {
        FileAggregateSummaryStore.summaryStore = summaryStore;
    }

    public static ConcurrentHashMap<String, FileAggregateSummary> getAllSummaryStore() {
        return summaryStore;
    }

    public static void evictCache(String correlationId) {
        summaryStore.remove(correlationId);
        syncCache();
    }

    public static void syncCache() {
        Gson gson = new Gson();
        String payload = gson.toJson(FileAggregateSummaryStore.getAllSummaryStore());
        try {
            SummaryStoreCache cache = new SummaryStoreCache(AGGREGATE_SUMMARY_CACHE_KEY, (payload == null ? "{}" : payload));
            summaryStoreRepository.save(cache);

        } catch (Exception e) {
            LOGGER.warn("Redis exception occured: {}", e.getLocalizedMessage(), e);
        }
    }

    private static long getSystemTime() {
        return System.currentTimeMillis();
    }
}