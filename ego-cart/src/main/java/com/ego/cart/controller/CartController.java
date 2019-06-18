package com.ego.cart.controller;

import com.ego.cart.pojo.Cart;
import com.ego.cart.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CartController {
    @Autowired
    CartService cartService;

    /**
     * 添加购物项到redis  （购物车）
     *
     * @param cart
     * @return*/



    @PostMapping
    public ResponseEntity<Void> addCart(@RequestBody Cart cart) {
        cartService.addCart(cart);
        return ResponseEntity.ok().build();

    }
    /*@PostMapping
    public ResponseEntity<Void> jiaCart(@RequestParam("skuId") Long skuId,
                                        @RequestParam("num") Integer num,
                                        @RequestParam("local") List local) {
        cartService.jiaCart(skuId,num,local);
        return ResponseEntity.ok().build();

    }*/

    @GetMapping
    public ResponseEntity<List<Cart>> queryCartList(){
        try
        {
            List<Cart> result = cartService.queryCartList();
            return ResponseEntity.ok(result);
        }
        catch (Exception e)
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @PutMapping
    public ResponseEntity<Void> updateNum(@RequestBody Cart cart){
        cartService.updateNum(cart);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{skuId}")
    public ResponseEntity<Void> delSkuId(@PathVariable("skuId") String skuId) {
        cartService.delSkuId(skuId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

}



