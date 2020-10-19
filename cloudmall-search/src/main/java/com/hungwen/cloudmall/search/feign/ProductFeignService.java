package com.hungwen.cloudmall.search.feign;

import com.hungwen.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @Description:
 * @Created: with IntelliJ IDEA.
 * @author: Hungwen  Tseng
 * @createTime: 2020-06-18 09:15
 **/

@FeignClient("cloudmall-product")
public interface ProductFeignService {

    @GetMapping("/product/attr/info/{attrId}")
    public R attrInfo(@PathVariable("attrId") Long attrId);

}
