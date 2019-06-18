package com.ego.item.controller;

import com.ego.common.pojo.CartDto;
import com.ego.common.pojo.PageResult;
import com.ego.item.pojo.Sku;
import com.ego.item.pojo.SpuBO;
import com.ego.item.pojo.SpuDetail;
import com.ego.item.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/goods")
public class GoodsController {
    @Autowired
    GoodsService goodsService;

    /**
     * 分页
     * @param key
     * @param saleable
     * @param page
     * @param rows
     * @return
     */
    @GetMapping("/spu/page")
    //    key=x&saleable=1&page=1&rows=5
    public ResponseEntity<PageResult>page(@RequestParam("key")String key,
                                          @RequestParam("saleable")Integer saleable,
                                          @RequestParam("page")Integer page,
                                          @RequestParam("rows")Integer rows
                                          ){
        PageResult result=goodsService.page(key,saleable,page,rows);
        return ResponseEntity.ok(result);
    }

    /**
     * 新增
     * @param spuBO
     * @return
     */
    @PostMapping
    public ResponseEntity<Void> save(@RequestBody SpuBO spuBO){
        goodsService.save(spuBO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 查询规则
     * @param supId
     * @return
     */
    @GetMapping("spu/detail/{id}")
    public ResponseEntity<SpuDetail> queryDetailById(@PathVariable("id")Long supId){

        return ResponseEntity.ok(goodsService.findSpuDetailBySpuId(supId));
    }

    /**
     * 查询sku
     * @param id
     * @return
     */
    @GetMapping("sku/list")
    public ResponseEntity<List<Sku>> querySkuById(@RequestParam("id")Long id){
        return ResponseEntity.ok(goodsService.findSkuById(id));
    }

    /**
     * 更新
     * @param spuBO
     * @return
     */
    @PutMapping
    public ResponseEntity<Void> update(@RequestBody SpuBO spuBO) {
        goodsService.update(spuBO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    @GetMapping("/spu/{id}")
    public SpuBO queryGoodsById(@PathVariable("id") Long spuId){
        return goodsService.queryGoodsById(spuId);
    }

    @GetMapping("/sku/{id}")
    Sku querySkuById(@PathVariable("id") String skuId){
        return goodsService.querySkuById(skuId);
    }

    /**
     * 减库存
     * @param cartDtos
     * @return
     */
    @PostMapping("stock/decrease")
    public ResponseEntity<Void> decreaseStock(@RequestBody List<CartDto> cartDtos){
        goodsService.decreaseStock(cartDtos);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
