/**
 * Copyright (c) 2016-2019 人人開源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版權所有，侵權必究！
 */

package io.renren.modules.sys.form;

import lombok.Data;

/**
 * 密碼表單
 *
 * @author hungwen.tseng@gmail.com
 */
@Data
public class PasswordForm {
    /**
     * 原密碼
     */
    private String password;
    /**
     * 新密碼
     */
    private String newPassword;

}
