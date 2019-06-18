package com.ego.item.controller;

import com.ego.item.pojo.Specification;
import com.ego.item.service.SpecificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 〈〉
 *
 * @author coach tam
 * @email 327395128@qq.com
 * @create 2019/4/2
 * @since 1.0.0
 * 〈坚持灵活 灵活坚持〉
 */
@Slf4j
@RestController
@RequestMapping("/spec")
public class SpecificationController {


    @Autowired
    private SpecificationService specificationService;

    @GetMapping("/{cid}")
    public ResponseEntity<String> queryByCid(@PathVariable(value = "cid") Long cid) {
        Specification specification =specificationService.findByCid(cid);
        return ResponseEntity.ok(specification.getSpecifications());
    }
}
