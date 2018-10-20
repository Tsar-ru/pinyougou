package cn.itcast;

import com.pinyougou.pojo.TbItem;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.SolrDataQuery;
import org.springframework.data.solr.core.query.result.ScoredPage;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/applicationContext-solr.xml")
public class SolrTest {

    @Autowired
    private SolrTemplate solrTemplate;
    @Test
    public void testAdd(){
        TbItem tbItem = new TbItem();
        tbItem.setId(1000L);
        tbItem.setTitle("我自己的测试数据");
        tbItem.setBrand("自主品牌");
        solrTemplate.saveBean(tbItem);
        solrTemplate.commit();
    }

    @Test
    public void testUpate(){
        TbItem tbItem = new TbItem();
        tbItem.setId(1000L);
        tbItem.setTitle("我自己的测试数据12121212");
        tbItem.setBrand("自主品牌1212121221");
        solrTemplate.saveBean(tbItem);
        solrTemplate.commit();
    }

    @Test
    public void testQuery(){
        Query query = new SimpleQuery("item_title:数据");
        ScoredPage<TbItem> scoredPage = solrTemplate.queryForPage(query, TbItem.class);
        List<TbItem> content = scoredPage.getContent(); //当前页的数据   默认是第一页 每页显示10条
        for (TbItem tbItem : content) {
            System.out.println(tbItem.getTitle());
        }
    }
    @Test
    public void testDele(){
//        solrTemplate.deleteById("1");
        SolrDataQuery query = new SimpleQuery("*:*");
        solrTemplate.delete(query);
        solrTemplate.commit();
    }

}
