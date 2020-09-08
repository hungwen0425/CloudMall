package com.hungwen.cloudmall.product;

import com.hungwen.cloudmall.product.dao.AttrGroupDao;
import com.hungwen.cloudmall.product.entity.BrandEntity;
import com.hungwen.cloudmall.product.service.BrandService;
import com.hungwen.cloudmall.product.service.CategoryService;
import com.hungwen.cloudmall.product.vo.SpuItemAttrGroupVo;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.data.redis.core.StringRedisTemplate;
//import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class CloudmallProductApplicationTests {

    @Autowired
    BrandService brandService;
    @Autowired
    CategoryService categoryService;
//    @Autowired
//    StringRedisTemplate springRedisTemplate;
//    @Autowired
//    AttrGroupDao attrGroupDao;

    @Test
    public void contextLoads() {

        BrandEntity brandEntity = new BrandEntity();
        brandEntity.setName("三星");
        brandService.save(brandEntity);
        System.out.println("新增成功");
    }

//    @Test
//    public void test() {
//        List<SpuItemAttrGroupVo> spuItemAttrGroupVo = attrGroupDao.getAttrGroupWithAttrsBySpuId(225L, 7L);
//        System.out.println(spuItemAttrGroupVo.toString());
//    }

//    @Test
//    public void testspringRedisTemplate() {
//        ValueOperations<String, String> ops = springRedisTemplate.opsForValue();
//        // save
//        ops.set("hello", "word"+ UUID.randomUUID().toString());
//        // query
//        System.out.println(ops.get("hello"));
//    }

    @Test
    public void testUpload() throws FileNotFoundException {

        // Endpoint 以香港為例，其它 Region 請按實際情況填寫。
        // String endpoint = "http://oss-cn-hongkong.aliyuncs.com";
        // 雲帳號AccessKey有所有API存取權限，建議遵循阿里雲安全最佳實務，建立並使用RAM子帳號進行API訪問或日常運維，請登入 https://ram.console.aliyun.com 建立。
        // String accessKeyId = "LTAI4GJQqri8yHji2v8BDLPr";
        // String accessKeySecret = "4rZhqebX8yqAx35QuHp1irMfC7T0Hs";
        // 建立OSSClient執行個體。
        //OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
        // 上傳檔案流。
        InputStream inputStream = new FileInputStream("C:\\Users\\hungw\\OneDrive\\桌面\\44.jpg");
        //ossClient.putObject("cloudmall", "aa.jpg", inputStream);
        // 關閉OSSClient。
        //ossClient.shutdown();
        System.out.println("上傳成功....");
    }

    @Test
    public void testFindPath() throws FileNotFoundException {
        Long[] catelogPath = categoryService.findCatelogPath(225L);
        System.out.println(Arrays.asList(catelogPath));
    }

}
