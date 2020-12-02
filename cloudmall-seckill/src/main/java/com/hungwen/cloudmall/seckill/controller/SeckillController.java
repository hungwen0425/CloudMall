package com.hungwen.cloudmall.seckill.controller;

import com.hungwen.cloudmall.seckill.service.SeckillService;
import com.hungwen.cloudmall.seckill.to.SeckillSkuRedisTo;
import com.hungwen.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @Description:
 * @Created: with IntelliJ IDEA.
 * @author: Hungwen Tseng
 * @createTime: 2020-11-29 11:01
 **/
@Slf4j
@Controller
public class SeckillController {

    @Autowired
    private SeckillService seckillService;

    /**
     * 當前時間可以參與限時搶購的商品資料
     * @return
     */
    @GetMapping(value = "/getCurrentSeckillSkus")
    @ResponseBody
    public R getCurrentSeckillSkus() {
        log.info("getCurrentSeckillSkus 正在運行中");
        // 查詢當前可以參加限時搶購商品的資料
        List<SeckillSkuRedisTo> vos = seckillService.getCurrentSeckillSkus();
        return R.ok().setData(vos);
    }

    /**
     * 根據 skuId 查詢商品是否參加限時搶購活動
     * @param skuId
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/sku/seckill/{skuId}")
    public R getSkuSeckilInfo(@PathVariable("skuId") Long skuId) {
        SeckillSkuRedisTo to = seckillService.getSkuSeckilInfo(skuId);
        return R.ok().setData(to);
    }

    /**
     * 商品進行限時搶購 (限時搶購開始) - 限時搶購第一種流程
     * @param killId
     * @param key
     * @param num
     * @return
     */
//    @GetMapping(value = "/kill")
//    public String seckill(@RequestParam("killId") String killId, @RequestParam("key") String key,
//                          @RequestParam("num") Integer num, Model model) {
//
//        return null;
//    }

    /**
     * 商品進行限時搶購 (限時搶購開始) - 限時搶購第二種流程
     * @param killId
     * @param key
     * @param num
     * @return
     */
    @GetMapping(value = "/kill")
    public String seckill(@RequestParam("killId") String killId, @RequestParam("key") String key,
                          @RequestParam("num") Integer num, Model model) {
        String orderSn = null;
        try {
            // 1. 判斷是否登入
            orderSn = seckillService.kill(killId, key, num);
            model.addAttribute("orderSn", orderSn);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "success";
    }
}
