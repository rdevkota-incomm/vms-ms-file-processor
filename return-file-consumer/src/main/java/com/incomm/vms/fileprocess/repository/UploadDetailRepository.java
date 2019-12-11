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

    public int save(FileUploadDetail fileUploadDetail) {
        String sql = " INSERT INTO vms_return_fileupload_dtls ( "
                + " VRF_INST_CODE, VRF_FILE_NAME, VRF_TOTAL_RECCOUNT, "
                + " VRF_SUCCESS_RECCOUNT, VRF_FAILURE_RECCOUNT, "
                + " VRF_FAILURE_DESC , VRF_FILE_STATUS, "
                + " VRF_INS_USER , VRF_INS_DATE, VRF_LUPD_USER, VRF_LUPD_DATE) "
                + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, SYSDATE, ? , SYSDATE) ";
        LOGGER.debug("Save sql being executed \n {}", sql);
        LOGGER.debug("Save sql Values:\n {}", fileUploadDetail.toString());
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
