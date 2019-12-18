package com.incomm.vms.fileprocess.repository;

import com.incomm.vms.fileprocess.model.OrderDetailCount;
import com.incomm.vms.fileprocess.model.PostBackInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

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

    public Optional<PostBackInfo> findPostBackInfo(String orderId, String partnerId) {
        String sql = "SELECT vod_postback_response, vod_postback_url " +
                " FROM vms_order_details " +
                " WHERE vod_order_id = ? " +
                " AND vod_partner_id = ? " +
                " AND upper(vod_print_order) = 'TRUE' ";
        PostBackInfo postbackInfo = null;
        try {
            postbackInfo = jdbcTemplate.queryForObject(sql, new Object[]{orderId, partnerId},
                    (resultSet, rowNum) -> {
                        PostBackInfo info = new PostBackInfo();
                        info.setReponse(resultSet.getString(1));
                        info.setUrl(resultSet.getString(2));
                        return info;
                    });
        } catch (EmptyResultDataAccessException e) {
            LOGGER.debug("No post back needed for orderId:{} partnerId:{}", orderId, partnerId);
        }
        return Optional.ofNullable(postbackInfo);
    }
}
