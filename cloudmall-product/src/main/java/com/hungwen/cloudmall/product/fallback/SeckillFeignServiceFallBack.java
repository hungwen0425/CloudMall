package com.hungwen.cloudmall.product.fallback;

import com.hungwen.cloudmall.product.feign.SecKillFeignService;
import com.hungwen.common.exception.BizCodeEnum;
import com.hungwen.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @Description:
 * @Created: with IntelliJ IDEA.
 * @author: Hungwen Tsaeng
 * @createTime: 2020-12-02 14:45
 **/
@Slf4j
@Component
public class SeckillFeignServiceFallBack implements SecKillFeignService {

    @Override
    public R getSkuSeckilInfo(Long skuId) {
        log.info("熔斷方法調用....getSkuSeckilInfo");
        return R.error(BizCodeEnum.TO_MANY_REQUEST.getCode(), BizCodeEnum.TO_MANY_REQUEST.getMessage());
    }
}
