package com.incomm.vms.fileprocess.repository;

import com.incomm.vms.fileprocess.model.OrderDetailAggregate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class OrderAggregateRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

//    public List<OrderDetailAggregate> getLineItemSummary1(List<String> panCodeList) {
//        MapSqlParameterSource parameters = new MapSqlParameterSource();
//        parameters.addValue("panCodeList", panCodeList);
//        String[] panList = panCodeList.stream().toArray(String[]::new);
//        String sql = "SELECT "
//                + "    COUNT(*) AS total_count, "
//                + "    SUM( "
//                + "        CASE "
//                + "            WHEN vli_status IN( "
//                + "                'Completed', 'Shipped', 'Printer_Acknowledged', 'Rejected' "
//                + "            ) THEN "
//                + "                1 "
//                + "            ELSE "
//                + "                0 "
//                + "        END "
//                + "    ) AS printer_acknowledged_count, "
//                + "    COUNT( "
//                + "        CASE "
//                + "            WHEN vli_reject_reason IN( "
//                + "                SELECT "
//                + "                    vfr_reject_reason "
//                + "                FROM "
//                + "                    vms_fileprocess_rjreason_mast "
//                + "                WHERE "
//                + "                    vfr_success_failure_flag = 'Y' "
//                + "            ) THEN "
//                + "                1 "
//                + "        END "
//                + "    ) AS reject_count, "
//                + "    MAX(vli_reject_reason) AS vli_reject_reason, "
//                + "    vli_order_id, "
//                + "    vli_partner_id, "
//                + "    vli_lineitem_id "
//                + " FROM vms_line_item_dtl "
//                +  "WHERE vli_pan_code = ANY(?) "
//                + " GROUP BY "
//                + "    vli_order_id, "
//                + "    vli_partner_id, "
//                + "    vli_lineitem_id";
//        return jdbcTemplate.query(sql, panList, (rs, rowNum) -> {
//            OrderDetailAggregate agg = new OrderDetailAggregate();
//            agg.setTotalCount(rs.getInt(1));
//            agg.setPrinterAcknowledgedCount(rs.getInt(2));
//            agg.setRejectCount(rs.getInt(3));
//            agg.setRejectReason(rs.getString(4));
//            agg.setOrderId(rs.getString(5));
//            agg.setPartnerId(rs.getString(6));
//            agg.setLineItemId(rs.getString(7));
//            return agg;
//        });
//    }

    public List<OrderDetailAggregate> getLineItemSummary(List<String> panCodeList) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("panCodeList", panCodeList);
        String sql = "SELECT "
                + "    COUNT(*) AS total_count, "
                + "    SUM( "
                + "        CASE "
                + "            WHEN vli_status IN( "
                + "                'Completed', 'Shipped', 'Printer_Acknowledged', 'Rejected' "
                + "            ) THEN "
                + "                1 "
                + "            ELSE "
                + "                0 "
                + "        END "
                + "    ) AS printer_acknowledged_count, "
                + "    COUNT( "
                + "        CASE "
                + "            WHEN vli_reject_reason IN( "
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
                +  "WHERE vli_pan_code = ANY(:panCodeList) "
                + " GROUP BY "
                + "    vli_order_id, "
                + "    vli_partner_id, "
                + "    vli_lineitem_id";
        return namedParameterJdbcTemplate.query(sql, parameters, (rs, rowNum) -> toOrderDetailAggregate(rs));

    }

    private OrderDetailAggregate toOrderDetailAggregate(ResultSet rs) throws SQLException {
        OrderDetailAggregate agg = new OrderDetailAggregate();
            agg.setTotalCount(rs.getInt(1));
            agg.setPrinterAcknowledgedCount(rs.getInt(2));
            agg.setRejectCount(rs.getInt(3));
            agg.setRejectReason(rs.getString(4));
            agg.setOrderId(rs.getString(5));
            agg.setPartnerId(rs.getString(6));
            agg.setLineItemId(rs.getString(7));
            return agg;
    }

}
