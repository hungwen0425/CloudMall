package com.hungwen.cloudmall.product.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Description:
 * @Created: with IntelliJ IDEA.
 * @author: Hungwen TSeng
 * @createTime: 2020-06-08 14:50
 *
 * 二級分類id
 **/

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Catelog2Vo {
    /**
     * 一級父分類 id
     */
    private String catalog1Id;

    /**
     * 三級子分類
     */
    private List<Catelog3Vo> catalog3List;
    private String id;
    private String name;

    /**
     * 三級分類 vo
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Catelog3Vo {
        /**
         * 父分類、二級分類 id
         */
        private String catalog2Id;
        private String id;
        private String name;
    }
}
