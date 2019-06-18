package com.ego.gateway.filter;

import com.ego.auth.utils.JwtUtils;
import com.ego.common.utils.CookieUtils;
import com.ego.gateway.config.FilterProperties;
import com.ego.gateway.config.JwtProperties;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
@EnableConfigurationProperties({JwtProperties.class, FilterProperties.class})
//@EnableConfigurationProperties(JwtProperties.class)
public class AuthFilter extends ZuulFilter {

    @Autowired
    JwtProperties jwtProperties;
    @Autowired
    FilterProperties filterProperties;

    @Override
    public String filterType() {
        //路由以前就过滤
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override//执行条件
    public boolean shouldFilter() {
        Boolean flag=true;
        //判断当前请求是否在白名单当中，如果是-->直接放行(返回false)
        //api/auth/login
        RequestContext currentContext = RequestContext.getCurrentContext();
        HttpServletRequest request = currentContext.getRequest();
        String requestURI = request.getRequestURI();
        //匹配  只要有一个匹配上了就返回true
//        boolean b = filterProperties.getAllowPaths().stream().allMatch(uri -> requestURI.startsWith(uri));
        for (String uri:filterProperties.getAllowPaths()) {
            if(requestURI.startsWith(uri)){
                flag=false;
                break;
            }
        }
        System.out.println(flag);
        return flag;
    }

    @Override
    public Object run() throws ZuulException {
        //获取cookie中的token，然后判断是否有效
        RequestContext currentContext = RequestContext.getCurrentContext();
        HttpServletRequest request = currentContext.getRequest();
        String cookieValue = CookieUtils.getCookieValue(request, jwtProperties.getCookieName());
        try {
            //判断token
            JwtUtils.getInfoFromToken(cookieValue, jwtProperties.getPublicKey());
        } catch (Exception e) {
            //说明token无效-->提示没有权限
            currentContext.setSendZuulResponse(false);
            currentContext.setResponseStatusCode(HttpStatus.FORBIDDEN.value());
        }
        return null;
    }
}
