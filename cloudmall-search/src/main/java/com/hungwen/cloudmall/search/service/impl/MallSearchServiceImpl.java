package com.hungwen.cloudmall.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.hungwen.cloudmall.search.config.CloudmallElasticSearchConfig;
import com.hungwen.cloudmall.search.constant.EsConstant;
import com.hungwen.cloudmall.search.feign.ProductFeignService;
import com.hungwen.cloudmall.search.service.MallSearchService;
import com.hungwen.cloudmall.search.vo.AttrResponseVo;
import com.hungwen.cloudmall.search.vo.SearchParam;
import com.hungwen.cloudmall.search.vo.SearchResult;
import com.hungwen.common.to.es.SkuEsModel;
import com.hungwen.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description:
 * @Created: with IntelliJ IDEA.
 * @author: Hungwen Tseng
 * @createTime: 2020-06-13 14:19
 **/

@Slf4j
@Service
public class MallSearchServiceImpl implements MallSearchService {

    @Autowired
    private RestHighLevelClient esRestClient;
    @Autowired
    private ProductFeignService productFeignService;

    @Override
    public SearchResult search(SearchParam param) {
        // 動態構建出查詢需要的 DSL 語句
        SearchResult result = null;
        // 1.準備檢索請求
        SearchRequest searchRequest = buildSearchRequest(param);
        try {
            // 2.執行檢索請求
            SearchResponse response = esRestClient.search(searchRequest, CloudmallElasticSearchConfig.COMMON_OPTIONS);
            // 3.分析響應資料，封裝成我們需要的格式
            result = buildSearchResult(response, param);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 構建結果資料
     * 模糊匹配，過濾（按照屬性、分類、品牌，價格區間，庫存），完成排序、分頁、高亮、聚合分析功能
     * @param response
     * @return
     */
    private SearchResult buildSearchResult(SearchResponse response, SearchParam param) {
        SearchResult result = new SearchResult();
        // 1.返回的所有查詢到的商品
        SearchHits hits = response.getHits();
        List<SkuEsModel> esModels = new ArrayList<>();
        // 遍歷所有商品資料
        if (hits.getHits() != null && hits.getHits().length > 0) {
            for (SearchHit hit : hits.getHits()) {
                String sourceAsString = hit.getSourceAsString();
                SkuEsModel esModel = JSON.parseObject(sourceAsString, SkuEsModel.class);
                // 判斷是否按關鍵字檢索，若是就顯示高亮，否則不顯示
                if (!StringUtils.isEmpty(param.getKeyword())) {
                    // 拿到高亮資料顯示標題
                    HighlightField skuTitle = hit.getHighlightFields().get("skuTitle");
                    String skuTitleValue = skuTitle.getFragments()[0].string();
                    esModel.setSkuTitle(skuTitleValue);
                }
                esModels.add(esModel);
            }
        }
        result.setProduct(esModels);

        // 2.當前商品涉及到的所有屬性資料
        List<SearchResult.AttrVo> attrVos = new ArrayList<>();
        // 查詢屬性資料的聚合
        ParsedNested attrsAgg = response.getAggregations().get("attr_agg");
        ParsedLongTerms attrIdAgg = attrsAgg.getAggregations().get("attr_id_agg");
        for (Terms.Bucket bucket : attrIdAgg.getBuckets()) {
            SearchResult.AttrVo attrVo = new SearchResult.AttrVo();
            // 2.1 得到屬性的id
            long attrId = bucket.getKeyAsNumber().longValue();
            attrVo.setAttrId(attrId);
            // 2.2 得到屬性的名字
            ParsedStringTerms attrNameAgg = bucket.getAggregations().get("attr_name_agg");
            String attrName = attrNameAgg.getBuckets().get(0).getKeyAsString();
            attrVo.setAttrName(attrName);
            // 2.3  得到屬性的所有值
            ParsedStringTerms attrValueAgg = bucket.getAggregations().get("attr_value_agg");
            List<String> attrValues = attrValueAgg.getBuckets().stream().map(item -> item.getKeyAsString()).collect(Collectors.toList());
            attrVo.setAttrValue(attrValues);

            attrVos.add(attrVo);
        }
        result.setAttrs(attrVos);

        // 3.當前商品涉及到的所有品牌資料
        List<SearchResult.BrandVo> brandVos = new ArrayList<>();
        // 查詢到品牌的聚合
        ParsedLongTerms brandAgg = response.getAggregations().get("brand_agg");
        for (Terms.Bucket bucket : brandAgg.getBuckets()) {
            SearchResult.BrandVo brandVo = new SearchResult.BrandVo();
            // 3.1  得到品牌的id
            long brandId = bucket.getKeyAsNumber().longValue();
            brandVo.setBrandId(brandId);
            // 3.2  得到品牌的名字
            ParsedStringTerms brandNameAgg = bucket.getAggregations().get("brand_name_agg");
            String brandName = brandNameAgg.getBuckets().get(0).getKeyAsString();
            brandVo.setBrandName(brandName);
            // 3.3 得到品牌的圖片
            ParsedStringTerms brandImgAgg = bucket.getAggregations().get("brand_img_agg");
            String brandImg = brandImgAgg.getBuckets().get(0).getKeyAsString();
            brandVo.setBrandImg(brandImg);

            brandVos.add(brandVo);
        }
        result.setBrands(brandVos);
        // 4.當前商品涉及到的所有分類資料
        // 4.1  查詢到分類的聚合
        List<SearchResult.CatalogVo> catalogVos = new ArrayList<>();
        ParsedLongTerms catalogAgg = response.getAggregations().get("catalog_agg");
        for (Terms.Bucket bucket : catalogAgg.getBuckets()) {
            SearchResult.CatalogVo catalogVo = new SearchResult.CatalogVo();
            // 4.2 得到分類 id
            String keyAsString = bucket.getKeyAsString();
            catalogVo.setCatalogId(Long.parseLong(keyAsString));
            // 4.3  得到分類名
            ParsedStringTerms catalogNameAgg = bucket.getAggregations().get("catalog_name_agg");
            String catalogName = catalogNameAgg.getBuckets().get(0).getKeyAsString();
            catalogVo.setCatalogName(catalogName);
            catalogVos.add(catalogVo);
        }
        result.setCatalogs(catalogVos);
        //===============以上可以從聚合資料中查詢====================//
        // 5.分頁資料-頁碼
        result.setPageNum(param.getPageNum());
        // 5、1 分頁資料、總記錄數
        long total = hits.getTotalHits().value;
        result.setTotal(total);
        // 5、2 分頁資料-總頁碼-計算
        int totalPages = (int)total % EsConstant.PRODUCT_PAGESIZE == 0 ?
                (int)total / EsConstant.PRODUCT_PAGESIZE : ((int)total / EsConstant.PRODUCT_PAGESIZE + 1);
        result.setTotalPages(totalPages);

        List<Integer> pageNavs = new ArrayList<>();
        for (int i = 1; i <= totalPages; i++) {
            pageNavs.add(i);
        }
        result.setPageNavs(pageNavs);
        // 6.構建麵包屑導航
        if (param.getAttrs() != null && param.getAttrs().size() > 0) {
            List<SearchResult.NavVo> collect = param.getAttrs().stream().map(attr -> {
                // 6.1  分析每一個 attrs 傳過來的參數值
                SearchResult.NavVo navVo = new SearchResult.NavVo();
                String[] s = attr.split("_");
                navVo.setNavValue(s[1]);
                R r = productFeignService.attrInfo(Long.parseLong(s[0]));
                if (r.getCode() == 0) {
                    AttrResponseVo data = r.getData("attr", new TypeReference<AttrResponseVo>() {
                    });
                    navVo.setNavName(data.getAttrName());
                } else {
                    navVo.setNavName(s[0]);
                }
                // 6.2  取消了這個麵包屑以後，我們要跳轉到哪個地方，將請求的地址 url 裡面的當前置空
                // 拿到所有的查詢條件，去掉當前
                String encode = null;
                try {
                    encode = URLEncoder.encode(attr,"UTF-8");
                    encode.replace("+", "%20");  //瀏覽器對空格的編碼和Java不一樣，差異化處理
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                String replace = param.get_queryString().replace("&attrs=" + attr, "");
                navVo.setLink("http://search.cloudmall.com/list.html?" + replace);

                return navVo;
            }).collect(Collectors.toList());
            result.setNavs(collect);
        }
        return result;
    }

    /**
     * 準備檢索請求
     * 模糊匹配，過濾（按照屬性，分類，品牌，價格區間，庫存），排序，分頁，高亮，聚合分析
     * @return
     */
    private SearchRequest buildSearchRequest(SearchParam param) {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        /**
         * 模糊匹配，過濾（按照屬性，分類，品牌，價格區間，庫存）
         */
        // 1.構建 bool-query
        BoolQueryBuilder boolQueryBuilder=new BoolQueryBuilder();
        // 1.1 bool-must
        if(!StringUtils.isEmpty(param.getKeyword())){
            boolQueryBuilder.must(QueryBuilders.matchQuery("skuTitle",param.getKeyword()));
        }
        // 1.2 bool-fiter
        // 1.2.1 catelogId
        if(null != param.getCatalog3Id()){
            boolQueryBuilder.filter(QueryBuilders.termQuery("catalogId",param.getCatalog3Id()));
        }
        // 1.2.2 brandId
        if(null != param.getBrandId() && param.getBrandId().size() >0){
            boolQueryBuilder.filter(QueryBuilders.termsQuery("brandId",param.getBrandId()));
        }
        // 1.2.3 attrs
        if(param.getAttrs() != null && param.getAttrs().size() > 0){
            param.getAttrs().forEach(item -> {
                //attrs = 1_5寸:8寸 & 2_16G:8G
                BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
                //attrs = 1_5寸:8寸
                String[] s = item.split("_");
                String attrId=s[0];
                String[] attrValues = s[1].split(":");// 這個屬性檢索用的值
                boolQuery.must(QueryBuilders.termQuery("attrs.attrId",attrId));
                boolQuery.must(QueryBuilders.termsQuery("attrs.attrValue",attrValues));

                NestedQueryBuilder nestedQueryBuilder = QueryBuilders.nestedQuery("attrs",boolQuery, ScoreMode.None);
                boolQueryBuilder.filter(nestedQueryBuilder);
            });
        }
        // 1.2.4 hasStock
        if(null != param.getHasStock()){
            boolQueryBuilder.filter(QueryBuilders.termQuery("hasStock",param.getHasStock() == 1));
        }
        // 1.2.5 skuPrice
        if(!StringUtils.isEmpty(param.getSkuPrice())){
            // skuPrice 形式為：1_500 或 _500 或 500_
            RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("skuPrice");
            String[] price = param.getSkuPrice().split("_");
            if(price.length==2){
                rangeQueryBuilder.gte(price[0]).lte(price[1]);
            }else if(price.length == 1){
                if(param.getSkuPrice().startsWith("_")){
                    rangeQueryBuilder.lte(price[1]);
                }
                if(param.getSkuPrice().endsWith("_")){
                    rangeQueryBuilder.gte(price[0]);
                }
            }
            boolQueryBuilder.filter(rangeQueryBuilder);
        }
        // 封裝所有的查詢條件
        searchSourceBuilder.query(boolQueryBuilder);
        // 排序，分頁，高亮
        // 形式為 sort = hotScore_asc / desc
        if(!StringUtils.isEmpty(param.getSort())){
            String sort = param.getSort();
            String[] sortFileds = sort.split("_");
            SortOrder sortOrder="asc".equalsIgnoreCase(sortFileds[1])?SortOrder.ASC:SortOrder.DESC;
            searchSourceBuilder.sort(sortFileds[0],sortOrder);
        }
        // 分頁
        searchSourceBuilder.from((param.getPageNum() - 1) * EsConstant.PRODUCT_PAGESIZE);
        searchSourceBuilder.size(EsConstant.PRODUCT_PAGESIZE);
        //高亮
        if(!StringUtils.isEmpty(param.getKeyword())){
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder.field("skuTitle");
            highlightBuilder.preTags("<b style='color:red'>");
            highlightBuilder.postTags("</b>");
            searchSourceBuilder.highlighter(highlightBuilder);
        }
        // 聚合分析
        // 1.按照品牌進行聚合
        TermsAggregationBuilder brand_agg = AggregationBuilders.terms("brand_agg");
        brand_agg.field("brandId").size(50);
        // 1.1 品牌的子聚合-品牌名聚合
        brand_agg.subAggregation(AggregationBuilders.terms("brand_name_agg")
                .field("brandName").size(1));
        // 1.2 品牌的子聚合-品牌圖片聚合
        brand_agg.subAggregation(AggregationBuilders.terms("brand_img_agg")
                .field("brandImg").size(1));

        searchSourceBuilder.aggregation(brand_agg);
        // 2.按照分類資料進行聚合
        TermsAggregationBuilder catalog_agg = AggregationBuilders.terms("catalog_agg");
        catalog_agg.field("catalogId").size(20);
        catalog_agg.subAggregation(AggregationBuilders.terms("catalog_name_agg").field("catalogName").size(1));
        searchSourceBuilder.aggregation(catalog_agg);
        // 2.按照屬性資料進行聚合
        NestedAggregationBuilder attr_agg = AggregationBuilders.nested("attr_agg", "attrs");
        // 2.1 按照屬性ID進行聚合
        TermsAggregationBuilder attr_id_agg = AggregationBuilders.terms("attr_id_agg").field("attrs.attrId");
        attr_agg.subAggregation(attr_id_agg);
        // 2.1.1 在每個屬性ID下，按照屬性名進行聚合
        attr_id_agg.subAggregation(AggregationBuilders.terms("attr_name_agg").field("attrs.attrName").size(1));
        // 2.1.1 在每個屬性ID下，按照屬性值進行聚合
        attr_id_agg.subAggregation(AggregationBuilders.terms("attr_value_agg").field("attrs.attrValue").size(50));
        searchSourceBuilder.aggregation(attr_agg);
        log.debug("構建的DSL語句 {}",searchSourceBuilder.toString());

        SearchRequest searchRequest = new SearchRequest(new String[]{EsConstant.PRODUCT_INDEX},searchSourceBuilder);
        return searchRequest;
    }
}
