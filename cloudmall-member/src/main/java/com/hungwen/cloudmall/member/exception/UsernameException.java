package com.hungwen.cloudmall.member.exception;

/**
 * @Description:
 * @Created: with IntelliJ IDEA.
 * @author: Hungwen TSeng
 * @createTime: 2020-10-27 16:04
 **/
public class UsernameException extends RuntimeException {
    public UsernameException() {
        super("存在相同使用者名稱");
    }
}
