package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbSpecificationMapper;
import com.pinyougou.mapper.TbSpecificationOptionMapper;
import com.pinyougou.pojo.TbSpecification;
import com.pinyougou.pojo.TbSpecificationExample;
import com.pinyougou.pojo.TbSpecificationOption;
import com.pinyougou.pojo.TbSpecificationOptionExample;
import com.pinyougou.sellergoods.service.SpecificationService;
import entity.PageResult;
import groupEntity.Specification;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class SpecificationServiceImpl implements SpecificationService {

    @Autowired
    private TbSpecificationMapper specificationMapper;
    @Autowired
    private TbSpecificationOptionMapper specificationOptionMapper;

    @Override
    public List<TbSpecification> findAll() {
        return specificationMapper.selectByExample(null);
    }

    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        Page<TbSpecification> page = (Page) specificationMapper.selectByExample(null);
//        page.getTotal() 总条数,page.getResult() 当前页的结果
        return new PageResult(page.getTotal(),page.getResult());
    }

    @Override
    public void add(Specification specification) {
//        保存两个表
//        tb_specification
        TbSpecification tbSpecification = specification.getTbSpecification();
        specificationMapper.insert(tbSpecification);  //需要返回对象的id  修改映射文件 在insert时 添加selectKey

//        tb_specification_option
        List<TbSpecificationOption> tbSpecificationOptionList = specification.getTbSpecificationOptionList();
        for (TbSpecificationOption tbSpecificationOption : tbSpecificationOptionList) {
            tbSpecificationOption.setSpecId(tbSpecification.getId()); //设置外键
            specificationOptionMapper.insert(tbSpecificationOption);
        }


    }

    @Override
    public Specification findOne(Long id) {
//        返回的是组合类 向组合类中set两个属性
        Specification specification = new Specification();
        TbSpecification tbSpecification = specificationMapper.selectByPrimaryKey(id);
        specification.setTbSpecification(tbSpecification);

//        select * from tb_specification_option where spec_id=id
        TbSpecificationOptionExample example = new TbSpecificationOptionExample();
        example.createCriteria().andSpecIdEqualTo(id);
        List<TbSpecificationOption> tbSpecificationOptionList = specificationOptionMapper.selectByExample(example);
        specification.setTbSpecificationOptionList(tbSpecificationOptionList);
        return  specification;
    }

    @Override
    public void update(Specification specification) {
        TbSpecification tbSpecification = specification.getTbSpecification();
        specificationMapper.updateByPrimaryKey(tbSpecification);

//        对于从表中的数据需要先根据specId删除后新增   delete  from  tb_specification_option where spec_id=?
        TbSpecificationOptionExample example = new TbSpecificationOptionExample();
        example.createCriteria().andSpecIdEqualTo(tbSpecification.getId());
        specificationOptionMapper.deleteByExample(example);

        List<TbSpecificationOption> tbSpecificationOptionList = specification.getTbSpecificationOptionList();
        for (TbSpecificationOption tbSpecificationOption : tbSpecificationOptionList) {
            tbSpecificationOption.setSpecId(tbSpecification.getId());  //设置外键
            specificationOptionMapper.insert(tbSpecificationOption);
        }

    }

    @Override
    public void dele(Long[] ids) {
        for (Long id : ids) {
            specificationMapper.deleteByPrimaryKey(id);  //删除了主表
//            删除从表中的数据 delete  from  tb_specification_option where spec_id=?
            TbSpecificationOptionExample example = new TbSpecificationOptionExample();
            example.createCriteria().andSpecIdEqualTo(id);
            specificationOptionMapper.deleteByExample(example);
        }
    }

    @Override
    public PageResult search(int pageNum, int pageSize, TbSpecification specification) {

        PageHelper.startPage(pageNum,pageSize);
        TbSpecificationExample example = new TbSpecificationExample();

//        构建条件  specification.getName  specification.getFirstChar
        TbSpecificationExample.Criteria criteria = example.createCriteria();

        Page<TbSpecification> page = (Page<TbSpecification>)specificationMapper.selectByExample(example);
        return new PageResult(page.getTotal(),page.getResult());
    }

    @Override
    public List<Map> findSpecList() {
        return specificationMapper.findSpecList();
    }
}