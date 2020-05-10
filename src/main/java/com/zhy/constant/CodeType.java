package com.zhy.constant;

/**
 * @author: zhangocean
 * @Date: 2019/11/18 15:51
 * Describe: 状态返回码
 */
public enum CodeType {

    /**
     * 状态码
     */
    SUCCESS_STATUS(0, "成功"),
    SERVER_EXCEPTION(1, "服务器异常"),

    FORBIDDEN_OPERATION(100, "权限不足"),
    LOGIN_SUCCESS(101, "登录成功"),
    LOGIN_FAIL(102, "登录失败！请检查手机号、密码或验证码是否正确"),
    LOGIN_OUT_SUCCESS(103, "登出成功"),
    REGISTER_SUCCESS(104, "注册成功"),
    PHONE_EXIST(105, "手机号存在"),
    PHONE_NOT_EXIST(106, "手机号不存在"),
    AUTH_CODE_ERROR(107, "验证码错误"),
    PASSWORD_ERROR(108, "密码错误"),
    USER_NOT_LOGIN(109, "用户未登录"),
    UPDATE_CERT_SUCCESS(110, "修改证件信息成功"),
    CHANGE_PASSWORD_SUCCESS(111, "修改密码成功"),
    CHANGE_USER_INFO_SUCCESS(112, "更新用户信息成功"),
    ORDER_ROOM_EXIST(113, "已处于约看中，请不要重复约看"),
    COLLECT_ROOM_EXIST(114, "已收藏该房间"),
    FACILITY_HAS_REPAIRED(115, "该设备已处于报修中"),
    ;

    private int code;

    private String message;

    CodeType(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }


}
