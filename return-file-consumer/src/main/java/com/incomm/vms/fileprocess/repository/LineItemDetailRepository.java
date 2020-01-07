package com.incomm.vms.fileprocess.repository;

import com.incomm.vms.fileprocess.model.LineItemDetail;
import com.incomm.vms.fileprocess.model.OrderDetailAggregate;
import com.incomm.vms.fileprocess.model.RejectReasonMaster;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class LineItemDetailRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(LineItemDetailRepository.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Optional<LineItemDetail> findLineItem(String instanceCode, String serialNumber) {
        LineItemDetail detail = null;
        String sql = " SELECT  cap_pan_code, vli_partner_id, vli_parent_oid, vli_order_id, vli_lineitem_id"
                + " FROM ( SELECT  pan.cap_pan_code, lineitem.vli_partner_id,lineitem.vli_parent_oid,"
                + " lineitem.vli_order_id, lineitem.vli_lineitem_id"
                + " FROM  vms_line_item_dtl lineitem, cms_appl_pan pan"
                + " WHERE pan.cap_form_factor IS NULL "
                + " AND pan.cap_pan_code = lineitem.vli_pan_code (+)"
                + " AND pan.cap_card_stat <> '9' "
                + " AND pan.cap_inst_code = ? "
                + " AND pan.cap_mbr_numb = '000'"
                + " AND pan.cap_serial_number = ? "
                + " ORDER BY pan.cap_pangen_date DESC ) "
                + " WHERE ROWNUM = 1 ";
        LOGGER.debug("Select sql being executed \n {}", sql);
        LOGGER.debug("Select sql Parameter Values for serialNumber: {} instanceCode: {}",
                serialNumber, instanceCode);
        try {
            detail = jdbcTemplate.queryForObject(sql, new Object[]{instanceCode, serialNumber},
                    (rs, rowNum) -> {
                        LineItemDetail lineItemDetail = new LineItemDetail();
                        lineItemDetail.setPanCode(rs.getString(1));
                        lineItemDetail.setPartnerId(rs.getString(2));
                        lineItemDetail.setParentOId(rs.getString(3));
                        lineItemDetail.setOrderId((rs.getString(4)));
                        lineItemDetail.setLineItemId(rs.getString(5));

                        return lineItemDetail;
                    });
        } catch (EmptyResultDataAccessException e) {
            LOGGER.debug("There is no record returned by the sql: {} ", sql);
            LOGGER.debug("Select sql Parameter Values for serialNumber: {} instanceCode: {}",
                    serialNumber, instanceCode);
        } catch (BadSqlGrammarException e) {
            LOGGER.error("Bad syntax error for lineItemDetail query {} ", e.getSql(), e);
        }

        return Optional.ofNullable(detail);
    }

    public int updateStatus(String serialNUmber, String panCode, RejectReasonMaster fileProcessReason) {
        String sql = " UPDATE vms_line_item_dtl SET "
                + " vli_reject_code = ? ,"
                + " vli_reject_reason = ? ,"
                + " vli_serial_number = ? ,"
                + " vli_printer_response = ? , "
                + " vli_status = "
                + " CASE  WHEN ( vli_status NOT IN ( 'Completed', 'Shipped' ) OR vli_status IS NULL ) THEN "
                + " DECODE( ? , 'Y', 'Printer_Acknowledged', 'N', 'Rejected', vli_status) "
                + " ELSE "
                + " vli_status "
                + " END "
                + " WHERE vli_pan_code = ? ";

        return jdbcTemplate.update(sql,
                fileProcessReason.getRejectCode(),
                fileProcessReason.getRejectReason(), serialNUmber,
                (fileProcessReason.getSuccessFailureFlag().equals("N") ? " Failed - " : "Success - ") + fileProcessReason.getRejectReason(),
                fileProcessReason.getSuccessFailureFlag(),
                panCode);
    }

    public OrderDetailAggregate getDetailAggregateCount(LineItemDetail lineItemDetail) {
        String sql = "SELECT COUNT(*) AS total_count, "
                + " SUM( "
                + "        CASE "
                + "            WHEN vli_status IN( 'Completed', 'Shipped', 'Printer_Acknowledged', 'Rejected' "
                + "            ) THEN "
                + "                1 "
                + "            ELSE "
                + "                0 "
                + "        END "
                + "    ) AS printer_acknowledged_count, "
                + "    vli_order_id, "
                + "    vli_partner_id "
                + " FROM vms_line_item_dtl "
                + " WHERE  vli_order_id = ? "
                + "   AND vli_partner_id = ? "
                + " GROUP BY vli_order_id,  vli_partner_id ";
        return jdbcTemplate.queryForObject(sql, new Object[]{lineItemDetail.getOrderId(), lineItemDetail.getPartnerId()},
                (rs, rowNum) -> OrderDetailAggregate.builder()
                        .totalCount(rs.getInt(1))
                        .printerAcknowledgedCount(rs.getInt(2))
                        .orderId(rs.getString(3))
                        .partnerId(rs.getString(4))
                        .build()
        );
    }

}