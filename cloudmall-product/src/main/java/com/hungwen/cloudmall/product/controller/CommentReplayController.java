package com.hungwen.cloudmall.product.controller;

import java.util.Arrays;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hungwen.cloudmall.product.entity.CommentReplayEntity;
import com.hungwen.cloudmall.product.service.CommentReplayService;
import com.hungwen.common.utils.PageUtils;
import com.hungwen.common.utils.R;



/**
 * 商品評價回覆關系
 *
 * @author Hungwen Tseng
 * @email hungwen.tseng@gmail.com
 * @date 2020-08-25 10:21:04
 */
@RestController
@RequestMapping("product/commentreplay")
public class CommentReplayController {
    @Autowired
    private CommentReplayService commentReplayService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("product:commentreplay:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = commentReplayService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 資料
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("product:commentreplay:info")
    public R info(@PathVariable("id") Long id){
		CommentReplayEntity commentReplay = commentReplayService.getById(id);

        return R.ok().put("commentReplay", commentReplay);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("product:commentreplay:save")
    public R save(@RequestBody CommentReplayEntity commentReplay){
		commentReplayService.save(commentReplay);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:commentreplay:update")
    public R update(@RequestBody CommentReplayEntity commentReplay){
		commentReplayService.updateById(commentReplay);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:commentreplay:delete")
    public R delete(@RequestBody Long[] ids){
		commentReplayService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
