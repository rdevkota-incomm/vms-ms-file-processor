package com.incomm.vms.fileprocess.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class CardIssuanceStatusRepository {
    private final static Logger LOGGER = LoggerFactory.getLogger(CardIssuanceStatusRepository.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public int updateCardStatus(String instanceCode, String panCode) {
        String sql = " UPDATE cms_cardissuance_status SET "
                + " ccs_card_status = '14', "
                + " ccs_lupd_date = SYSDATE "
                + " WHERE ccs_inst_code = ? "
                + " AND ccs_pan_code = ? "
                + " AND ccs_card_status = '3' ";
        LOGGER.debug("Executing sql {}", sql);
        return jdbcTemplate.update(sql, instanceCode, panCode);
    }
}
