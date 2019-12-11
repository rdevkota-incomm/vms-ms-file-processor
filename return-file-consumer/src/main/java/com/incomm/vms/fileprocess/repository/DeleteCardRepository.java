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
import java.util.List;
import java.util.Map;

@Repository
public class DeleteCardRepository {
    private final static Logger LOGGER = LoggerFactory.getLogger(DeleteCardRepository.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public String deleteCard(String panCode) {

        SimpleJdbcCall jdbcCall =  new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("delete_card")
                .declareParameters(
                        new SqlParameter("PAN_CODE", Types.VARCHAR),
                        new SqlOutParameter("RESPONSE_MESSAGE", Types.VARCHAR));

        SqlParameterSource in = new MapSqlParameterSource().addValue("PAN_CODE", panCode );
        Map<String, Object> simpleJdbcCallResult = jdbcCall.execute(in);
        return simpleJdbcCallResult.get("p_resp_msg_out").toString();
    }

    public String deleteCards(List<String> cardPanList) {

        SimpleJdbcCall jdbcCall =  new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("delete_card")
//                .withCatalogName("VMSB2BAPI")
                .declareParameters(
                        new SqlParameter("PAN_CODE", Types.VARCHAR),
                        new SqlOutParameter("RESPONSE_MESSAGE", Types.VARCHAR));

        SqlParameterSource in = new MapSqlParameterSource().addValue("PAN_CODE", "pW99IjxXwjjyqfdSHwUEqkNxtVyDkROltGHN7Cuuplc=" );
        Map<String, Object> simpleJdbcCallResult = jdbcCall.execute(in);
        return simpleJdbcCallResult.get("p_resp_msg_out").toString();
    }
//    public String delete(List<String> cardPanList) {
//        String[] panList = cardPanList.stream().toArray(String[]::new);
//
//
//
//        List<SqlParameter> parameters = Arrays.asList(new SqlParameter(OracleTypes.ARRAY),
//                new SqlOutParameter("p_resp_msg_out", Types.VARCHAR));
//        Map<String, Object> callResult = jdbcTemplate.call(new CallableStatementCreator() {
//            @Override
//            public CallableStatement createCallableStatement(Connection con) throws SQLException {
////                Array aArray = con.createArrayOf("VARCHAR", panList);
//                ArrayDescriptor des = ArrayDescriptor.createDescriptor("p_card_nos_in", con);
//                Array array = new ARRAY(des, con, cardPanList.toArray());
//                CallableStatement callableStatement = con.prepareCall("{call vmsb2bapi.delete_cards (?, ?)}");
//                callableStatement.setArray(1, array);
//                callableStatement.registerOutParameter(2, Types.VARCHAR);
//                return callableStatement;
//            }
//        }, parameters);
//
////        String[] panList = cardPanList.stream().toArray(String[]::new);
////        SimpleJdbcCall jdbcCall =  new SimpleJdbcCall(jdbcTemplate)
////                .withProcedureName("delete_cards")
////                .withCatalogName("VMSB2BAPI")
////                .declareParameters(
////                        new SqlParameter("p_card_nos_in", OracleTypes.ARRAY, "shuffle_array_typ"),
////                        new SqlOutParameter("p_resp_msg_out", Types.VARCHAR));
////
////        SqlParameterSource in = new MapSqlParameterSource().addValue("p_card_nos_in", cardPanList );
////        Map<String, Object> simpleJdbcCallResult = jdbcCall.execute(in);
//        return callResult.get("p_resp_msg_out").toString();
//    }

//    public String delete(List<String> cardPanList) {
////        String outValue;
////        ArrayDescriptor  arrayDescriptor = ArrayDescriptor.createDescriptor("shuffle_array_typ");
//        String[] panList = cardPanList.stream().toArray(String[]::new);
//        SimpleJdbcCall jdbcCall =  new SimpleJdbcCall(jdbcTemplate)
//                .withProcedureName("delete_cards")
//                .withCatalogName("VMSB2BAPI")
//                .declareParameters(
//                        new SqlParameter("p_card_nos_in", OracleTypes.ARRAY, "shuffle_array_typ"),
//                        new SqlOutParameter("p_resp_msg_out", Types.VARCHAR));
//
//        SqlParameterSource in = new MapSqlParameterSource().addValue("p_card_nos_in", cardPanList );
//        Map<String, Object> simpleJdbcCallResult = jdbcCall.execute(in);
//        return simpleJdbcCallResult.get("p_resp_msg_out").toString();
//    }

//    public String delete1(List<String> cardPanList) {
//
//        SqlParameter fNameParam = new SqlParameter(Types.ARRAY);
//        SqlOutParameter outParameter = new SqlOutParameter("p_resp_msg_out", Types.VARCHAR);
//
//        List<SqlParameter> paramList = new ArrayList();
//
//        paramList.add(fNameParam);
//        paramList.add(outParameter);
//
//        final String procedureCall = "{call vmsb2bapi.delete_cards(?, ?)}";
//        Map<String, Object> resultMap = jdbcTemplate.call(new CallableStatementCreator() {
//            @Override
//            public CallableStatement createCallableStatement(Connection connection) throws SQLException {
//                Array cardlistArray = connection.createArrayOf("VARCHAR", cardPanList.toArray());
//                CallableStatement callableStatement = connection.prepareCall(procedureCall);
//                callableStatement.setArray(1, cardlistArray);
//                callableStatement.registerOutParameter(3, Types.VARCHAR);
//                return callableStatement;
//
//            }
//        }, paramList);
//        System.out.println(resultMap.get("p_resp_msg_out"));
//        return resultMap.get("p_resp_msg_out").toString();
//    }
}
