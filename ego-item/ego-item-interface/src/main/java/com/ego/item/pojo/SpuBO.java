package com.ego.item.pojo;

import lombok.Data;

import java.util.List;

/**
 * 〈〉
 *
 * @author coach tam
 * @email 327395128@qq.com
 * @create 2019/4/8
 * @since 1.0.0
 * 〈坚持灵活 灵活坚持〉
 */
@Data
public class SpuBO extends Spu{
    private String categoryName;
    private String brandName;

    private List<Sku> skus;
    private SpuDetail spuDetail;
}
