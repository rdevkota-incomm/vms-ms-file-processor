package com.incomm.vms.fileprocess.repository;

import com.incomm.vms.fileprocess.model.ReturnFileErrorData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ReturnFileErrorRepository {
    private final static Logger LOGGER = LoggerFactory.getLogger(ReturnFileErrorRepository.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public int save(ReturnFileErrorData returnFileErrorData) {
        String sql = " INSERT INTO vms_returnfile_error_data ("
                + " vre_file_name, vre_parent_order_id, vre_child_order_id,"
                + " vre_serial_number, vre_reject_code, vre_reject_reason,"
                + " vre_pan, vre_error_message ) VALUES (" +
                "  ?, ?, ?, ?, ?, ?, ?, ? )";

        return jdbcTemplate.update(sql,
                returnFileErrorData.getFileName(),
                returnFileErrorData.getParentOrderId(),
                returnFileErrorData.getChildOrderId(),
                returnFileErrorData.getSerialNumber(),
                returnFileErrorData.getRejectCode(),
                returnFileErrorData.getRejectReason(),
                returnFileErrorData.getPan(),
                returnFileErrorData.getErrorMessage()
        );
    }
}
