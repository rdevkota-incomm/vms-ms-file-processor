package com.incomm.vms.fileprocess.repository;

import com.incomm.vms.fileprocess.model.FileUploadDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UploadDetailRepository {
    private final static Logger LOGGER = LoggerFactory.getLogger(UploadDetailRepository.class);

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
                "    SELECT  " +
                "        ?,  " +
                "        ?,  " +
                "        SUM(failure_count) + SUM(sucess_count) AS total_rec_count,  " +
                "        SUM(sucess_count),  " +
                "        SUM(failure_count),  " +
                "        NULL,  " +
                "        'Y',  " +
                "        ?,  " +
                "        SYSDATE,  " +
                "        ?,  " +
                "        SYSDATE  " +
                "    FROM  " +
                "        (  " +
                "            SELECT  " +
                "                COUNT(*) AS failure_count,  " +
                "                0 sucess_count,  " +
                "                vre_file_name AS file_name  " +
                "            FROM  " +
                "                vms_returnfile_error_data  " +
                "            WHERE  " +
                "                vre_file_name = 'Test_001.csv'  " +
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
                "        )";
        return jdbcTemplate.update(sql, instanceCode, fileName, userId, userId, fileName);
    }

    public int save(FileUploadDetail fileUploadDetail) {
        String sql = " INSERT INTO vms_return_fileupload_dtls ( "
                + " VRF_INST_CODE, VRF_FILE_NAME, VRF_TOTAL_RECCOUNT, "
                + " VRF_SUCCESS_RECCOUNT, VRF_FAILURE_RECCOUNT, "
                + " VRF_FAILURE_DESC , VRF_FILE_STATUS, "
                + " VRF_INS_USER , VRF_INS_DATE, VRF_LUPD_USER, VRF_LUPD_DATE) "
                + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, SYSDATE, ? , SYSDATE) ";
        LOGGER.debug("Save sql being executed    {}", sql);
        LOGGER.debug("Save sql Values:   {}", fileUploadDetail.toString());
        return jdbcTemplate.update(sql,
                fileUploadDetail.getInstanceCode(), fileUploadDetail.getFileName(),
                fileUploadDetail.getTotalRecCount(), fileUploadDetail.getSuccessRecCount(),
                fileUploadDetail.getFailureRecCount(), fileUploadDetail.getFailureDesc(),
                fileUploadDetail.getFileStatus(), fileUploadDetail.getUser(), fileUploadDetail.getUser());
    }

    public int update(FileUploadDetail fileUploadDetail) {

        return 0;
    }

    public FileUploadDetail findByFileName(String filename) {
        return null;
    }
}
