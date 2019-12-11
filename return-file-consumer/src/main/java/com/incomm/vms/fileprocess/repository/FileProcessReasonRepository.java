package com.incomm.vms.fileprocess.repository;

import com.incomm.vms.fileprocess.model.RejectReasonMaster;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import static com.incomm.vms.fileprocess.config.Constants.RETURN_REASON_CACHE_NAME;

@Repository
public class FileProcessReasonRepository {
    private final static Logger LOGGER = LoggerFactory.getLogger(FileProcessReasonRepository.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Cacheable(RETURN_REASON_CACHE_NAME)
    public RejectReasonMaster findByRejectReason(String rejectReason) {
        RejectReasonMaster fileProcessReason = new RejectReasonMaster();
        String sql = " SELECT vfr_reject_code, vfr_success_failure_flag "
                + " FROM vms_fileprocess_rjreason_mast "
                + "  WHERE upper(vfr_reject_reason) = upper(?)";
        LOGGER.debug("Select sql being executed {}", sql);
        LOGGER.debug("Select sql Parameter Values for rejectReason: {} ", rejectReason);
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{rejectReason},
                    (rs, rowNum) -> {
                        fileProcessReason.setRejectCode(rs.getString(1));
                        fileProcessReason.setSuccessFailureFlag(rs.getString(2));
                        fileProcessReason.setRejectReason(rejectReason);

                        return fileProcessReason;
                    });
        } catch (EmptyResultDataAccessException e) {
            LOGGER.debug("no record returned by the reject reason sql: {} for reason {}", sql, rejectReason);
            fileProcessReason.setSuccessFailureFlag("N");
            fileProcessReason.setRejectReason("Rejected Reason - " + rejectReason);
        }
        return fileProcessReason;
    }
}
