package com.pinyougou.data.solr;

import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:spring/applicationContext-*.xml")
public class SolrManager {

    @Autowired
    private TbItemMapper itemMapper;

    @Autowired
    private SolrTemplate solrTemplate;
    @Test
    public void initSolrData(){
       List<TbItem> itemList = itemMapper.selectGrounding();//查询上架的sku数据
        for (TbItem tbItem : itemList) {
//            tbItem.getSpec() = {"网络":"双卡","机身内存":"128G"}
            Map specMap = JSON.parseObject(tbItem.getSpec(), Map.class);
            tbItem.setSpecMap(specMap);
        }
        solrTemplate.saveBeans(itemList);
        solrTemplate.commit();
    }

    @Test
    public void testQuery2(){
        HighlightQuery  highlightQuery = new SimpleHighlightQuery(new Criteria("item_title").is("小米"));
        HighlightOptions highlightOptions = new HighlightOptions();
        highlightOptions.addField("item_title");  //指定开启高亮的域
        highlightOptions.setSimplePrefix("<span style=\"color:red\">");
        highlightOptions.setSimplePostfix("</span>");
        highlightQuery.setHighlightOptions(highlightOptions);//设置高亮的属性
        HighlightPage<TbItem> highlightPage = solrTemplate.queryForHighlightPage(highlightQuery, TbItem.class);
        List<TbItem> content = highlightPage.getContent();
        for (TbItem tbItem : content) {
            List<HighlightEntry.Highlight> highlights = highlightPage.getHighlights(tbItem);
            String title = highlights.get(0).getSnipplets().get(0);
            tbItem.setTitle(title);
        }

        System.out.println(JSON.toJSONString(highlightPage,true));
    }
    @Test
    public void testQuery1(){

        List<String> categoryList = new ArrayList<String>();
        Query groupQuery = new SimpleQuery(new Criteria("item_keywords").is("三星"));
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
        System.out.println(JSON.toJSONString(groupResult,true));
    }
}
