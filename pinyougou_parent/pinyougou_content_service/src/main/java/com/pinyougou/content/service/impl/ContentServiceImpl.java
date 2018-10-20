package com.pinyougou.content.service.impl;
import java.util.List;

import com.pinyougou.content.service.ContentService;
import org.aspectj.lang.annotation.SuppressAjWarnings;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbContentMapper;
import com.pinyougou.pojo.TbContent;
import com.pinyougou.pojo.TbContentExample;
import com.pinyougou.pojo.TbContentExample.Criteria;

import entity.PageResult;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class ContentServiceImpl implements ContentService {

	@Autowired
	private RedisTemplate redisTemplate;
	@Autowired
	private TbContentMapper contentMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbContent> findAll() {
		return contentMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbContent> page=   (Page<TbContent>) contentMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbContent content) {
		contentMapper.insert(content);
//		同步更新redis----->把redis的数据 按照分类id清空
		redisTemplate.boundHashOps("contentList").delete(content.getCategoryId());
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbContent content){

		TbContent tbContent = contentMapper.selectByPrimaryKey(content.getId());
		; // 从首页轮播广告
		contentMapper.updateByPrimaryKey(content);
		//		同步更新redis
//		假如 分类 从首页轮播广告 ----》 今日推荐   content.getCategoryId()就是今日推荐分类的id
		redisTemplate.boundHashOps("contentList").delete(content.getCategoryId());
		if(content.getCategoryId().longValue() != tbContent.getCategoryId().longValue() ){
			redisTemplate.boundHashOps("contentList").delete(tbContent.getCategoryId());
		}
	}
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbContent findOne(Long id){
		return contentMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		//		同步更新redis
		for(Long id:ids){
			TbContent content = contentMapper.selectByPrimaryKey(id);
			//		同步更新redis----->把redis的数据 按照分类id清空
			redisTemplate.boundHashOps("contentList").delete(content.getCategoryId());
			contentMapper.deleteByPrimaryKey(id);
		}


	}
	
	
		@Override
	public PageResult findPage(TbContent content, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbContentExample example=new TbContentExample();
		Criteria criteria = example.createCriteria();
		
		if(content!=null){			
						if(content.getTitle()!=null && content.getTitle().length()>0){
				criteria.andTitleLike("%"+content.getTitle()+"%");
			}
			if(content.getUrl()!=null && content.getUrl().length()>0){
				criteria.andUrlLike("%"+content.getUrl()+"%");
			}
			if(content.getPic()!=null && content.getPic().length()>0){
				criteria.andPicLike("%"+content.getPic()+"%");
			}
			if(content.getStatus()!=null && content.getStatus().length()>0){
				criteria.andStatusLike("%"+content.getStatus()+"%");
			}
	
		}
		
		Page<TbContent> page= (Page<TbContent>)contentMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Override
	public List<TbContent> findByCategotyId(Long categoryId) {

		List<TbContent> contentList = (List<TbContent>) redisTemplate.boundHashOps("contentList").get(categoryId);

		if(contentList==null){
//			从mysql中查询 ，并且放入到redis中
			//		根据分类查询是广告数据    select  * from tb_content where categoty_id=? and status=1  order by sort_order
			TbContentExample example=new TbContentExample();
			example.createCriteria().andCategoryIdEqualTo(categoryId).andStatusEqualTo("1");
//		排序
			example.setOrderByClause("sort_order");
			contentList = contentMapper.selectByExample(example);
//			并且放入到redis中
			redisTemplate.boundHashOps("contentList").put(categoryId,contentList);
			System.out.println("from MYSQL ");
		}else{
			System.out.println("from redis");
		}
		return contentList;
	}



}
