package com.ego.user.service;

import com.ego.common.utils.CodecUtils;
import com.ego.user.mapper.UserMapper;
import com.ego.user.pojo.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class UserService {
    @Autowired
    UserMapper userMapper;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private AmqpTemplate amqpTemplate;

    private static  final String codeKey = "ego:sms:code:";

    public Boolean checkData(String data, Integer type) {
        User user = new User();
        if(type==1){
            user.setUsername(data);
        }else if(type==2){
            user.setPhone(data);
        }else {
            throw new RuntimeException("类型不匹配");
        }
        //0为true
        return userMapper.selectCount(user)==0;
    }


    /**
     * 生成随机数
     * @param len
     * @return
     */
    public static String generateCode(int len){
        len = Math.min(len, 8);
        int min = Double.valueOf(Math.pow(10, len - 1)).intValue();
        int num = new Random().nextInt(
                Double.valueOf(Math.pow(10, len + 1)).intValue() - 1) + min;
        return String.valueOf(num).substring(0,len);
    }

    public void sendCode(String phone) {
        //发送验证码（生成随机数）
        String code = generateCode(6);
        //将验证码存储到redis(5分钟有效)  key:ego.sms.code.phone  value:code
        ValueOperations<String, String> stringValueOperations = stringRedisTemplate.opsForValue();
        stringValueOperations.set(codeKey + phone, code,5, TimeUnit.MINUTES);

        //通过mq发送消息
        Map<String, String> map = new HashMap<>();
        map.put("phone", phone);
        map.put("code", code);
        //交换机     通配符
        amqpTemplate.convertAndSend("ego.exchange.sms","sms.send",map);
    }

    public void register(User user, String code) {
        //判断验证码
        //从redis获取
        String redisCode = stringRedisTemplate.opsForValue().get(codeKey + user.getPhone());
        if (StringUtils.isBlank(redisCode)) {
            throw new RuntimeException("验证码无效1");
        }
        if (redisCode.equals(code)) {
            //加密密码
            String password = CodecUtils.passwordBcryptEncode(user.getUsername(), user.getPassword());
            user.setPassword(password);
            user.setCreated(new Date());
            //保存信息到数据库
            userMapper.insertSelective(user);
            stringRedisTemplate.delete(codeKey + user.getPhone());
        }else
        {
            throw new RuntimeException("验证码无效2");
        }

    }

    public User findByUP(String username, String password) {

        User user = new User();
        user.setUsername(username);
        User user1 = userMapper.selectOne(user);

        Boolean aBoolean = CodecUtils.passwordConfirm(username + password, user1.getPassword());
        System.out.println(aBoolean);
        /*if (aBoolean) {
            return user1;
        }
        return null;*/
        if(!aBoolean)
        {
            return null;
        }
        return  user1;
    }
}
