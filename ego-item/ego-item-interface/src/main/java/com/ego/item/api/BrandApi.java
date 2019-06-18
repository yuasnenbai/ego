package com.ego.item.api;

import com.ego.common.pojo.PageResult;
import com.ego.item.pojo.Brand;
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
@RequestMapping("/brand")
public interface BrandApi {
    @GetMapping("/page")
    public ResponseEntity<PageResult> page(@RequestParam("descending") Boolean descending,
                                           @RequestParam(value = "page", defaultValue = "1") Integer page,
                                           @RequestParam(value = "rowsPerPage", defaultValue = "5") Integer rowsPerPage,
                                           @RequestParam("sortBy") String sortBy,
                                           @RequestParam("search") String search
    );

    @PostMapping
    public ResponseEntity<Void> save(Brand brand, @RequestParam("cids") String cids);

    @GetMapping("/cid/{cid}")
    public ResponseEntity<List<Brand>> queryByCid(@PathVariable("cid") Long cid);

    @GetMapping("/ids/list")
    List<Brand> queryListByIds(@RequestParam("ids") List<Long> idList);
}
