package com.ego.user.api;

import com.ego.user.pojo.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

public interface UserApi {
    @RequestMapping("/check/{data}/{type}")
    public ResponseEntity<Boolean> checkUserData(@PathVariable("data")String data, @PathVariable("type")Integer type);
    /**
     * 获取验证码
     * @param phone
     * @return
     */
    @PostMapping("/send")
    public ResponseEntity<Void> sendCode(@RequestParam("phone")String phone);

    /**
     * 注册
     * @param user
     * @param code
     * @return
     */

    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid User user, @RequestParam("code") String code);

    /**
     * 根据账号密码获取用户
     * @param username
     * @param password
     * @return
     */
    @GetMapping("/query")
    public ResponseEntity<User> query(@RequestParam("username") String username, @RequestParam("password") String password);
}
