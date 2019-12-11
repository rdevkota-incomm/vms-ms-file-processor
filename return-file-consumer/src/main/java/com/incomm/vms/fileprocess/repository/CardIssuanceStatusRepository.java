package com.incomm.vms.fileprocess.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Date;

@Repository
public class CardIssuanceStatusRepository {
    private final static Logger LOGGER = LoggerFactory.getLogger(CardIssuanceStatusRepository.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public int update(String instanceCode, String panCode) {
        Date currentDate = new Date(System.currentTimeMillis());
        String sql = " UPDATE cms_cardissuance_status SET "
                + " ccs_card_status = '14', "
                + " ccs_lupd_date = ? "
                + " WHERE ccs_inst_code = ? "
                + " AND ccs_pan_code = ? "
                + " AND ccs_card_status = '3' ";

        return jdbcTemplate.update(sql, currentDate, instanceCode, panCode);
    }
}
