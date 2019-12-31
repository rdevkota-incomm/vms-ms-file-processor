package com.incomm.vms.postback.config;

public class Constants {
    private Constants() {}

    public final static String SERIAL_NUMBER_NOT_FOUND = "Serial Number not found/Closed";

    public static final String FILE_NAME = "FILE_NAME";
    public static final String RECORD_NUMBER = "RECORD_NUMBER";
    public static final String CORRELATION_ID = "CORRELATION_ID";

    public final static String RETURN_REASON_CACHE_NAME = "RETURN_REASONS_CACHE";

    public final static String CONSUMER_CONTAINER_ID = "printer-awk-id";

    public final static String CONSUMER_CONTAINER_GROUP = "printer-awk-group";

    public final static String POST_BACK_TASK_EXECUTOR_POOL = "POST_BACK_TASK_EXECUTOR_POOL";

    public static final String ORDER_STATUS_API = "ORDERSTATUS";

    public static final String FSAPI_SUCCESS_RESPONSE_CODE = "00";
    public static final String FSAPI_REJECT_RESPONSE_CODE = "02";
    public static final String ORDER_REJECT_STATUS = "Rejected";
    public static final String PRE_AUTH_VERIFICATION_SUCCESS_FLAG ="S";
}
