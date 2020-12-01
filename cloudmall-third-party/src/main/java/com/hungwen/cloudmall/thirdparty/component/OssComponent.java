package com.hungwen.cloudmall.thirdparty.component;

import com.aliyun.oss.OSS;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.MatchMode;
import com.aliyun.oss.model.PolicyConditions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @Description TODO
 * @Author Hungwen Tseng
 * @Date 2020/6/28 09:43
 * @Version 1.0
 **/
@Slf4j
@Component
public class OssComponent {

    @Autowired
    OSS ossClient;

    @Value("${spring.cloud.alicloud.oss.bucket}")
    private String ALIYUN_OSS_BUCKET_NAME;
    @Value("${spring.cloud.alicloud.oss.endpoint}")
    private String ALIYUN_OSS_ENDPOINT;
    @Value("${spring.cloud.alicloud.access-key}")
    private String ALIYUN_OSS_ACCESS_KEY;

    /**
     * 簽名生成
     */
    public Map<String,String> policy() {

        String bucket = ALIYUN_OSS_BUCKET_NAME; // 請填寫您的 bucketname 。
        String host = "https://" + bucket + "." + ALIYUN_OSS_ENDPOINT; // host的格式為 bucketname.endpoint
        // callbackUrl 為上傳回調服務器的URL，請將下面的 IP 和 Port 設定為您自己的真實資料。
//        String callbackUrl = "http://88.88.88.88:8888";

        // 存儲目錄
        String format = new SimpleDateFormat("yyyy/MM/dd").format(new Date());
        String dir = "cloudmall/image/" + format + "/"; // 使用者上傳文件時指定的前缀。
        log.debug("上傳的路径為：{}",dir);
        Map<String, String> respMap = null;
        try {
            long expireTime = 30;
            long expireEndTime = System.currentTimeMillis() + expireTime * 1000;
            Date expiration = new Date(expireEndTime);
            PolicyConditions policyConds = new PolicyConditions();
            policyConds.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, 1048576000);
            policyConds.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, dir);

            String postPolicy = ossClient.generatePostPolicy(expiration, policyConds);
            byte[] binaryData = postPolicy.getBytes("utf-8");
            String encodedPolicy = BinaryUtil.toBase64String(binaryData);
            String postSignature = ossClient.calculatePostSignature(postPolicy);

            respMap = new LinkedHashMap<String, String>();
            respMap.put("accessid", ALIYUN_OSS_ACCESS_KEY);
            respMap.put("policy", encodedPolicy);
            respMap.put("signature", postSignature);
            respMap.put("dir", dir);
            respMap.put("host", host);
            respMap.put("expire", String.valueOf(expireEndTime / 1000));
            // respMap.put("expire", formatISO8601Date(expiration));


        } catch (Exception e) {
            // Assert.fail(e.getMessage());
            System.out.println(e.getMessage());
        }
        return respMap;
    }
}
