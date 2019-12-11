package com.incomm.vms.fileprocess.repository;

import com.incomm.vms.fileprocess.model.OrderDetailCount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class OrderDetailRepository {
    private final static Logger LOGGER = LoggerFactory.getLogger(OrderDetailRepository.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public int update(OrderDetailCount detailCount, String orderId, String partnerId) {
        String sql = " UPDATE vms_order_details "
                + " SET vod_order_status = "
                + "   CASE "
                + "     WHEN ? = 0  THEN 'Rejected' "
                + "     WHEN ? = ?  THEN 'Printer_Acknowledged' "
                + "     ELSE vod_order_status "
                + "   END "
                + " WHERE vod_order_id = ? "
                + " AND vod_partner_id = ? "
                + " AND ( vod_order_status NOT IN ('Completed','Shipped','Rejected','Shipping' ) "
                + " OR vod_order_status IS NULL )";
        return jdbcTemplate.update(sql, detailCount.getRejectedCountSummation(),
                detailCount.getStatusCountSummation(),
                detailCount.getTotalCount(), orderId, partnerId);

    }
}
