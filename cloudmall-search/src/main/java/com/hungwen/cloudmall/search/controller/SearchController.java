package com.hungwen.cloudmall.search.controller;

import com.hungwen.cloudmall.search.service.MallSearchService;
import com.hungwen.cloudmall.search.vo.SearchParam;
import com.hungwen.cloudmall.search.vo.SearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * @Description:
 * @Created: with IntelliJ IDEA.
 * @author: Hungwen Tseng
 * @createTime: 2020-10-12 18:07
 **/

@Controller
public class SearchController {

    @Autowired
    private MallSearchService mallSearchService;
    /**
     * 自動將頁面提交過來的所有請求參數封裝成我們指定的物件
     * @param param
     * @returna
     */
    @GetMapping(value="/list.html")
    public String listPage(SearchParam param, Model model, HttpServletRequest request) {
        param.set_queryString(request.getQueryString());
        // 1.根據傳遞來的頁面的查詢參數，去 es 中檢索商品
        SearchResult result = mallSearchService.search(param);
        model.addAttribute("result", result);
        return "list";
    }
}
