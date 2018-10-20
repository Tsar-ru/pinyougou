package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbTypeTemplate;
import com.pinyougou.sellergoods.service.TypeTemplateService;
import entity.PageResult;
import entity.Result;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/typeTemplate")
public class TypetemplateController {

    @Reference
    private TypeTemplateService typeTemplateService;
    //    return $http.post("../typeTemplate/search/"+pageNum+"/"+pageSize,searchEntity);
    @RequestMapping("/search/{pageNum}/{pageSize}")
    public PageResult search(@PathVariable("pageNum") int pageNum, @PathVariable("pageSize") int pageSize, @RequestBody TbTypeTemplate typeTemplate){ //@PathVariable从url中获取数据
        return  typeTemplateService.search(pageNum,pageSize,typeTemplate);
    }

    @RequestMapping("/findAll")
    public List<TbTypeTemplate> findAll(){ //@PathVariable从url中获取数据
        return  typeTemplateService.findAll();
    }
    @RequestMapping("/add")
    public Result add(@RequestBody TbTypeTemplate typeTemplate){
        try {
            typeTemplateService.add(typeTemplate);
            return new Result(true,"");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"保存失败");
        }
    }
    @RequestMapping("/update")
    public Result update(@RequestBody TbTypeTemplate typeTemplate){
        try {
            typeTemplateService.update(typeTemplate);
            return new Result(true,"");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"保存失败");
        }
    }

    @RequestMapping("/findOne/{id}")
    public TbTypeTemplate findOne(@PathVariable("id") Long id){
        return  typeTemplateService.findOne(id);
    }
}
