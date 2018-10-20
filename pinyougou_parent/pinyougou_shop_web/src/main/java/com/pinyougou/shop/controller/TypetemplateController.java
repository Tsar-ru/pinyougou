package com.pinyougou.shop.controller;

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
import java.util.Map;

@RestController
@RequestMapping("/typeTemplate")
public class TypetemplateController {

    @Reference
    private TypeTemplateService typeTemplateService;

    // 根据模板获取规格数据，但是从模板中获取的的数据格式是：
    // [{"id":27,"text":"网络"},{"id":32,"text":"机身内存"}]
    // 我们应该拼凑出这样的数据格式才能把规格和规格小项一起显示到页面上
    // [{"id":27,"text":"网络",options:[{id:1,optionName:'移动3G'},{},{}]},{"id":32,"text":"机身内存"}]
    @RequestMapping("/findSpecList/{id}")
    public List<Map> findSpecList(@PathVariable("id") Long id){
        return typeTemplateService.findSpecList(id);
    }

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
