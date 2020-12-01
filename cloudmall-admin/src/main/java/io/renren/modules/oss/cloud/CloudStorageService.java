/**
 * Copyright (c) 2016-2019 人人開源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版權所有，侵權必究！
 */

package io.renren.modules.oss.cloud;

import io.renren.common.utils.DateUtils;
import org.apache.commons.lang.StringUtils;

import java.io.InputStream;
import java.util.Date;
import java.util.UUID;

/**
 * 雲端存儲(支持七牛、阿里雲、腾訊雲、又拍雲)
 *
 * @author hungwen.tseng@gmail.com
 */
public abstract class CloudStorageService {
    /** 雲端存儲設定備註 */
    CloudStorageConfig config;

    /**
     * 檔案路径
     * @param prefix 前缀
     * @param suffix 後缀
     * @return 返回上傳路径
     */
    public String getPath(String prefix, String suffix) {
        //生成uuid
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        //檔案路径
        String path = DateUtils.format(new Date(), "yyyyMMdd") + "/" + uuid;

        if(StringUtils.isNotBlank(prefix)){
            path = prefix + "/" + path;
        }

        return path + suffix;
    }

    /**
     * 檔案上傳
     * @param data    檔案字節陣列
     * @param path    檔案路径，包含檔案名
     * @return        返回http地址
     */
    public abstract String upload(byte[] data, String path);

    /**
     * 檔案上傳
     * @param data     檔案字節陣列
     * @param suffix   後缀
     * @return         返回http地址
     */
    public abstract String uploadSuffix(byte[] data, String suffix);

    /**
     * 檔案上傳
     * @param inputStream   字節流
     * @param path          檔案路径，包含檔案名
     * @return              返回http地址
     */
    public abstract String upload(InputStream inputStream, String path);

    /**
     * 檔案上傳
     * @param inputStream  字節流
     * @param suffix       後缀
     * @return             返回http地址
     */
    public abstract String uploadSuffix(InputStream inputStream, String suffix);

}
