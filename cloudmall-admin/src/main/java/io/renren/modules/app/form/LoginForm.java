/**
 * Copyright (c) 2016-2019 人人開源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版權所有，侵權必究！
 */

package io.renren.modules.app.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 登入表單
 *
 * @author hungwen.tseng@gmail.com
 */
@Data
@ApiModel(value = "登入表單")
public class LoginForm {
    @ApiModelProperty(value = "手機號")
    @NotBlank(message="手機號不能為空")
    private String mobile;

    @ApiModelProperty(value = "密碼")
    @NotBlank(message="密碼不能為空")
    private String password;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
