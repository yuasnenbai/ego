package com.ego.search.pojo;

import com.ego.common.pojo.PageResult;
import com.ego.item.pojo.Brand;
import com.ego.item.pojo.Category;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 〈〉
 *
 * @author coach tam
 * @email 327395128@qq.com
 * @create 2019/4/14
 * @since 1.0.0
 * 〈坚持灵活 灵活坚持〉
 */
@Data
public class SearchResult extends PageResult<Goods> {
    //商品类别列表
    private List<Category> categoryList;
    //商品品牌列表
    private List<Brand> brandList;
    //其他规格 List<Map<k,options>>
    private List<Map<String,Object>> specs;

    public SearchResult() {
    }

    public SearchResult(List<Category> categoryList, List<Brand> brandList) {
        this.categoryList = categoryList;
        this.brandList = brandList;
    }

    public SearchResult(Long total, List<Goods> items, List<Category> categoryList, List<Brand> brandList) {
        super(total, items);
        this.categoryList = categoryList;
        this.brandList = brandList;
    }

    public SearchResult(Long total, Long totalPage, List<Goods> items, List<Category> categoryList, List<Brand> brandList) {
        super(total, totalPage, items);
        this.categoryList = categoryList;
        this.brandList = brandList;
    }

    public SearchResult(Long total, Long totalPage, List<Goods> items, List<Category> categoryList, List<Brand> brandList, List<Map<String, Object>> specs) {
        super(total, totalPage, items);
        this.categoryList = categoryList;
        this.brandList = brandList;
        this.specs = specs;
    }
}
