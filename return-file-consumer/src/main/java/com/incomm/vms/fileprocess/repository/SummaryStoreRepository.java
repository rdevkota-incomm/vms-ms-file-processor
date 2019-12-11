package com.incomm.vms.fileprocess.repository;

import com.incomm.vms.fileprocess.model.SummaryStoreCache;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SummaryStoreRepository extends CrudRepository<SummaryStoreCache, String> {
}
