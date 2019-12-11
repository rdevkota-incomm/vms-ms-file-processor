package com.incomm.vms.fileprocess.repository;

import com.incomm.vms.fileprocess.model.OrderDetailAggregate;
import com.incomm.vms.fileprocess.model.OrderDetailCount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class OrderLineItemRepository {
    private final static Logger LOGGER = LoggerFactory.getLogger(OrderLineItemRepository.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public int update(OrderDetailAggregate orderDetailAggregate) {
        String sql = " UPDATE vms_order_lineitem SET "
                + " vol_order_status =  "
                + " CASE  WHEN ? = 0  THEN 'Rejected'"
                + " WHEN ? = ? THEN  'Printer_Acknowledged'"
                + " ELSE  vol_order_status"
                + " END, "
                + " vol_return_file_msg = ? "
                + " WHERE vol_order_id = ? "
                + " AND vol_line_item_id = ? "
                + " AND vol_partner_id = ? "
                + " AND ( vol_order_status NOT IN ( 'Completed', 'Shipped', 'Rejected', 'Shipping' ) "
                + " OR vol_order_status IS NULL )";
        LOGGER.debug("Query being executed {} with values {}", sql, orderDetailAggregate.toString());
        return jdbcTemplate.update(sql,
                orderDetailAggregate.getRejectCount(),
                orderDetailAggregate.getTotalCount(),
                orderDetailAggregate.getPrinterAcknowledgedCount(),
                orderDetailAggregate.getRejectReason(),
                orderDetailAggregate.getOrderId(),
                orderDetailAggregate.getLineItemId(),
                orderDetailAggregate.getPartnerId());
    }

    public OrderDetailCount getDetailCount(String orderId, String partnerId) {
        String sql = "SELECT COUNT(*), "
                + " SUM( CASE  WHEN vol_order_status <> 'Rejected' "
                + " THEN "
                + "  1 "
                + " ELSE "
                + "   0 "
                + " END), "
                + " SUM( CASE  WHEN vol_order_status "
                + " IN('Completed', 'Shipped', 'Printer_Acknowledged', 'Rejected', 'Shipping') "
                + " THEN "
                + "  1 "
                + " ELSE "
                + "  0 "
                + " END ) "
                + " FROM  vms_order_lineitem "
                + " WHERE vol_order_id = ? "
                + " AND vol_partner_id = ? ";

        return jdbcTemplate.queryForObject(sql, new Object[]{orderId, partnerId}, (rs, rowNum) -> {
            OrderDetailCount detailCount = new OrderDetailCount();
            detailCount.setTotalCount(rs.getInt(1));
            detailCount.setRejectedCountSummation(rs.getInt(2));
            detailCount.setStatusCountSummation(rs.getInt(3));
            return detailCount;
        });
    }
}
