package com.pinyougou.search.service.impl;

import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import javax.xml.soap.Text;
import java.util.List;

public class UpdateSolrConsumer implements MessageListener{

    @Autowired
    private SolrTemplate solrTemplate;
    @Autowired
    private TbItemMapper itemMapper;
    @Override
    public void onMessage(Message message) {
        TextMessage textMessage = (TextMessage) message;
            try {
                String goodsId = textMessage.getText();
                TbItemExample example = new TbItemExample();
            example.createCriteria().andGoodsIdEqualTo(Long.parseLong(goodsId));
            List<TbItem> itemList = itemMapper.selectByExample(example);
//            同步solr索引库
            solrTemplate.saveBeans(itemList);
            solrTemplate.commit();
            System.out.println("solr update success!!!");
        } catch (JMSException e) {
            e.printStackTrace();
        }


    }
}
