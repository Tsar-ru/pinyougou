package com.pinyougou.portal.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.content.service.ContentService;
import com.pinyougou.pojo.TbContent;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/index")
public class IndecController {

    @Reference
    private ContentService contentService;

    @RequestMapping("/findByCategotyId")
    public List<TbContent> findByCategotyId(Long categoryId){
       return contentService.findByCategotyId(categoryId);
    }
}