package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.pojo.TbBrandExample;
import com.pinyougou.sellergoods.service.BrandService;
import entity.PageResult;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

@Service
public class BrandServiceImpl implements BrandService {

    @Autowired
    private TbBrandMapper brandMapper;

    @Override
    public List<TbBrand> findAll() {
        return brandMapper.selectByExample(null);
    }

    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        Page<TbBrand> page = (Page) brandMapper.selectByExample(null);
//        page.getTotal() 总条数,page.getResult() 当前页的结果
        return new PageResult(page.getTotal(),page.getResult());
    }

    @Override
    public void add(TbBrand brand) {
        brandMapper.insert(brand);
    }

    @Override
    public TbBrand findOne(Long id) {
        return brandMapper.selectByPrimaryKey(id);
    }

    @Override
    public void update(TbBrand brand) {
        brandMapper.updateByPrimaryKey(brand);
    }

    @Override
    public void dele(Long[] ids) {
        for (Long id : ids) {
            brandMapper.deleteByPrimaryKey(id);
        }
    }

    @Override
    public PageResult search(int pageNum, int pageSize, TbBrand brand) {

        PageHelper.startPage(pageNum,pageSize);
        TbBrandExample example = new TbBrandExample();

//        构建条件  brand.getName  brand.getFirstChar
        TbBrandExample.Criteria criteria = example.createCriteria();
        if(StringUtils.isNotBlank(brand.getName())){//判断是否为空
            criteria.andNameLike("%"+brand.getName()+"%");
        }
        if(StringUtils.isNotBlank(brand.getFirstChar())){//判断是否为空
            criteria.andFirstCharEqualTo(brand.getFirstChar());
        }
        Page<TbBrand> page = (Page<TbBrand>)brandMapper.selectByExample(example);
        return new PageResult(page.getTotal(),page.getResult());
    }

    @Override
    public List<Map> findBrandList() {
        return brandMapper.findBrandList();
    }
}