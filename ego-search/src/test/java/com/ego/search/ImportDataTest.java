package com.ego.search;

import com.ego.common.pojo.PageResult;
import com.ego.item.pojo.SpuBO;
import com.ego.search.client.GoodsClient;
import com.ego.search.pojo.Goods;
import com.ego.search.repository.GoodsRepository;
import com.ego.search.service.GoodsService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * 〈〉
 *
 * @author coach tam
 * @email 327395128@qq.com
 * @create 2019/4/11
 * @since 1.0.0
 * 〈坚持灵活 灵活坚持〉
 */
@SpringBootTest
//运行器    表明这是一个测试类
@RunWith(SpringRunner.class)
public class ImportDataTest {

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private GoodsRepository goodsRepository;
    @Test
    public void test(){
        int size = 0;
        int page = 1;
        int rows = 100;

        //通过商品微服务查询到所有的商品
        do{
            PageResult<SpuBO> pageResult = goodsClient.page("", 1, page, rows).getBody();

            size = pageResult.getItems().size();
            page ++;
            List<Goods> goodsList = new ArrayList<>();
            //将所有的商品(Spu)-->Goods
            pageResult.getItems().forEach(spu->{
                try{
                    Goods goods = goodsService.buildGoods(spu);
                    goodsList.add(goods);
                }
                catch (Exception e)
                {
                }
            });
            //将所有goods保存到es
            goodsRepository.saveAll(goodsList);
        }while (size==100);

    }
}
