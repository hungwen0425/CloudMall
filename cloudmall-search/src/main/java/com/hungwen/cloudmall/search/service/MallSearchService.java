package com.hungwen.cloudmall.search.service;


import com.hungwen.cloudmall.search.vo.SearchParam;
import com.hungwen.cloudmall.search.vo.SearchResult;

/**
 * @Description:
 * @Created: with IntelliJ IDEA.
 * @author: Hungwen Tseng
 * @createTime: 2020-10-13 14:17
 **/
public interface MallSearchService {

    /**
     * @param param 檢索的所有参數
     * @return  返回檢索的结果，裡面包含頁面需要的所有資料
     */
    SearchResult search(SearchParam param);
}
