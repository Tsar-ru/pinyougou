package com.pinyougou.shop.controller;
import java.util.List;

import groupEntity.Goods;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.sellergoods.service.GoodsService;

import entity.PageResult;
import entity.Result;
/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

	@Reference
	private GoodsService goodsService;
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbGoods> findAll(){			
		return goodsService.findAll();
	}
	
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findPage/{pageNum}/{pageSize}")
	public PageResult  findPage(@PathVariable("pageNum") int pageNum,@PathVariable("pageSize") int pageSize){			
		return goodsService.findPage(pageNum, pageSize);
	}
	
	/**
	 * 增加
	 * @param goods
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody Goods goods){
		try {
			//			`seller_id` varchar(20) DEFAULT NULL COMMENT '商家ID',
			String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
			goods.getTbGoods().setSellerId(sellerId);
			goodsService.add(goods);



			return new Result(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "增加失败");
		}
	}
	
	/**
	 * 修改
	 * @param goods
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody TbGoods goods){
		try {
			goodsService.update(goods);
			return new Result(true, "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "修改失败");
		}
	}	
	
	/**
	 * 获取实体
	 * @param id
	 * @return
	 */
	@RequestMapping("/findOne/{id}")
	public TbGoods findOne(@PathVariable("id") Long id){
		return goodsService.findOne(id);		
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete/{ids}")
	public Result delete(@PathVariable("ids") Long[] ids){
		try {
			goodsService.delete(ids);
			return new Result(true, "删除成功"); 
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}
	
		/**
	 * 查询+分页
	 * @param 
	 * @param 
	 * @param 
	 * @return
	 */
	  
	@RequestMapping("/search/{pageNum}/{pageSize}")
	public PageResult search(@RequestBody TbGoods goods, @PathVariable("pageNum") int pageNum,@PathVariable("pageSize") int pageSize ){
        goods.setSellerId(SecurityContextHolder.getContext().getAuthentication().getName());
		return goodsService.findPage(goods, pageNum, pageSize);		
	}

	@RequestMapping("/updateIsMarketable/{status}/{ids}")
	public Result updateIsMarketable( @PathVariable("status") String  status,@PathVariable("ids") Long[] ids ){
		try {
			goodsService.updateIsMarketable(status, ids);
			/*上架：
			1、修改商品状态
			goodsService.updateIsMarketable(status, ids);1
			2、同步solr
					searchService.sssss      1
			3、同步静态页
				itempageService.sssss*/


			return  new Result(true,"");
		} catch (Exception e) {
			e.printStackTrace();
			return  new Result(false,"状态修改失败");
		}
	}

}
