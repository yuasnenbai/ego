package com.ego.search.listener;


import com.ego.search.service.GoodsService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 〈〉
 *
 * @author coach tam
 * @email 327395128@qq.com
 * @create 2019/4/17
 * @since 1.0.0
 * 〈坚持灵活 灵活坚持〉
 */
@Component
public class GoodsListener {
    @Autowired
    private GoodsService goodsService;

    /**
     * 处理insert和update的消息
     *
     * @param id
     * @throws Exception
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "ego.create.index.queue", durable = "true"),
            exchange = @Exchange(
                    value = "ego.item.exchange",
                    ignoreDeclarationExceptions = "true",
                    type = ExchangeTypes.TOPIC),
            key = {"item.insert", "item.update"}))
    public void listenCreate(Long id) throws Exception {
        System.out.println("1111111111111111111111");
        if (id == null) {
            return;
        }
//        int i = 1/0;
        // 创建或更新索引
        this.goodsService.createIndex(id);
    }
}
