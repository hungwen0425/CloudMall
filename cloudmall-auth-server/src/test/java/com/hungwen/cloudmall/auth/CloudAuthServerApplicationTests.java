package com.hungwen.cloudmall.auth;

import com.hungwen.common.utils.HttpUtils;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CloudAuthServerApplicationTests {

	@Test
	public void contextLoads() {
	}

//	@Test
//	public void sendCode() {
//		String host = "https://smsmsgs.market.alicloudapi.com";
//		String path = "/sms/";
//		String method = "GET";
//		String appcode = "980eb5fac51d4fb58dbc4b1b58dc379e";
//		Map<String, String> headers = new HashMap<String, String>();
//		//最後在 header 中的格式 (中間是英文空格) 為 Authorization:APPCODE 83359fd73fe94948385f570e3c139105
//		headers.put("Authorization", "APPCODE " + appcode);
//		Map<String, String> querys = new HashMap<String, String>();
//		querys.put("code", "66785");
//		querys.put("phone", "00886986052168");
//		querys.put("skin", "5");
//		querys.put("sign", "1");
//		//JDK 1.8示例代碼請在這裡下載：  http://code.fegine.com/Tools.zip
//		try {
//			/**
//			 * 重要提示如下:
//			 * HttpUtils 請從
//			 * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/src/main/java/com/aliyun/api/gateway/demo/util/HttpUtils.java
//			 * 或者直接下載：
//			 * http://code.fegine.com/HttpUtils.zip
//			 * 下載
//			 * 相應的依賴請參照
//			 * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/pom.xml
//			 * 相關 jar 包（非 pom）直接下載：
//			 * http://code.fegine.com/aliyun-jar.zip
//			 */
//			HttpResponse response = HttpUtils.doGet(host, path, method, headers, querys);
//			//System.out.println(response.toString());如不輸出 json, 請打開這行代碼，打印調試頭部狀態碼。
//			//狀態碼: 200 正常；400 URL 無效；401 appCode 錯誤； 403 次數用完； 500 API 網管錯誤
//			//查詢response的body
//			System.out.println(EntityUtils.toString(response.getEntity()));
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
}
