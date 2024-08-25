package com.tencentcs.iotvideo.messagemgr

enum class SubscribeError(val errorCode: Int) {
    AV_ER_DTLS_AUTH_FAIL(-20041),
    AV_ER_DTLS_WRONG_PWD(-20040),
    AV_ER_REMOTE_NOT_SUPPORT_DTLS(-20039),
    AV_ER_TOKEN_EXCEED_MAX_SIZE(-20038),
    AV_ER_REMOTE_NOT_SUPPORT(-20037),
    AV_ER_REQUEST_ALREADY_CALLED(-20036),
    AV_ER_FAIL_CREATE_DTLS(-20035),
    AV_ER_FAIL_INITIALIZE_DTLS(-20034),
    AV_ER_NOT_SUPPORT(-20033),
    AV_ER_DASA_CLEAN_BUFFER(-20032),
    AV_ER_ALREADY_INITIALIZED(-20031),
    AV_ER_SOCKET_QUEUE_FULL(-20030),
    AV_ER_CLEANBUF_ALREADY_CALLED(-20029),
    AV_ER_WAIT_KEY_FRAME(-20028),
    AV_ER_IOTC_CHANNEL_IN_USED(-20027),
    AV_ER_IOTC_DEINITIALIZED(-20026),
    AV_ER_IOTC_SESSION_CLOSED(-20025),
    AV_ER_WRONG_ACCPWD_LENGTH(-20024),
    AV_ER_NO_PERMISSION(-20023),
    AV_ER_SENDIOCTRL_EXIT(-20022),
    AV_ER_SENDIOCTRL_ALREADY_CALLED(-20021),
    AV_ER_CLIENT_NOT_SUPPORT(-20020),
    AV_ER_NOT_INITIALIZED(-20019),
    AV_ER_CLIENT_EXIT(-20018),
    AV_ER_SERVER_EXIT(-20017),
    AV_ER_REMOTE_TIMEOUT_DISCONNECT(-20016),
    AV_ER_SESSION_CLOSE_BY_REMOTE(-20015),
    AV_ER_LOSED_THIS_FRAME(-20014),
    AV_ER_INCOMPLETE_FRAME(-20013),
    AV_ER_DATA_NOREADY(-20012),
    AV_ER_TIMEOUT(-20011),
    AV_ER_INVALID_SID(-20010),
    AV_ER_WRONG_VIEWACCorPWD(-20009),
    AV_ER_CLIENT_NO_AVLOGIN(-20008),
    AV_ER_SERV_NO_RESPONSE(-20007),
    AV_ER_EXCEED_MAX_SIZE(-20006),
    AV_ER_EXCEED_MAX_ALARM(-20005),
    AV_ER_FAIL_CREATE_THREAD(-20004),
    AV_ER_MEM_INSUFF(-20003),
    AV_ER_EXCEED_MAX_CHANNEL(-20002),
    AV_ER_BUFPARA_MAXSIZE_INSUFF(-20001),
    AV_ER_INVALID_ARG(-20000),
    RDT_ER_REMOTE_EXIT(-10016),
    RDT_ER_LOCAL_EXIT(-10015),
    RDT_ER_INVALID_ARG(-10014),
    RDT_ER_NO_PERMISSION(-10013),
    RDT_ER_CHANNEL_OCCUPIED(-10012),
    RDT_ER_LOCAL_ABORT(-10011),
    RDT_ER_REMOTE_ABORT(-10010),
    RDT_ER_RCV_DATA_END(-10009),
    RDT_ER_INVALID_RDT_ID(-10008),
    RDT_ER_TIMEOUT(-10007),
    RDT_ER_RDT_DESTROYED(-10006),
    RDT_ER_FAIL_CREATE_MUTEX(-10005),
    RDT_ER_FAIL_CREATE_THREAD(-10004),
    RDT_ER_MEM_INSUFF(-10003),
    RDT_ER_EXCEED_MAX_CHANNEL(-10002),
    RDT_ER_ALREADY_INITIALIZED(-10001),
    RDT_ER_NOT_INITIALIZED(-10000),
    TUTK_ER_NOT_SUPPORT(-1006),
    TUTK_ER_NO_LICENSE_KEY(-1005),
    TUTK_ER_INVALID_LICENSE_KEY(-1004),
    TUTK_ER_MEM_INSUFFICIENT(-1003),
    TUTK_ER_INVALID_ARG(-1002),
    TUTK_ER_ALREADY_INITIALIZED(-1001),
    IOTC_ER_SESSION_IN_USE(-72),
    IOTC_ER_DID_NOT_LOGIN_WITH_AUTHKEY(-71),
    IOTC_ER_DID_NOT_LOGIN(-70),
    IOTC_ER_DEVICE_NOT_USE_KEY_AUTHENTICATION(-69),
    IOTC_ER_DEVICE_REJECT_BY_WRONG_AUTH_KEY(-68),
    IOTC_ER_DEVICE_REJECT_BYPORT(-67),
    IOTC_ER_WAKEUP_NOT_INITIALIZED(-66),
    IOTC_ER_TCP_NOT_SUPPORT(-65),
    IOTC_ER_DEVICE_IS_SLEEP(-64),
    IOTC_ER_NOT_SUPPORT(-63),
    IOTC_ER_QUEUE_FULL(-62),
    IOTC_ER_RESOURCE_ERROR(-61),
    IOTC_ER_MASTER_NOT_RESPONSE(-60),
    IOTC_ER_DEVICE_IS_BANNED(-59),
    IOTC_ER_NOT_ENOUGH_MEMORY(-58),
    IOTC_ER_STILL_IN_PROCESSING(-57),
    IOTC_ER_SERVICE_IS_NOT_STARTED(-56),
    IOTC_ER_NO_PATH_TO_WRITE_DATA(-55),
    IOTC_ER_SERVER_NOT_SUPPORT(-54),
    IOTC_ER_EXCEED_MAX_PACKET_SIZE(-53),
    IOTC_ER_ABORTED(-52),
    IOTC_ER_REMOTE_NOT_SUPPORTED(-51),
    IOTC_ER_SESSION_CLOSED(-50),
    IOTC_ER_BLOCKED_CALL(-49),
    IOTC_ER_DEVICE_EXCEED_MAX_SESSION(-48),
    IOTC_ER_NOT_SUPPORT_PE(-47),
    IOTC_ER_INVALID_ARG(-46),
    IOTC_ER_DEVICE_MULTI_LOGIN(-45),
    IOTC_ER_NO_SERVER_LIST(-44),
    IOTC_ER_NOT_SUPPORT_RELAY(-43),
    IOTC_ER_FAIL_SETUP_RELAY(-42),
    IOTC_ER_NETWORK_UNREACHABLE(-41),
    IOTC_ER_NO_PERMISSION(-40),
    IOTC_ER_EXIT_LISTEN(-39),
    IOTC_ER_INVALID_MODE(-38),
    IOTC_ER_DEVICE_SECURE_MODE(-37),
    IOTC_ER_DEVICE_NOT_SECURE_MODE(-36),
    IOTC_ER_CLIENT_SECURE_MODE(-35),
    IOTC_ER_CLIENT_NOT_SECURE_MODE(-34),
    IOTC_ER_TCP_CONNECT_TO_SERVER_FAILED(-33),
    IOTC_ER_TCP_TRAVEL_FAILED(-32),
    IOTC_ER_SESSION_NO_FREE_CHANNEL(-31),
    IOTC_ER_AES_CERTIFY_FAIL(-29),
    IOTC_ER_MASTER_TOO_FEW(-28),
    IOTC_ER_FAIL_CONNECT_SEARCH(-27),
    IOTC_ER_CH_NOT_ON(-26),
    IOTC_ER_DEVICE_NOT_LISTENING(-24),
    IOTC_ER_REMOTE_TIMEOUT_DISCONNECT(-23),
    IOTC_ER_SESSION_CLOSE_BY_REMOTE(-22),
    IOTC_ER_CONNECT_IS_CALLING(-20),
    IOTC_ER_CAN_NOT_FIND_DEVICE(-19),
    IOTC_ER_EXCEED_MAX_SESSION(-18),
    IOTC_ER_LISTEN_ALREADY_CALLED(-17),
    IOTC_ER_FAIL_GET_LOCAL_IP(-16),
    IOTC_ER_UNKNOWN_DEVICE(-15),
    IOTC_ER_INVALID_SID(-14),
    IOTC_ER_TIMEOUT(-13),
    IOTC_ER_NOT_INITIALIZED(-12),
    IOTC_ER_LOGIN_ALREADY_CALLED(-11),
    IOTC_ER_UNLICENSE(-10),
    IOTC_ER_FAIL_SOCKET_BIND(-8),
    IOTC_ER_FAIL_SOCKET_OPT(-7),
    IOTC_ER_FAIL_CREATE_SOCKET(-6),
    IOTC_ER_FAIL_CREATE_THREAD(-5),
    IOTC_ER_FAIL_CREATE_MUTEX(-4),
    IOTC_ER_ALREADY_INITIALIZED(-3),
    IOTC_ER_FAIL_RESOLVE_HOSTNAME(-2),
    IOTC_ER_SERVER_NOT_RESPONSE(-1),
    UNKNOWN_ERR(Int.MAX_VALUE);

    companion object {
        fun fromErrorCode(errorCode: Int): SubscribeError {
            val negativeErrorCode = if (errorCode < 0) errorCode else -errorCode
            for (error in entries) {
                if (error.errorCode == negativeErrorCode) {
                    return error
                }
            }
            return UNKNOWN_ERR
        }
    }
}
