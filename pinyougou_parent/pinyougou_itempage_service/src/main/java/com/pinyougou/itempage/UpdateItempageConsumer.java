package com.pinyougou.itempage;

import com.pinyougou.itempage.service.FreeMarkerService;
import com.pinyougou.pojo.TbItem;
import freemarker.template.Configuration;
import freemarker.template.Template;
import groupEntity.Goods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

public class UpdateItempageConsumer implements MessageListener {

    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;

    @Autowired
    private FreeMarkerService freeMarkerService;
    @Override
    public void onMessage(Message message) {
        TextMessage textMessage = (TextMessage) message;
        try {
            String goodsId = textMessage.getText();
//            要为此spu下的所有spu生成静态页面
            //        第一步：创建一个 Configuration 对象，直接 new 一个对象。构造方法的参数就是 freemarker 的版本号。
            Configuration configuration = freeMarkerConfigurer.getConfiguration();
//        第二步：设置模板文件所在的路径。
//        第三步：设置模板文件使用的字符集。一般就是 utf-8.
//        第四步：加载一个模板，创建一个模板对象。
            Template template = configuration.getTemplate("item.ftl");
//        第五步：创建一个模板使用的数据集，可以是 pojo 也可以是 map。一般是 Map。
            Goods goods = freeMarkerService.findOne(Long.parseLong(goodsId));
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
            System.out.println("item update success!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
