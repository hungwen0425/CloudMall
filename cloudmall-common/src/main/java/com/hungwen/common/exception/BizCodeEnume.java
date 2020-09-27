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
public enum BizCodeEnume {

    UNKNOW_EXCEPTION(10000, "系统未知異常"),
    VAILD_EXCEPTION(10001, "参數格式校驗失敗"),
    VAILD_SMS_CODE_EXCEPTION(10002, "發送驗證碼频率過快，稍後再試！"),
    TO_MANY_REQUEST(10003, "請求流量過大"),
    PRODUCT_UP_TO_ES_EXCETION(11000, "商品上架給es索引資料時異常"),
    USER_EXIST_EXCEPTION(15001, "使用者已存在"),
    PHONE_EXIST_EXCEPTION(15002, "手機號已存在"),
    LOGINACCT_PASSSWORD_INVAILD_EXCEPTION(15003, "帳號或密碼錯誤"),
    NO_STOCK_EXCEPTION(21000, "商品庫存不足");

    private int code;
    private String msg;
    BizCodeEnume(int code,String msg){
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
