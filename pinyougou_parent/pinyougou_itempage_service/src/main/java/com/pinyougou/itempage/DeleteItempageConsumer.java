package com.pinyougou.itempage;

import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import javax.xml.soap.Text;
import java.io.File;
import java.util.List;

public class DeleteItempageConsumer implements MessageListener {

    @Autowired
    private TbItemMapper itemMapper;

    @Override
    public void onMessage(Message message) {
        TextMessage textMessage = (TextMessage) message;
        try {
            String goodsId = textMessage.getText();
            TbItemExample example = new TbItemExample();
            example.createCriteria().andGoodsIdEqualTo(Long.parseLong(goodsId));
            List<TbItem> itemList = itemMapper.selectByExample(example);//根据spuID查询sku的列表
            for (TbItem tbItem : itemList) {
                //            删除静态页面
                new File("D:\\class59\\html\\"+tbItem.getId()+".html").delete();
            }
            System.out.println("delete itempage success!");
        } catch (JMSException e) {
            e.printStackTrace();
        }


    }
}
