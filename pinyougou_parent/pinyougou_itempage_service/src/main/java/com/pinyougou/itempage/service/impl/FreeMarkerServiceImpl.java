package com.pinyougou.itempage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.itempage.service.FreeMarkerService;
import com.pinyougou.mapper.TbGoodsDescMapper;
import com.pinyougou.mapper.TbGoodsMapper;
import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbGoodsDesc;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;
import groupEntity.Goods;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FreeMarkerServiceImpl implements FreeMarkerService {

    @Autowired
    private TbGoodsMapper goodsMapper;
    @Autowired
    private TbGoodsDescMapper goodsDescMapper;
    @Autowired
    private TbItemMapper itemMapper;
    @Autowired
    private TbItemCatMapper itemCatMapper;
    @Override
    public Goods findOne(Long goodsId) {
        Goods goods = new Goods();

        TbGoods tbGoods = goodsMapper.selectByPrimaryKey(goodsId);
        goods.setTbGoods(tbGoods);
        TbGoodsDesc tbGoodsDesc = goodsDescMapper.selectByPrimaryKey(goodsId);
        goods.setTbGoodsDesc(tbGoodsDesc);
//        select  * from tb_item where goods_id=?
        TbItemExample example = new TbItemExample();
        example.createCriteria().andGoodsIdEqualTo(goodsId);
        List<TbItem> itemList = itemMapper.selectByExample(example);
        goods.setItemList(itemList);

        Map catMap = new HashMap();

        catMap.put("category1",itemCatMapper.selectByPrimaryKey(tbGoods.getCategory1Id()).getName());
        catMap.put("category2",itemCatMapper.selectByPrimaryKey(tbGoods.getCategory2Id()).getName());
        catMap.put("category3",itemCatMapper.selectByPrimaryKey(tbGoods.getCategory3Id()).getName());
        goods.setCatMap(catMap);


        return goods;
    }

    @Override
    public List<Goods> findAllGoods() {
        List<Goods> goodsList = new ArrayList<Goods>();
        List<TbGoods> tbGoodsList = goodsMapper.selectByExample(null);
        for (TbGoods tbGoods : tbGoodsList) {
            Goods goods = findOne(tbGoods.getId());
            goodsList.add(goods);
        }
        return goodsList;
    }
}
