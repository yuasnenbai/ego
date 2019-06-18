package com.ego.cart.config;

import com.ego.cart.interceptor.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 〈〉
 *
 * @author coach tam
 * @email 327395128@qq.com
 * @create 2019/4/21
 * @since 1.0.0
 * 〈坚持灵活 灵活坚持〉*/


@EnableConfigurationProperties(JwtProperties.class)
@Configuration
public class MvcConfig implements WebMvcConfigurer {
    @Autowired
    private JwtProperties jwtProperties;
    @Bean
    public LoginInterceptor authInterceptor(){
        return new LoginInterceptor(jwtProperties);
    }
    @Override
        public void addInterceptors(InterceptorRegistry registry) {
        //拦截器的范围
        registry.addInterceptor(authInterceptor()).addPathPatterns("/**");
    }
}
