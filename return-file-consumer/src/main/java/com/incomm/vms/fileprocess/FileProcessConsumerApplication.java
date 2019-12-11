package com.incomm.vms.fileprocess;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.incomm.vms.fileprocess.cache.FileAggregateSummaryStore;
import com.incomm.vms.fileprocess.model.FileAggregateSummary;
import com.incomm.vms.fileprocess.model.SummaryStoreCache;
import com.incomm.vms.fileprocess.repository.SummaryStoreRepository;
import com.incomm.vms.fileprocess.service.AggregateSchedulerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.lang.reflect.Type;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;

import static com.incomm.vms.fileprocess.config.Constants.AGGREGATE_SUMMARY_CACHE_KEY;

@SpringBootApplication
@ComponentScan("com.incomm.vms.fileprocess")
@EnableTransactionManagement
@EnableJdbcRepositories(basePackages = "com.incomm.vms.fileprocess.repository")
@EnableRedisRepositories(basePackages = "com.incomm.vms.fileprocess.repository")
@EnableCaching
@EnableScheduling
public class FileProcessConsumerApplication implements CommandLineRunner {
    private static Logger LOGGER = LoggerFactory.getLogger(FileProcessConsumerApplication.class);

    @Autowired
    private AggregateSchedulerService aggregateSchedulerService;

    @Autowired
    private SummaryStoreRepository summaryStoreRepository;

    public static void main(String[] args) {
        LOGGER.info("FileProcessConsumerApplication is being started");
        SpringApplication.run(FileProcessConsumerApplication.class, args);
    }

    @Override
    public void run(String... args) {
        try {
            final SummaryStoreCache retrivedSummary = summaryStoreRepository.findById(AGGREGATE_SUMMARY_CACHE_KEY).get();
            Gson gson = new Gson();
            Type summaryStoreType = new TypeToken<ConcurrentHashMap<String, FileAggregateSummary>>() {
            }.getType();
            ConcurrentHashMap<String, FileAggregateSummary> cache = gson.fromJson(retrivedSummary.getSummaryStore(), summaryStoreType);
            FileAggregateSummaryStore.setSummaryStore(cache);
            LOGGER.info("Got the summary {}", retrivedSummary.getSummaryStore());
            LOGGER.info("FileProcessConsumerApplication is started!!!! ");
        } catch (NoSuchElementException e) {
            LOGGER.debug("Not cache remains in the redis cache");
        }

    }

    @Scheduled(fixedDelayString = "${vms.printer-awk-agg.job.frequencyMilliSec}")
    public void cleanUpJob() {
        LOGGER.info("Clean up job is being started in every millisecond {}", "${vms.printer-awk-agg.job.frequencyMilliSec}");
        aggregateSchedulerService.processOrphanedData();
    }
}
