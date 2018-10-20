package com.pinyougou.sellergoods.service;

import com.pinyougou.pojo.TbSpecification;
import entity.PageResult;
import groupEntity.Specification;

import java.util.List;
import java.util.Map;

public interface SpecificationService {
    public List<TbSpecification> findAll();

    PageResult findPage(int pageNum, int pageSize);

    void add(Specification specification);

    Specification findOne(Long id);

    void update(Specification specification);

    void dele(Long[] ids);

    PageResult search(int pageNum, int pageSize, TbSpecification specification);

    List<Map> findSpecList();
}
