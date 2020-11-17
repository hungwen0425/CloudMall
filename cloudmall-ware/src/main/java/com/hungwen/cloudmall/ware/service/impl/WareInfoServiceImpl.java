package com.hungwen.cloudmall.ware.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.alibaba.nacos.client.utils.StringUtils;
import com.hungwen.cloudmall.ware.feign.MemberFeignService;
import com.hungwen.cloudmall.ware.vo.FareVo;
import com.hungwen.cloudmall.ware.vo.MemberAddressVo;
import com.hungwen.common.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hungwen.common.utils.PageUtils;
import com.hungwen.common.utils.Query;

import com.hungwen.cloudmall.ware.dao.WareInfoDao;
import com.hungwen.cloudmall.ware.entity.WareInfoEntity;
import com.hungwen.cloudmall.ware.service.WareInfoService;


@Service("wareInfoService")
public class WareInfoServiceImpl extends ServiceImpl<WareInfoDao, WareInfoEntity> implements WareInfoService {

    @Autowired
    private MemberFeignService memberFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<WareInfoEntity> queryWrapper = new QueryWrapper<>();
        String key=(String)params.get("key");
        if(StringUtils.isNotEmpty(key)){
            queryWrapper.eq("id",key)
                    .or().like("name",key)
                    .or().like("address",key)
                    .or().like("areacode",key);
        }

        IPage<WareInfoEntity> page = this.page(
                new Query<WareInfoEntity>().getPage(params),queryWrapper
        );

        return new PageUtils(page);
    }

    /**
     * 計算運費
     * @param addrId
     * @return
     */
    @Override
    public FareVo getFare(Long addrId) {
        FareVo fareVo = new FareVo();
        // 收獲地址的詳細 資料
        R addrInfo = memberFeignService.info(addrId);
        MemberAddressVo memberAddressVo = addrInfo.getData("memberReceiveAddress", new TypeReference<MemberAddressVo>() {});
        if (memberAddressVo != null) {
            String phone = memberAddressVo.getPhone();
            // 截取用戶手機號碼最後一位作為我們的運費計算
            // 1558022051
            String fare = phone.substring(phone.length() - 10, phone.length() - 8);
            BigDecimal bigDecimal = new BigDecimal(fare);
            fareVo.setFare(bigDecimal);
            fareVo.setAddress(memberAddressVo);
            return fareVo;
        }
        return null;
    }

}