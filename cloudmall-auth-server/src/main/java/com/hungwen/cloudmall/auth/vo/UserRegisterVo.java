package com.hungwen.cloudmall.auth.vo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

/**
 * @Description:
 * @Created: with IntelliJ IDEA.
 * @author: Hungwen TSEng
 * @createTime: 2020-10-27 11:41
 **/

@Data
public class UserRegisterVo {

    @NotEmpty(message = "使用者名不能為空")
    @Length(min = 6, max = 19, message="使用者名長度在6-18字元")
    private String userName;

    @NotEmpty(message = "密碼必須填寫")
    @Length(min = 6,max = 18,message = "密碼必須是6—18位字元")
    private String password;

    @NotEmpty(message = "手機號碼不能為空")
    @Pattern(regexp = "^09[0-9]{8}$", message = "手機號碼格式不正確")
    private String phone;

    @NotEmpty(message = "驗證碼不能為空")
    private String code;

}
