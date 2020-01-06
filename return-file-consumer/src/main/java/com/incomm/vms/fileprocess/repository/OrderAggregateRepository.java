package com.incomm.vms.fileprocess.repository;

import com.incomm.vms.fileprocess.model.LineItemDetail;
import com.incomm.vms.fileprocess.model.OrderDetailAggregate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class OrderAggregateRepository {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public List<OrderDetailAggregate> getLineItemSummary(LineItemDetail lineItemDetail) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("orderId", lineItemDetail.getOrderId());
        parameters.addValue("partnerId", lineItemDetail.getPartnerId());
        String sql = "SELECT "
                + "    COUNT(*) AS total_count, "
                + "    SUM( "
                + "        CASE "
                + "            WHEN vli_status IN ( "
                + "                'Completed', 'Shipped', 'Printer_Acknowledged', 'Rejected' "
                + "            ) THEN "
                + "                1 "
                + "            ELSE "
                + "                0 "
                + "        END "
                + "    ) AS printer_acknowledged_count, "
                + "    COUNT( "
                + "        CASE "
                + "            WHEN vli_reject_reason IN ( "
                + "                SELECT "
                + "                    vfr_reject_reason "
                + "                FROM "
                + "                    vms_fileprocess_rjreason_mast "
                + "                WHERE "
                + "                    vfr_success_failure_flag = 'Y' "
                + "            ) THEN "
                + "                1 "
                + "        END "
                + "    ) AS reject_count, "
                + "    MAX(vli_reject_reason) AS vli_reject_reason, "
                + "    vli_order_id, "
                + "    vli_partner_id, "
                + "    vli_lineitem_id "
                + " FROM vms_line_item_dtl "
                + " WHERE vli_order_id = :orderId AND  vli_partner_id = :partnerId "
                + " GROUP BY "
                + "    vli_order_id, "
                + "    vli_partner_id, "
                + "    vli_lineitem_id";

        return namedParameterJdbcTemplate.query(sql, parameters, (rs, rowNum) -> toOrderDetailAggregate(rs));

    }

    private OrderDetailAggregate toOrderDetailAggregate(ResultSet rs) throws SQLException {
        return OrderDetailAggregate.builder()
                .totalCount(rs.getInt(1))
                .printerAcknowledgedCount(rs.getInt(2))
                .rejectCount(rs.getInt(3))
                .rejectReason(rs.getString(4))
                .orderId(rs.getString(5))
                .partnerId(rs.getString(6))
                .lineItemId(rs.getString(7))
                .build();
    }

}
