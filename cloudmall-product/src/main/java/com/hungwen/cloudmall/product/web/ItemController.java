package com.hungwen.cloudmall.product.web;

import com.hungwen.cloudmall.product.service.SkuInfoService;
import com.hungwen.cloudmall.product.vo.SkuItemVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.concurrent.ExecutionException;

/**
 * @Description:
 * @Created: with IntelliJ IDEA.
 * @author: Hungwen Tseng
 * @createTime: 2020-10-19 11:21
 **/

@Controller
public class ItemController {

    @Autowired
    private SkuInfoService skuInfoService;

    /**
     * 展示當前 sku 的詳情
     * @param skuId
     * @return
     */
    @GetMapping("/{skuId}.html")
    public String skuItem(@PathVariable("skuId") Long skuId, Model model) throws ExecutionException, InterruptedException {
        SkuItemVo vos = skuInfoService.item(skuId);
        model.addAttribute("item", vos);
        return "item";
    }
}