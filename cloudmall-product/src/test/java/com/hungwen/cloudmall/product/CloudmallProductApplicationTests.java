package com.hungwen.cloudmall.product;

import com.hungwen.cloudmall.product.entity.BrandEntity;
import com.hungwen.cloudmall.product.service.BrandService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CloudmallProductApplicationTests {

    @Autowired
    BrandService brandService;

    @Test
    public void contextLoads() {

        BrandEntity brandEntity = new BrandEntity();
        brandEntity.setName("三星");
        brandService.save(brandEntity);
        System.out.println("新增成功");
    }

}
