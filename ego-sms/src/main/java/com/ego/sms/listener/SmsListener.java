package com.ego.sms.listener;

import com.aliyuncs.exceptions.ClientException;
import com.ego.sms.utils.SmsUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 〈〉
 *
 * @author coach tam
 * @email 327395128@qq.com
 * @create 2019/4/18
 * @since 1.0.0
 * 〈坚持灵活 灵活坚持〉
 */
@Component
public class SmsListener {

    @Autowired
    private SmsUtils smsUtils;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "ego.sms.send.queue",durable = "true",ignoreDeclarationExceptions = "true"),
            exchange = @Exchange(value = "ego.exchange.sms",type = "topic",durable = "true",ignoreDeclarationExceptions = "true"),
            key = "sms.send"
    ))
    public void sendSms(Map<String,String> map) throws ClientException {
        String phone = map.get("phone");
        String code = map.get("code");

        if(StringUtils.isNotBlank(phone)&&StringUtils.isNotBlank(code))
        {
            smsUtils.sendSms(phone, code);
        }
        else
        {
            return ;
        }
    }
}
