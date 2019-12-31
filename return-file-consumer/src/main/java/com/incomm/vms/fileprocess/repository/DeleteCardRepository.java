package com.incomm.vms.fileprocess.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import java.sql.Types;
import java.util.Map;

@Repository
public class DeleteCardRepository {
    private final static Logger LOGGER = LoggerFactory.getLogger(DeleteCardRepository.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public String deleteCard(String panCode) {

        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("delete_card")
                .declareParameters(
                        new SqlParameter("PAN_CODE", Types.VARCHAR),
                        new SqlOutParameter("RESPONSE_MESSAGE", Types.VARCHAR));

        SqlParameterSource in = new MapSqlParameterSource().addValue("PAN_CODE", panCode);
        Map<String, Object> simpleJdbcCallResult = jdbcCall.execute(in);

        LOGGER.debug("DeleteCardRepository result:{}", simpleJdbcCallResult);

        return simpleJdbcCallResult.get("p_resp_msg_out").toString();
    }

}
