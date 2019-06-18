package com.ego.item.api;

import com.ego.item.pojo.Category;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
@RequestMapping("/category")
public interface CategoryApi {
    @GetMapping("/list")
    public ResponseEntity<List<Category>> queryListByPid(@RequestParam(value = "pid", defaultValue = "0") Long pid);

    /**
     * 根据类别id列表查询出类别列表
     * @param idList
     * @return
     */
        @GetMapping("/ids/list")
        List<Category> queryListByIds(@RequestParam("ids") List<Long> idList);
}
