package com.ego.auth.service;

import com.ego.auth.client.UserClient;
import com.ego.auth.config.JwtProperties;
import com.ego.auth.entity.UserInfo;
import com.ego.auth.utils.JwtUtils;
import com.ego.user.pojo.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@EnableConfigurationProperties({JwtProperties.class})
public class AuthService {
    @Autowired
    UserClient userClient;
    @Autowired
    JwtProperties jwtProperties;

    public String login(String username, String password) throws Exception {
        String result=null;
        ResponseEntity<User> query = userClient.query(username, password);
        User user = query.getBody();
        //判断是否有效 获取token
        if (user != null) {
            try{
                UserInfo userInfo = new UserInfo(user.getId(), user.getUsername());
                result= JwtUtils.generateToken(userInfo, jwtProperties.getPrivateKey(), jwtProperties.getExpire());
            }
            catch (Exception e)
            {
                log.error("生成token异常",e);
                throw new RuntimeException("生成token异常");
            }
        }

        return  result;
    }


    public UserInfo verifyUser(String token) {

        try {
            UserInfo infoFromToken = JwtUtils.getInfoFromToken(token, jwtProperties.getPublicKey());
            return infoFromToken;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String refreshToken(UserInfo userInfo) {
        try {
            String s = JwtUtils.generateToken(userInfo,jwtProperties.getPrivateKey(), jwtProperties.getExpire());
            return s;
        } catch (Exception e) {
            e.printStackTrace();
        }
    return null;
    }
}

