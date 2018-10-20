package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.sellergoods.service.BrandService;
import entity.PageResult;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

//@Controller
//@ResponseBody //转json 并且回显到浏览器
@RestController
@RequestMapping("/brand")
public class BrandController {

    @Reference
    private BrandService brandService;
    // 此方法是select2要求的  需要的格式 [{'id':1,'text':'aaaa'},{'id':2,'text':'bbbb'},{'id':3,'text':'cccc'}]
    @RequestMapping("/findBrandList")
    public List<Map> findBrandList(){
       return brandService.findBrandList();
    }
    @RequestMapping("/findAll")
    public List<TbBrand> findAll(){
       return brandService.findAll();
    }
    @RequestMapping("/findPage")
    public PageResult findPage(int pageNum, int pageSize){
//        {total:110,rows:[{},{},{}]}
       return brandService.findPage(pageNum,pageSize);
    }
    @RequestMapping("/add")
    public Result add(@RequestBody TbBrand brand){
//        {success:true|false,message:"保存成功"|"保存失败"}
       try {
           brandService.add(brand);
           return new Result(true,"保存成功");
       }catch (Exception e){
           e.printStackTrace();
           return new Result(false,"保存失败");
       }
    }

    @RequestMapping("/update")
    public Result update(@RequestBody TbBrand brand){
        try {
            brandService.update(brand);
            return new Result(true,"修改成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"修改失败");
        }
    }
    @RequestMapping("/dele")
    public Result dele(Long[] ids){
        try {
            brandService.dele(ids);
            return new Result(true,"删除成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"删除失败");
        }
    }
    @RequestMapping("/findOne")
    public TbBrand findOne(Long id){
        return  brandService.findOne(id);
    }

    @RequestMapping("/search")
    public PageResult findPage(int pageNum, int pageSize,@RequestBody TbBrand brand){
//        {total:110,rows:[{},{},{}]}
        return brandService.search(pageNum,pageSize,brand);
    }

}
