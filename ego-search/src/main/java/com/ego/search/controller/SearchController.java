package com.ego.search.controller;

import com.ego.common.pojo.PageResult;
import com.ego.search.pojo.Goods;
import com.ego.search.pojo.SearchRequest;
import com.ego.search.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class SearchController {
    @Autowired
    GoodsService goodsService;
    @RequestMapping("/page")
    public ResponseEntity<PageResult<Goods>> page(@RequestBody SearchRequest searchRequest){
        PageResult<Goods> result = goodsService.page(searchRequest);
        return ResponseEntity.ok(result);
    }
}
