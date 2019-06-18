package com.ego.item.service;

import com.ego.item.pojo.Category;

import java.util.List;

public interface CategoryService {
    /**
     * 根据父id查询类别列表
     * @param pid 父类别主键
     * @return
     */
    List<Category> queryListByPid(Long pid);
    /**
     * 根据类别id列表查询类别名字列表
     * @param cids
     * @return
     */
    List<String> queryListByCids(List<Long> cids);

    List<Category> queryListByList(List<Long> idList);

}
