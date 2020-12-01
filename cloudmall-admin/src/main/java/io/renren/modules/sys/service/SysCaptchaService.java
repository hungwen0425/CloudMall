/**
 * Copyright (c) 2016-2019 人人開源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版權所有，侵權必究！
 */

package io.renren.modules.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.renren.modules.sys.entity.SysCaptchaEntity;

import java.awt.image.BufferedImage;

/**
 * 驗証碼
 *
 * @author hungwen.tseng@gmail.com
 */
public interface SysCaptchaService extends IService<SysCaptchaEntity> {

    /**
     * 取得圖片驗証碼
     */
    BufferedImage getCaptcha(String uuid);

    /**
     * 驗証碼效驗
     * @param uuid  uuid
     * @param code  驗証碼
     * @return  true：成功  false：失敗
     */
    boolean validate(String uuid, String code);
}
