package com.ego.user.controller;

import com.ego.user.pojo.User;
import com.ego.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
public class UserController {
    @Autowired
    UserService userService;

    /**
     * 校验账号和手机号
     * @param data
     * @param type
     * @return
     */
    @RequestMapping("/check/{data}/{type}")
    public ResponseEntity<Boolean> checkUserData(@PathVariable("data")String data,@PathVariable("type")Integer type){

        Boolean b=userService.checkData(data, type);

        /*if(b==null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }*/
        return ResponseEntity.ok(b);
    }

    /**
     * 获取验证码
     * @param phone
     * @return
     */
    @PostMapping("/send")
    public ResponseEntity<Void> sendCode(@RequestParam("phone")String phone)
    {
        userService.sendCode(phone);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 注册
     * @param user
     * @param code
     * @return
     */

    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid User user, @RequestParam("code") String code)
    {
        userService.register(user,code);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 根据账号密码获取用户
     * @param username
     * @param password
     * @return
     */
    @GetMapping("/query")
    public ResponseEntity<User> query(@RequestParam("username") String username, @RequestParam("password") String password) {

        User user = userService.findByUP(username, password);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(user);
    }
}
