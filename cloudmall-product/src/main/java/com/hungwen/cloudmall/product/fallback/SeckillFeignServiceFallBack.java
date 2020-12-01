package com.hungwen.cloudmall.product.fallback;

import com.hungwen.cloudmall.product.feign.SecKillFeignService;
import com.hungwen.common.exception.BizCodeEnum;
import com.hungwen.common.utils.R;
import org.springframework.stereotype.Component;

/**
 * @Description:
 * @Created: with IntelliJ IDEA.
 * @author: Hungwen Tsaeng
 * @createTime: 2020-11-29 14:45
 **/

@Component
public class SeckillFeignServiceFallBack implements SecKillFeignService {

    @Override
    public R getSkuSeckilInfo(Long skuId) {
        return R.error(BizCodeEnum.TO_MANY_REQUEST.getCode(),BizCodeEnum.TO_MANY_REQUEST.getMessage());
    }
}
