package com.incomm.vms.fileprocess.config;

import com.incomm.vms.fileprocess.cache.FileAggregateSummaryStore;
import com.incomm.vms.fileprocess.repository.SummaryStoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class StaticContextInitializer {
    @Autowired
    SummaryStoreRepository summaryStoreRepository;

    @Autowired
    private ApplicationContext context;

    @PostConstruct
    public void init() {
        FileAggregateSummaryStore.setSummaryStoreRepository(summaryStoreRepository);
    }
}
