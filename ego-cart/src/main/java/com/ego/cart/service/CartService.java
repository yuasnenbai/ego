package com.ego.cart.service;

import com.ego.auth.entity.UserInfo;
import com.ego.cart.client.GoodsClient;
import com.ego.cart.interceptor.LoginInterceptor;
import com.ego.cart.pojo.Cart;
import com.ego.common.utils.JsonUtils;
import com.ego.item.pojo.Sku;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartService {
    @Autowired
    GoodsClient goodsClient;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    static final String KEY_PREFIX = "ego:cart:uid:";

    static final Logger logger = LoggerFactory.getLogger(CartService.class);

    public void addCart(Cart cart) {
        //获取登陆的用户名
        UserInfo user = LoginInterceptor.getUser();

        //hash操作对象
        BoundHashOperations<String, Object, Object> carts = stringRedisTemplate.boundHashOps(KEY_PREFIX + user.getId());

        //查询是否存在购物项
        String  skuId = cart.getSkuId().toString();
        Integer num = cart.getNum();

        if (carts.hasKey(skuId) ) {
            //存在，获取购物车信息
            //修改购物车数量
            String s = carts.get(skuId).toString();
            cart = JsonUtils.parse(s, Cart.class);
            cart.setNum(cart.getNum() + num);
        } else{
            //不存在，新增购物车数据
            //其他信息，需要查询商品服务
            cart    = new Cart();
            cart.setNum(num);
            //根据skuId查询参数fegin
            Sku sku = goodsClient.querySkuById(skuId);
            cart.setImage(sku.getImages());
            cart.setOwnSpec(sku.getOwnSpec());
            cart.setPrice(sku.getPrice());
            cart.setTitle(sku.getTitle());
            cart.setSkuId(sku.getId());
            cart.setUserId(user.getId());
        }
        //存字符串。存入Redis
        carts.put(skuId, JsonUtils.serialize(cart));

        //本地存储
       /* List<Cart> local = cart.getLocal();
            for (int i=0;i<=local.size();i++) {
            Cart c = (Cart) local.get(i);
            c.setPrice(null);
            cart.setImage(null);
            cart.setOwnSpec(null);
            cart.setPrice(null);
            cart.setTitle(null);
            cart.setSkuId(null);
            cart.setUserId(null);
            if(carts.hasKey(c.getSkuId())){
            //本地缓存
            String x = carts.get(c.getSkuId()).toString();
            cart = JsonUtils.parse(x, Cart.class);
            cart.setNum(cart.getNum() + num);
            }else {
                //不存在，新增购物车数据
                //其他信息，需要查询商品服务
                cart    = new Cart();
                cart.setNum(num);
                //根据skuId查询参数fegin
                Sku sku = goodsClient.querySkuById(c.getSkuId().toString());
                cart.setImage(sku.getImages());
                cart.setOwnSpec(sku.getOwnSpec());
                cart.setPrice(sku.getPrice());
                cart.setTitle(sku.getTitle());
                cart.setSkuId(sku.getId());
                cart.setUserId(user.getId());
            }
            //存字符串。存入Redis
            carts.put(skuId, JsonUtils.serialize(cart));
        }*/



    }

    public List<Cart> queryCartList() {
        //获取用户信息
        UserInfo user = LoginInterceptor.getUser();
        //获取redis对象
        BoundHashOperations<String, Object, Object> carts = stringRedisTemplate.boundHashOps(KEY_PREFIX + user.getId());
        //获取到redis的value遍历成string封装在Cart中转换为List
        return carts.values().stream().map(json -> JsonUtils.parse(json.toString(), Cart.class)).collect(Collectors.toList());
    }

    public void updateNum(Cart cart) {
        //获取用户信息
        UserInfo user = LoginInterceptor.getUser();
        //获取redis对象
        BoundHashOperations<String, Object, Object> carts = stringRedisTemplate.boundHashOps(KEY_PREFIX + user.getId());
        Integer num = cart.getNum();
        //根据skuid找到对应的商品
        String  json = (String)carts.get(cart.getSkuId().toString());
        //转对象
        Cart parse = JsonUtils.parse(json, Cart.class);
        parse.setNum(num);
        //写入redis
        carts.put(cart.getSkuId().toString(),JsonUtils.serialize(cart));

    }

    public void delSkuId(String skuId) {
        //获取用户信息
        UserInfo user = LoginInterceptor.getUser();
        //获取redis对象
        BoundHashOperations<String, Object, Object> carts = stringRedisTemplate.boundHashOps(KEY_PREFIX + user.getId());
        carts.delete(skuId);

    }

    public void jiaCart(Long skuId, Integer num, List local) {

    }
}



