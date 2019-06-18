package com.ego.item.api;

import com.ego.common.pojo.CartDto;
import com.ego.common.pojo.PageResult;
import com.ego.item.pojo.Sku;
import com.ego.item.pojo.SpuBO;
import com.ego.item.pojo.SpuDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 〈〉
 *
 * @author coach tam
 * @email 327395128@qq.com
 * @create 2019/4/11
 * @since 1.0.0
 * 〈坚持灵活 灵活坚持〉
 */
@RequestMapping("/goods")
public interface GoodsApi {
    @GetMapping("/spu/page")
    public ResponseEntity<PageResult<SpuBO>> page(@RequestParam("key") String key,
                                           @RequestParam("saleable") Integer saleable,
                                           @RequestParam(value = "page", defaultValue = "1") Integer page,
                                           @RequestParam(value = "rows", defaultValue = "5") Integer rows);


    @PostMapping
    public ResponseEntity<Void> save(@RequestBody SpuBO spuBO);

    @GetMapping("/spu/detail/{spuId}")
    public ResponseEntity<SpuDetail> querySpuDetailBySpuId(@PathVariable("spuId") Long spuId);
    @PutMapping
    public ResponseEntity<Void> update(@RequestBody SpuBO spuBO);
    @GetMapping("/sku/list")
    public ResponseEntity<List<Sku>> querySkuBySpuId(@RequestParam("id") Long spuId);
    @GetMapping("/spu/{id}")
    SpuBO queryGoodsById(@PathVariable("id") Long spuId);
    @GetMapping("/sku/{id}")
    Sku querySkuById(@PathVariable("id")String skuId);

    /**
     * 减库存
     * @param cartDTOS
     */
    @PostMapping("stock/decrease")
    void decreaseStock(@RequestBody List<CartDto> cartDTOS);
}
