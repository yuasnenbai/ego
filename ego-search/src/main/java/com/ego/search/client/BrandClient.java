package com.ego.search.client;

import com.ego.common.pojo.PageResult;
import com.ego.item.api.BrandApi;
import com.ego.item.pojo.Brand;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;

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
@FeignClient("item-service")
public interface BrandClient extends BrandApi {
    @Override
    default ResponseEntity<PageResult> page(Boolean descending, Integer page, Integer rowsPerPage, String sortBy, String search) {
        return null;
    }

    @Override
    default ResponseEntity<Void> save(Brand brand, String cids) {
        return null;
    }

    @Override
    default ResponseEntity<List<Brand>> queryByCid(Long cid) {
        return null;
    }

    @Override
    default List<Brand> queryListByIds(List<Long> idList) {
        return null;
    }
}
