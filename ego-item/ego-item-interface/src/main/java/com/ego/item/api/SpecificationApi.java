package com.ego.item.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 〈〉
 *
 * @author coach tam
 * @email 327395128@qq.com
 * @create 2019/4/11
 * @since 1.0.0
 * 〈坚持灵活 灵活坚持〉
 */
@RequestMapping("/spec")
public interface SpecificationApi {
    @GetMapping("/{cid}")
    public ResponseEntity<String> queryByCid(@PathVariable(value = "cid") Long cid);

}
