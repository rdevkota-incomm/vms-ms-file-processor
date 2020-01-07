package com.incomm.vms.fileprocess.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UploadDetailRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(UploadDetailRepository.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public int aggregateFileDetail(String instanceCode, String userId, String fileName) {
        String sql = "INSERT INTO vms_return_fileupload_dtls (  " +
                "    vrf_inst_code,  " +
                "    vrf_file_name,  " +
                "    vrf_total_reccount,  " +
                "    vrf_success_reccount,  " +
                "    vrf_failure_reccount,  " +
                "    vrf_failure_desc,  " +
                "    vrf_file_status,  " +
                "    vrf_ins_user,  " +
                "    vrf_ins_date,  " +
                "    vrf_lupd_user,  " +
                "    vrf_lupd_date )  " +
                "    SELECT  " + instanceCode + ",  " +
                "        file_name,  " +
                "        SUM(failure_count) + SUM(success_count) AS total_rec_count,  " +
                "        SUM(success_count),  " +
                "        SUM(failure_count),  " +
                "        NULL,  " +
                "        'Y',  " + userId + " ,  " +
                "        SYSDATE,  " + userId + ",  " +
                "        SYSDATE  " +
                "    FROM  " +
                "        (  " +
                "            SELECT  " +
                "                COUNT(*) AS failure_count,  " +
                "                0 success_count,  " +
                "                vre_file_name AS file_name  " +
                "            FROM  " +
                "                vms_returnfile_error_data  " +
                "            WHERE  " +
                "                vre_file_name = ? " +
                "            GROUP BY  " +
                "                vre_file_name  " +
                "            UNION ALL  " +
                "            SELECT  " +
                "                0 AS failure_count,  " +
                "                COUNT(*) AS success_count,  " +
                "                vrd_file_name AS file_name  " +
                "            FROM  " +
                "                vms_returnfile_data  " +
                "            WHERE  " +
                "                vrd_file_name = ? " +
                "            GROUP BY  " +
                "                vrd_file_name  " +
                "        ) GROUP BY file_name";

        LOGGER.debug("Executing query \n {}", sql);

        return jdbcTemplate.update(sql, fileName, fileName);
    }

}
