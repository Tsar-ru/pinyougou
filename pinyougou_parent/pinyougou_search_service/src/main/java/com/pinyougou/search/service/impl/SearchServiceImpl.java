package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SearchServiceImpl implements SearchService {
    @Autowired
    private SolrTemplate solrTemplate;
    @Autowired
    private RedisTemplate redisTemplate;
    @Override
    public Map searchByParamMap(Map paramMap) {

        Map resultMap = new HashMap();

//        根据关键字查询分类数据  三星------》手机  平板电脑
//        select category from tb_item where title like '%三星%'  GROUP BY category
        /*根据关键字查询分类数据 开始*/
        List<String> categoryList = new ArrayList<String>();
        Query groupQuery = new SimpleQuery(new Criteria("item_keywords").is(paramMap.get("keyword")));
        GroupOptions groupOption = new GroupOptions();
        groupOption.addGroupByField("item_category");
        groupQuery.setGroupOptions(groupOption);
        GroupPage<TbItem> groupPage = solrTemplate.queryForGroupPage(groupQuery, TbItem.class);
        GroupResult<TbItem> groupResult = groupPage.getGroupResult("item_category");
        Page<GroupEntry<TbItem>> groupEntries = groupResult.getGroupEntries();
        List<GroupEntry<TbItem>> groupEntryList = groupEntries.getContent();
        for (GroupEntry<TbItem> tbItemGroupEntry : groupEntryList) {
            String groupValue = tbItemGroupEntry.getGroupValue();
            categoryList.add(groupValue);
        }
        resultMap.put("categoryList",categoryList);
        /*根据关键字查询分类数据 结束*/

        /*根据第一个分类查询品牌数据*/
        String category = categoryList.get(0);
        List<Map> brandList = (List<Map>) redisTemplate.boundHashOps("itemcat_brands").get(category);
        resultMap.put("brandList",brandList);
        /*根据第一个分类查询规格数据*/
        List<Map> specList = (List<Map>) redisTemplate.boundHashOps("itemcat_specs").get(category);
        resultMap.put("specList",specList);



        SimpleHighlightQuery highlightQuery = new SimpleHighlightQuery(new Criteria("item_keywords").is(paramMap.get("keyword")));
        HighlightOptions highlightOptions = new HighlightOptions();
        highlightOptions.addField("item_title");  //指定开启高亮的域
        highlightOptions.setSimplePrefix("<span style=\"color:red\">");
        highlightOptions.setSimplePostfix("</span>");
        highlightQuery.setHighlightOptions(highlightOptions);//设置高亮的属性

//        添加过滤条件
//       分类
        if(!"".equals(paramMap.get("category"))){
            highlightQuery.addFilterQuery(new SimpleFilterQuery(new Criteria("item_category").is(paramMap.get("category"))));
            System.out.println("添加了category的过滤条件");
        }
//        品牌
        if(!"".equals(paramMap.get("brand"))){
            highlightQuery.addFilterQuery(new SimpleFilterQuery(new Criteria("item_brand").is(paramMap.get("brand"))));
            System.out.println("添加了brand的过滤条件");
        }
//        规格
//        item_spec_网络:联通3G
//        spec={'网络':' 移动3G','机身内存':'16G'}
        Map<String,String> specSpec = (Map) paramMap.get("spec");
        for (String key : specSpec.keySet()) {
            highlightQuery.addFilterQuery(new SimpleFilterQuery(new Criteria("item_spec_"+key).is( specSpec.get(key) )));
            System.out.println("添加了spec:"+key+"的过滤条件");
        }
//        价格区间
        if(!"".equals(paramMap.get("price"))){
//            0-500  500-1000
            String[] prices = paramMap.get("price").toString().split("-");
            if("*".equals(prices[1])){
                //            3000-*
                highlightQuery.addFilterQuery(new SimpleFilterQuery(new Criteria("item_price").greaterThanEqual(prices[0])));

            }else{
                highlightQuery.addFilterQuery(new SimpleFilterQuery(new Criteria("item_price").between(prices[0],prices[1],true,true)));
            }
           System.out.println("添加了price的过滤条件");
        }

//        排序
        if("asc".equals(paramMap.get("order"))){
            highlightQuery.addSort(new Sort(Sort.Direction.ASC,"item_price"));
        }else{
            highlightQuery.addSort(new Sort(Sort.Direction.DESC,"item_price"));
        }

//        设置分页  ：起始位置  每页显示的条数：60
        Integer page = (Integer) paramMap.get("page");
        highlightQuery.setOffset((page-1) * 60);  //如果第一页:0  如果是第二页：60  第三页：120 第四页：180
        highlightQuery.setRows(60);  //每页显示的条数



        HighlightPage<TbItem> highlightPage = solrTemplate.queryForHighlightPage(highlightQuery, TbItem.class);
        List<TbItem> itemList = highlightPage.getContent();

        for (TbItem tbItem : itemList) {
            List<HighlightEntry.Highlight> highlights = highlightPage.getHighlights(tbItem);
            if(highlights!=null&&highlights.size()>0){
                HighlightEntry.Highlight highlight = highlights.get(0);
                List<String> snipplets = highlight.getSnipplets();
                if(snipplets!=null&&snipplets.size()>0){
                    tbItem.setTitle(snipplets.get(0));
                }

            }
            
        }

        resultMap.put("totalPages",highlightPage.getTotalPages());//总页数
        resultMap.put("total",highlightPage.getTotalElements()); //总条数
        resultMap.put("itemList",itemList);  //当前页的数据列表

        return  resultMap;
    }

}
