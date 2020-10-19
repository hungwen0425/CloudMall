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
     * @param param 检索的所有参数
     * @return  返回检索的结果，里面包含页面需要的所有資料
     */
    SearchResult search(SearchParam param);
}
