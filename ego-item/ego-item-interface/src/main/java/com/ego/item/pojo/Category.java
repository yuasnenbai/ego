package com.ego.item.pojo;

import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.*;

/**
 * 〈〉
 *
 * @author coach tam
 * @email 327395128@qq.com
 * @create 2019/4/2
 * @since 1.0.0
 * 〈坚持灵活 灵活坚持〉
 */
@Table(name = "tb_category")
@Data
public class Category {
    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @KeySql(useGeneratedKeys = true)
    private Long id;
    private String name;
    @Column(name = "parent_id")
    private Long parentId;
    private Boolean isParent; // 注意isParent生成的getter和setter方法需要手动加上Is
    private Integer sort;
}
