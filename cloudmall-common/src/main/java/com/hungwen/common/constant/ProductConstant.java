package com.hungwen.common.constant;

public class ProductConstant {

    public enum  AttrEnum{
        ATTR_TYPE_BASE(1,"基本屬性"), ATTR_TYPE_SALE(0,"銷售屬性");
        private int code;
        private String msg;

        AttrEnum(int code, String msg){
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

    public enum  StatusEnum{
        NEW_SPU(0,"新建"), SPU_UP(1," 商品上架"), SPU_DOWN(2,"商品下架");
        private int code;
        private String msg;

        StatusEnum(int code, String msg){
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

    public enum ProductStatusEnum {
        NEW_SPU(0,"新建"), SPU_UP(1,"商品上架"), SPU_DOWN(2,"商品下架");
        private int code;
        private String message;

        ProductStatusEnum(int code, String message) {
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
}
