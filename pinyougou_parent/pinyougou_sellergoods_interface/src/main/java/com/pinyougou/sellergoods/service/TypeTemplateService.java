package com.pinyougou.sellergoods.service;

import com.pinyougou.pojo.TbTypeTemplate;
import entity.PageResult;

import java.util.List;
import java.util.Map;

public interface TypeTemplateService {
    public PageResult search( int pageNum ,int pageSize ,TbTypeTemplate typeTemplate);

    void add(TbTypeTemplate typeTemplate);

    void update(TbTypeTemplate typeTemplate);

    TbTypeTemplate findOne(Long id);

    List<TbTypeTemplate> findAll();

    List<Map> findSpecList(Long id);
}
