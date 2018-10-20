package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbSpecificationOptionMapper;
import com.pinyougou.mapper.TbTypeTemplateMapper;
import com.pinyougou.pojo.TbSpecificationOption;
import com.pinyougou.pojo.TbSpecificationOptionExample;
import com.pinyougou.pojo.TbTypeTemplate;
import com.pinyougou.sellergoods.service.TypeTemplateService;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

@Service
public class TypeTemplateServiceImpl implements TypeTemplateService {
    @Autowired
    private TbTypeTemplateMapper typeTemplateMapper;
    @Autowired
    private TbSpecificationOptionMapper specificationOptionMapper;
    @Override
    public PageResult search(int pageNum, int pageSize, TbTypeTemplate typeTemplate) {
        PageHelper.startPage(pageNum,pageSize);
        Page<TbTypeTemplate> page =( Page<TbTypeTemplate> )typeTemplateMapper.selectByExample(null);
        return new PageResult(page.getTotal(),page.getResult());
    }

    @Override
    public void add(TbTypeTemplate typeTemplate) {
        typeTemplateMapper.insert(typeTemplate);
    }

    @Override
    public void update(TbTypeTemplate typeTemplate) {
        typeTemplateMapper.updateByPrimaryKey(typeTemplate);
    }

    @Override
    public TbTypeTemplate findOne(Long id) {
        return typeTemplateMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<TbTypeTemplate> findAll() {
        return typeTemplateMapper.selectByExample(null);
    }

    // 根据模板获取规格数据，但是从模板中获取的的数据格式是：
    // [{"id":27,"text":"网络"},{"id":32,"text":"机身内存"}]
    // 我们应该拼凑出这样的数据格式才能把规格和规格小项一起显示到页面上
    // [{"id":27,"text":"网络",options:[{id:1,optionName:'移动3G'},{},{}]},{"id":32,"text":"机身内存"}]
    @Override
    public List<Map> findSpecList(Long id) {
        TbTypeTemplate template = typeTemplateMapper.selectByPrimaryKey(id);
        String specIds = template.getSpecIds();//[{"id":27,"text":"网络"},{"id":32,"text":"机身内存"}]
//        List----String--List
//       String 字符串 = JSON.toJSONString(对象)；
//         Object 对象 = JSON.parse(字符串)；
        List<Map> specMap = JSON.parseArray(specIds, Map.class);
//        [{"id":27,"text":"网络"},{"id":32,"text":"机身内存"}]
        for (Map map : specMap) {
//            为map中追加options属性  options属性对应的值 select * from tb_specification_option where spec_id= map.get("id")
            TbSpecificationOptionExample example = new TbSpecificationOptionExample();
            example.createCriteria().andSpecIdEqualTo(Long.parseLong(map.get("id")+""));
            List<TbSpecificationOption> tbSpecificationOptions = specificationOptionMapper.selectByExample(example);
            map.put("options",tbSpecificationOptions);
        }
//        [{"id":27,"text":"网络",options:[{id:1,optionName:'移动3G'},{},{}]},{"id":32,"text":"机身内存"，options:[]}]
        return specMap;
    }
}
