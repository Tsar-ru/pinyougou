package com.pinyougou.itempage.service;

import groupEntity.Goods;

import java.util.List;

public interface FreeMarkerService {
    Goods findOne(Long goodsId);

    List<Goods> findAllGoods();
}
