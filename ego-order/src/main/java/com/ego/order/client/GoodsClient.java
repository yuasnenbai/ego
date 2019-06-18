package com.ego.order.client;

import com.ego.common.pojo.CartDto;
import com.ego.common.pojo.PageResult;
import com.ego.item.api.GoodsApi;
import com.ego.item.pojo.Sku;
import com.ego.item.pojo.SpuBO;
import com.ego.item.pojo.SpuDetail;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * @author yaorange
 * @date 2019/03/01
 */
@FeignClient(value = "item-service")
public interface GoodsClient extends GoodsApi {

    @Override
    default ResponseEntity<PageResult<SpuBO>> page(String key, Integer saleable, Integer page, Integer rows) {
        return null;
    }

    @Override
    default ResponseEntity<Void> save(SpuBO spuBO) {
        return null;
    }

    @Override
    default ResponseEntity<SpuDetail> querySpuDetailBySpuId(Long spuId) {
        return null;
    }

    @Override
    default ResponseEntity<Void> update(SpuBO spuBO) {
        return null;
    }

    @Override
    default ResponseEntity<List<Sku>> querySkuBySpuId(Long spuId) {
        return null;
    }

    @Override
    default SpuBO queryGoodsById(Long spuId) {
        return null;
    }

    @Override
    default Sku querySkuById(String skuId) {
        return null;
    }

    @Override
    default void decreaseStock(List<CartDto> cartDTOS) {

    }
}
