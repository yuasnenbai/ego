package com.ego.item.service;

import com.ego.item.mapper.SpecificationMapper;
import com.ego.item.pojo.Specification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SpecificationService {
    @Autowired
    SpecificationMapper specificationMapper;

    public Specification findByCid(Long cid) {
        return specificationMapper.selectByPrimaryKey(cid);
    }
}
