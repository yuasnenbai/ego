package com.ego.item.controller;

import com.ego.item.pojo.Category;
import com.ego.item.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
@RequestMapping("/category")
public class CategoryController {


    @Autowired
    private CategoryService categoryService;

    @GetMapping("/list")
    public ResponseEntity<List<Category>> queryListByPid(@RequestParam(value = "pid",defaultValue = "0") Long pid) {
        try{
            if(pid==null||pid<0)
            {
                return  ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }

            //查询数据
            List<Category> result = categoryService.queryListByPid(pid);

            if(result==null||result.size()==0)
            {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            return ResponseEntity.ok(result);
        }catch (Exception e){
            log.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 根据类别id列表查询出类别列表
     * @param idList
     * @return
     */
    @GetMapping("/ids/list")
    List<Category> queryListByIds(@RequestParam("ids") List<Long> idList){
        return categoryService.queryListByList(idList);
    }

}
