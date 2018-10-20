package com.pinyougou.itempage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.itempage.service.FreeMarkerService;
import com.pinyougou.pojo.TbItem;
import freemarker.template.Configuration;
import freemarker.template.Template;
import groupEntity.Goods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/itempage")
public class ItempageController {

    @Autowired
    private  FreeMarkerConfigurer freeMarkerConfigurer;
    @Reference
    private FreeMarkerService freeMarkerService;
    @RequestMapping("/gen_item")
    public String gen_item(Long goodsId) throws Exception {

//        第一步：创建一个 Configuration 对象，直接 new 一个对象。构造方法的参数就是 freemarker 的版本号。
        Configuration configuration = freeMarkerConfigurer.getConfiguration();
//        第二步：设置模板文件所在的路径。
//        第三步：设置模板文件使用的字符集。一般就是 utf-8.
//        第四步：加载一个模板，创建一个模板对象。
        Template template = configuration.getTemplate("item.ftl");
//        第五步：创建一个模板使用的数据集，可以是 pojo 也可以是 map。一般是 Map。
        Goods goods = freeMarkerService.findOne(goodsId);
//        第六步：创建一个 Writer 对象，一般创建一 FileWriter 对象，指定生成的文件名。
        Map map = new HashMap();
        for(TbItem tbItem:goods.getItemList()){
            map.put("tbItem",tbItem);
            map.put("goods",goods);
            Writer writer = new FileWriter("D:\\class59\\html\\"+tbItem.getId()+".html");
//第七步：调用模板对象的 process 方法输出文件。
            template.process(map,writer);  //dataModel
//        第八步：关闭流
            writer.close();
        }
        return "success！";

    }


    @RequestMapping("/gen_itemAll")
    public String gen_itemAll() throws Exception {

//        第一步：创建一个 Configuration 对象，直接 new 一个对象。构造方法的参数就是 freemarker 的版本号。
        Configuration configuration = freeMarkerConfigurer.getConfiguration();
//        第二步：设置模板文件所在的路径。
//        第三步：设置模板文件使用的字符集。一般就是 utf-8.
//        第四步：加载一个模板，创建一个模板对象。
        Template template = configuration.getTemplate("item.ftl");
//        第五步：创建一个模板使用的数据集，可以是 pojo 也可以是 map。一般是 Map。
        List<Goods> goodsList = freeMarkerService.findAllGoods();
        for (Goods goods : goodsList) {
//        第六步：创建一个 Writer 对象，一般创建一 FileWriter 对象，指定生成的文件名。
            Map map = new HashMap();
            for(TbItem tbItem:goods.getItemList()){
                map.put("tbItem",tbItem);
                map.put("goods",goods);
                Writer writer = new FileWriter("D:\\class59\\html\\"+tbItem.getId()+".html");
//第七步：调用模板对象的 process 方法输出文件。
                template.process(map,writer);  //dataModel
//        第八步：关闭流
                writer.close();
            }
        }

        return "success！";

    }

}
