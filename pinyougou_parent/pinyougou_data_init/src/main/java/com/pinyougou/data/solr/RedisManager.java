package com.pinyougou.data.solr;

import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.mapper.TbSpecificationOptionMapper;
import com.pinyougou.mapper.TbTypeTemplateMapper;
import com.pinyougou.pojo.TbItemCat;
import com.pinyougou.pojo.TbSpecificationOption;
import com.pinyougou.pojo.TbSpecificationOptionExample;
import com.pinyougou.pojo.TbTypeTemplate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:spring/applicationContext-*.xml")
public class RedisManager {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private TbItemCatMapper itemCatMapper;

    @Autowired
    private TbTypeTemplateMapper typeTemplateMapper;
    @Autowired
    private TbSpecificationOptionMapper specificationOptionMapper;
    @Test
    public void initRedis(){
//        初始化每个分类与品牌、规格的关系
//        1、查询所有的分类数据
        List<TbItemCat> itemCatList = itemCatMapper.selectByExample(null);
        for (TbItemCat tbItemCat : itemCatList) {
            TbTypeTemplate typeTemplate = typeTemplateMapper.selectByPrimaryKey(tbItemCat.getTypeId());
            String brandIds = typeTemplate.getBrandIds(); // "[{id:1,text:''},{}]"
            redisTemplate.boundHashOps("itemcat_brands").put(tbItemCat.getName(), JSON.parseArray(brandIds, Map.class));

            String specIds = typeTemplate.getSpecIds(); //"[{id:1,text:'',options:[{},{}]},{}]"
            List<Map> specMap = JSON.parseArray(specIds, Map.class);
//        [{"id":27,"text":"网络"},{"id":32,"text":"机身内存"}]
            for (Map map : specMap) {
//            为map中追加options属性  options属性对应的值 select * from tb_specification_option where spec_id= map.get("id")
                TbSpecificationOptionExample example = new TbSpecificationOptionExample();
                example.createCriteria().andSpecIdEqualTo(Long.parseLong(map.get("id")+""));
                List<TbSpecificationOption> tbSpecificationOptions = specificationOptionMapper.selectByExample(example);
                map.put("options",tbSpecificationOptions);
            }
            redisTemplate.boundHashOps("itemcat_specs").put(tbItemCat.getName(),specMap);

        }
    }
}
