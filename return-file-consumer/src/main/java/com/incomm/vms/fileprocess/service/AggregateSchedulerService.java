package com.incomm.vms.fileprocess.service;

import com.incomm.vms.fileprocess.cache.FileAggregateSummaryStore;
import com.incomm.vms.fileprocess.model.FileAggregateSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class AggregateSchedulerService {
    private static Logger LOGGER = LoggerFactory.getLogger(AggregateSchedulerService.class);
    @Autowired
    private FileAggregationService fileAggregationService;

    @Value("${vms.printer-awk-agg.job.frequencyMilliSec}")
    private long jobFrequencyInMilliSec;

    public void processOrphanedData() {
        LOGGER.info("Looking for orphaned aggregate jobs");
        ConcurrentHashMap<String, FileAggregateSummary> summaryStore = FileAggregateSummaryStore.getAllSummaryStore();

        summaryStore.forEach((correlationId, fileAggregateSummary) -> {
            if (System.currentTimeMillis() >= fileAggregateSummary.getCompletionTime() + jobFrequencyInMilliSec) {
                LOGGER.info("Cleaning up orphaned job for correlation Id: {} and file {}", correlationId, fileAggregateSummary.getFileName());
                aggregateOrphanedData(correlationId);
            }
        });

    }

    private void aggregateOrphanedData(String correlationId) {
        LOGGER.info("Found and Processing orphaned aggregate job for Correlation Id {}", correlationId);
        fileAggregationService.aggregateSummary(correlationId);
    }
}
