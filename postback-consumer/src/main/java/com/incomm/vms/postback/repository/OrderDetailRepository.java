package com.incomm.vms.postback.repository;

import com.incomm.vms.postback.model.Card;
import com.incomm.vms.postback.model.LineItem;
import com.incomm.vms.postback.model.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.incomm.vms.postback.config.Constants.*;

@Repository
public class OrderDetailRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Order getOrderStatus(String orderId, String partnerId) {

        String sql = "SELECT  vod_order_id,vod_order_status, vod_shipping_method, vod_ofac_flag, " +
                "    vol_line_item_id, vol_order_status, vol_return_file_msg, ( " +
                "        SELECT vfr_reject_code " +
                "        FROM vms_fileprocess_rjreason_mast " +
                "        WHERE vfr_reject_reason = upper(vol_return_file_msg) " +
                "    ) AS resp_code " +
                " FROM  vms_order_details, vms_order_lineitem " +
                " WHERE  vol_order_id = vod_order_id " +
                " AND vod_partner_id = vol_partner_id " +
                " AND vol_order_id = ? " +
                " AND vod_partner_id = ? ";

        List<Map<String, Object>> records = jdbcTemplate.queryForList(sql, orderId, partnerId);

        Order order = Order.builder().orderId(orderId).partnerId(partnerId).build();
        List<LineItem> lineItems = new ArrayList<>();

        for (Map record : records) {
            String orderStatus = (String) record.get("vod_order_status");
            order.setStatus(orderStatus);
            order.setResponseCode(orderStatus.equalsIgnoreCase(ORDER_REJECT_STATUS) ? FSAPI_REJECT_RESPONSE_CODE : FSAPI_SUCCESS_RESPONSE_CODE);
            order.setShippingMethod((String) record.get("vod_shipping_method"));
            order.setOfacFlag((String) record.get("vod_ofac_flag"));

            LineItem lineItem = LineItem.builder()
                    .lineItemId((String) record.get("vol_line_item_id"))
                    .status((String) record.get("vol_order_status"))
                    .responseMessage((String) record.get("vol_return_file_msg"))
                    .responseCode((String) record.get("resp_code"))
                    .build();

            lineItems.add(lineItem);
        }
        order.setLineItem(lineItems);
        return order;
    }

    public List<Card> getCardDetails(String orderId, String partnerId, String lineItemId) {
        String sql = "SELECT  cap_proxy_number as proxyNumber, cap_serial_number as serialNumber,  " +
                "  cap_card_stat as status,  vli_pin as pin, vli_proxy_pin_encr as encryptedString,  " +
                "  vli_tracking_no as trackingNumber, vli_shipping_datetime as shippingDateTime,  " +
                "  cap_cust_code as custCode,  cap_prod_code as prodCode,  cap_card_type as cardType,  " +
                "  fn_dmaps_main(cap_pan_code_encr) as cardNumber, cap_pan_code as panCode,  " +
                "  TO_CHAR(cap_expry_date, 'YYYY-MM-DD') as expirationDate, cap_pan_code_encr as encryptCardNumber, " +
                "  vli_printer_response as  printerResponse" +
                " FROM   vms_order_details, vms_order_lineitem, vms_line_item_dtl, cms_appl_pan  " +
                "WHERE  " +
                "    vod_order_id = vol_order_id  " +
                "    AND vod_partner_id = vol_partner_id  " +
                "    AND vod_order_id = vli_order_id  " +
                "    AND vod_partner_id = vli_partner_id  " +
                "    AND vol_line_item_id = vli_lineitem_id  " +
                "    AND cap_pan_code (+) = vli_pan_code  " +
                "    AND vod_order_id = ?  " +
                "    AND vol_line_item_id = ?  " +
                "    AND vod_partner_id = ?";

        RowMapper rowMapper = BeanPropertyRowMapper.newInstance(Card.class);
        List cards = jdbcTemplate.query(sql, new Object[]{orderId, lineItemId, partnerId}, rowMapper);
        return cards;
    }

}
