package com.ego.item.mapper;

import com.ego.item.pojo.Brand;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.additional.idlist.SelectByIdListMapper;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * 〈〉
 *
 * @author coach tam
 * @email 327395128@qq.com
 * @create 2019/4/4
 * @since 1.0.0
 * 〈坚持灵活 灵活坚持〉
 */
public interface BrandMapper extends Mapper<Brand>, SelectByIdListMapper<Brand,Long> {
    /**
     * 向tb_category_brand保存数据
     * @param bid
     * @param cid
     */
    @Insert("insert into tb_category_brand(category_id,brand_id) values(#{cid},#{bid})")
    void insertBrandCategory(@Param("bid") Long bid,@Param("cid") Long cid);

    @Select("select b.* from tb_category_brand cb  LEFT JOIN tb_brand b on cb.brand_id  = b.id where cb.category_id = #{cid}")
    List<Brand> findByCid(@Param("cid") Long cid);
}
