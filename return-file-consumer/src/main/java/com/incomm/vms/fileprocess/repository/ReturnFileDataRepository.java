package com.incomm.vms.fileprocess.repository;

import com.incomm.vms.fileprocess.model.LineItemDetail;
import com.incomm.vms.fileprocess.model.ReturnFileDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ReturnFileDataRepository {
    private final static Logger LOGGER = LoggerFactory.getLogger(ReturnFileDataRepository.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public int save(String instanceCode, String fileName, String rowId,
                    ReturnFileDTO returnFileRecord, LineItemDetail lineItemDetail) {

        String sql = " INSERT INTO vms_returnfile_data ("
                + " vrd_inst_code, vrd_file_name, vrd_customer_desc,"
                + " vrd_ship_suffix_no, vrd_parent_order_id, vrd_child_order_id,"
                + " vrd_serial_number, vrd_reject_code, vrd_reject_reason,"
                + " vrd_file_date, vrd_card_type, vrd_client_order_id, vrd_process_flag,"
                + " vrd_row_id, vrd_error_desc, vrd_ins_user, vrd_ins_date,"
                + " vrd_lupd_user, vrd_lupd_date, vrd_lineitem_id ) "
                + " VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, to_date(?, 'mm/dd/yyyy'), ?, ?, ?, ?, ?, ?, SYSDATE, ?, SYSDATE, ?)";

        return jdbcTemplate.update(sql,
                instanceCode, fileName, returnFileRecord.getCustomer(),
                returnFileRecord.getShipSuffix(), returnFileRecord.getParentOrderId(),
                returnFileRecord.getChildOrderId(), returnFileRecord.getSerialNumber(),
                returnFileRecord.getRejectCode(), returnFileRecord.getRejectReason(),
                returnFileRecord.getFileDate(), returnFileRecord.getCardType(),
                returnFileRecord.getClientOrderId(), "P", rowId, "Success", "1",
                "1", lineItemDetail.getLineItemId());
    }
}
