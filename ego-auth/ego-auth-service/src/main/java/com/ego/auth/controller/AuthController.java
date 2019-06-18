package com.ego.auth.controller;

import com.ego.auth.entity.UserInfo;
import com.ego.auth.service.AuthService;
import com.ego.common.utils.CookieUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
public class AuthController {
    @Autowired
    AuthService authService;

    @Value(value = "${ego.jwt.cookieName}")
    private String cookieName;
    @Value(value = "${ego.jwt.cookieMaxage}")
    private Integer cookieMaxage;

    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestParam("username")String username,
                                      @RequestParam("password")String password,
                                      HttpServletRequest httpServletRequest,
                                      HttpServletResponse httpServletResponse){
        try{
            //生成token
            String token = authService.login(username, password);
            //将token写入cookie
            CookieUtils.setCookie(httpServletRequest,httpServletResponse,cookieName,token,cookieMaxage);
         }
        catch (Exception e)
        {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/verify")
    public ResponseEntity<UserInfo> verifyUser(@CookieValue("EGO_TOKEN")String token,
                                               HttpServletRequest httpServletRequest,
                                               HttpServletResponse httpServletResponse){
        UserInfo userInfo=authService.verifyUser(token);
        if (userInfo == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        //刷新token
        String token1=authService.refreshToken(userInfo);
        //写入cookie
        CookieUtils.setCookie(httpServletRequest,httpServletResponse,cookieName,token1);
        return ResponseEntity.ok(userInfo);
    }

}
