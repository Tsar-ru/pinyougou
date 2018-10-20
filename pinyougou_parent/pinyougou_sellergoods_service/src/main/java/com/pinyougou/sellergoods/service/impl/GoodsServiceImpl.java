package com.pinyougou.sellergoods.service.impl;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.*;
import com.pinyougou.pojo.TbGoodsDesc;
import com.pinyougou.pojo.TbItem;
import com.sun.tools.javac.api.ClientCodeWrapper;
import groupEntity.Goods;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbGoodsExample;
import com.pinyougou.pojo.TbGoodsExample.Criteria;
import com.pinyougou.sellergoods.service.GoodsService;

import entity.PageResult;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.transaction.annotation.Transactional;

import javax.jms.*;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
@Transactional
public class GoodsServiceImpl implements GoodsService {

	@Autowired
	private TbGoodsMapper goodsMapper;
	
	@Autowired
	private TbGoodsDescMapper goodsDescMapper;

	@Autowired
	private TbItemMapper itemMapper;
	@Autowired
	private TbItemCatMapper itemCatMapper;
	@Autowired
	private TbBrandMapper brandMapper;
	@Autowired
	private TbSellerMapper sellerMapper;

	/**
	 * 查询全部
	 */
	@Override
	public List<TbGoods> findAll() {
		return goodsMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbGoods> page=   (Page<TbGoods>) goodsMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(Goods goods) {
		TbGoods tbGoods = goods.getTbGoods();
//		private String auditStatus; //0：未审核  1：已审核  2：审核未通过
//		private String isMarketable;//0:未上架  1：已上架 2：已下架
//  `audit_status` varchar(2) DEFAULT NULL COMMENT '状态',
		tbGoods.setAuditStatus("0");   //0
//  `is_marketable` varchar(1) DEFAULT NULL COMMENT '是否上架',
		tbGoods.setIsMarketable("0");
//  `is_delete` varchar(1) DEFAULT NULL COMMENT '是否删除',
		tbGoods.setIsDelete("0");
		goodsMapper.insert(tbGoods); //注意selectKey

		TbGoodsDesc tbGoodsDesc = goods.getTbGoodsDesc();
		tbGoodsDesc.setGoodsId(tbGoods.getId());
		goodsDescMapper.insert(tbGoodsDesc);

		if(tbGoods.getIsEnableSpec().equals("1")){ //代表启用规格
			List<TbItem> itemList = goods.getItemList();
			for (TbItem tbItem : itemList) {
	//   title = tbGoods.getGoodsName();
	//  `title` varchar(100) NOT NULL COMMENT '商品标题',   // 小米6X 移动4G 64G   tbItem.spec {"网络":"移动4G","机身内存":"64G"}
			String title = tbGoods.getGoodsName();
			String spec = tbItem.getSpec();  // spec: {"网络":"移动4G","机身内存":"64G"}
			Map<String,String> specMap = JSON.parseObject(spec, Map.class);
			for(String key:specMap.keySet()){
				title+= " "+specMap.get(key);
			}
				tbItem.setTitle(title);
				tbItem = createTbItem(tbItem,tbGoods,tbGoodsDesc);

				itemMapper.insert(tbItem);
			}
		}else{
//			需要保存一条TbItem数据
			TbItem tbItem = new TbItem();
			tbItem.setTitle( tbGoods.getGoodsName());
			tbItem.setSpec("{}");
			tbItem.setPrice(tbGoods.getPrice());
			tbItem.setNum(9999);
			tbItem.setStatus("1");
			tbItem.setIsDefault("0");
			tbItem = createTbItem(tbItem,tbGoods,tbGoodsDesc);
			itemMapper.insert(tbItem);
		}
	}

	private TbItem createTbItem(TbItem tbItem,TbGoods tbGoods,TbGoodsDesc tbGoodsDesc){
		//  `sell_point` varchar(500) DEFAULT NULL COMMENT '商品卖点', //从TbGoods的 副标题中获取
		tbItem.setSellPoint(tbGoods.getCaption());
		//  `image` varchar(2000) DEFAULT NULL COMMENT '商品图片', //从 TBGoodsDesc中的图片列表中获取第一张图片的url
		String itemImages = tbGoodsDesc.getItemImages(); //[{"color":"白色","url":"http://192.168.25.133/group1/M00/00/00/wKgZhVmOWsOAPwNYAAjlKdWCzvg742.jpg"},
		// {"color":"黑色","url":"http://192.168.25.133/group1/M00/00/00/wKgZhVmOWs2ABppQAAETwD7A1Is142.jpg"}]
		List<Map> imageMapList = JSON.parseArray(itemImages, Map.class);
		if(imageMapList.size()>0){
			tbItem.setImage(imageMapList.get(0).get("url")+"");
		}

		//  `categoryId` bigint(10) NOT NULL COMMENT '所属类目，叶子类目',  //从TBGoods中获取第三级的类目id
		tbItem.setCategoryid(tbGoods.getCategory3Id());
		//  `create_time` datetime NOT NULL COMMENT '创建时间',
		//  `update_time` datetime NOT NULL COMMENT '更新时间',
		tbItem.setCreateTime(new Date());
		tbItem.setUpdateTime(new Date());
		//  `goods_id` bigint(20) DEFAULT NULL,  取的是TBGoods的id
		tbItem.setGoodsId(tbGoods.getId());
		//  `seller_id` varchar(30) DEFAULT NULL,  可以从 TBGoods中的获取
		tbItem.setSellerId(tbGoods.getSellerId());
		//  `category` varchar(200) DEFAULT NULL,  第三级类目的名称  根据ID查询类目对象 获取名称
		tbItem.setCategory((itemCatMapper.selectByPrimaryKey(tbItem.getCategoryid())).getName());
		//  `brand` varchar(100) DEFAULT NULL,   品牌的名称          根据TBGoods中brandID查询品牌对象 获取名称
		tbItem.setBrand((brandMapper.selectByPrimaryKey(tbGoods.getBrandId())).getName());
		//  `seller` varchar(200) DEFAULT NULL  上架的名称           根据TBGoods中selllerId查询商家对象 获取名称
		tbItem.setSeller((sellerMapper.selectByPrimaryKey(tbGoods.getSellerId())).getName());
		return tbItem;
	}
	
	/**
	 * 修改
	 */
	@Override
	public void update(TbGoods goods){
		goodsMapper.updateByPrimaryKey(goods);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbGoods findOne(Long id){
		return goodsMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			goodsMapper.deleteByPrimaryKey(id);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbGoods goods, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbGoodsExample example=new TbGoodsExample();
		Criteria criteria = example.createCriteria();
		
		if(goods!=null){			
						if(goods.getSellerId()!=null && goods.getSellerId().length()>0){
//						    laowang   dalaowang
				criteria.andSellerIdEqualTo(goods.getSellerId());
			}
			if(goods.getGoodsName()!=null && goods.getGoodsName().length()>0){
				criteria.andGoodsNameLike("%"+goods.getGoodsName()+"%");
			}
			if(goods.getAuditStatus()!=null && goods.getAuditStatus().length()>0){
				criteria.andAuditStatusLike("%"+goods.getAuditStatus()+"%");
			}
			if(goods.getIsMarketable()!=null && goods.getIsMarketable().length()>0){
				criteria.andIsMarketableLike("%"+goods.getIsMarketable()+"%");
			}
			if(goods.getCaption()!=null && goods.getCaption().length()>0){
				criteria.andCaptionLike("%"+goods.getCaption()+"%");
			}
			if(goods.getSmallPic()!=null && goods.getSmallPic().length()>0){
				criteria.andSmallPicLike("%"+goods.getSmallPic()+"%");
			}
			if(goods.getIsEnableSpec()!=null && goods.getIsEnableSpec().length()>0){
				criteria.andIsEnableSpecLike("%"+goods.getIsEnableSpec()+"%");
			}
			if(goods.getIsDelete()!=null && goods.getIsDelete().length()>0){
				criteria.andIsDeleteLike("%"+goods.getIsDelete()+"%");
			}
	
		}
		
		Page<TbGoods> page= (Page<TbGoods>)goodsMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

    @Override
    public void updateAuditStatus(String status, Long[] ids) {
//	    status 1 ：通过  2：驳回
        for (Long id : ids) {
            TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
            tbGoods.setAuditStatus(status);
            goodsMapper.updateByPrimaryKey(tbGoods);
        }
    }

    @Autowired
    private JmsTemplate jmsTemplate;
    @Autowired
	@Qualifier("updateSolrItempage")
    private Destination updateSolrItempage;
    @Autowired
	@Qualifier("deleteSolrItempage")
    private Destination deleteSolrItempage;

    @Override
    public void updateIsMarketable(String status, Long[] ids) {
//	    status 1 ：上架  2：下架
        for (Long id : ids) {
            TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
            tbGoods.setIsMarketable(status);
            goodsMapper.updateByPrimaryKey(tbGoods);
        }
//        同步添加solr
//		同步创建静态页
		if("1".equals(status)){
//        	代表上架，把每个goodsId放到mq的updateSolrItempage队中
			for (Long id : ids) {
				jmsTemplate.send(updateSolrItempage, new MessageCreator() {
					@Override
					public Message createMessage(Session session) throws JMSException {
						TextMessage textMessage = session.createTextMessage(id + "");
						return textMessage;
					}
				});
			}
			System.out.println("put update goodsId ok!!");
		}else{
			for (Long id : ids) {
				jmsTemplate.send(deleteSolrItempage, new MessageCreator() {
					@Override
					public Message createMessage(Session session) throws JMSException {
						TextMessage textMessage = session.createTextMessage(id + "");
						return textMessage;
					}
				});
			}
			System.out.println("put delete goodsId ok!!");

		}
    }

}
