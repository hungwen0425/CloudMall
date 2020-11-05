package com.hungwen.common.exception;

/***
 * 錯誤碼和錯誤資料定義類
 * 1. 錯誤碼定義規則為5為數字
 * 2. 前兩位表示業務場景，最後三位表示錯誤碼。例如：10001
 *    10:通用 001:系統未知異常
 * 3. 維護錯誤碼後需要維護錯誤描述，將他們定義為枚舉形式
 * 錯誤碼列表：
 *  10: 通用
 *      001：參數格式校驗
 *      002：簡訊驗證碼频率太高
 *  11: 商品
 *  12: 訂單
 *  13: 購物車
 *  14: 物流
 *  15: 使用者
 */
public enum BizCodeEnum {

    UNKNOW_EXCEPTION(10000,"系統未知異常"),
    VAILD_EXCEPTION(10001,"參數格式校驗失敗"),
    TO_MANY_REQUEST(10002,"請求流量過大，請稍後再試"),
    SMS_CODE_EXCEPTION(10002,"驗證碼查詢頻率太高，請稍後再試"),
    PRODUCT_UP_EXCEPTION(11000,"商品上架異常"),
    USER_EXIST_EXCEPTION(15001,"存在相同的使用者"),
    PHONE_EXIST_EXCEPTION(15002,"存在相同的手機號碼"),
    LOGINACCT_PASSWORD_EXCEPTION(15003,"賬號或密碼錯誤"),
    NO_STOCK_EXCEPTION(21000,"商品庫存不足");


    private int code;
    private String message;

    BizCodeEnum(int code, String message){
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
