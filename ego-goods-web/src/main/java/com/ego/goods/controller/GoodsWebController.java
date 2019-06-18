package com.ego.goods.controller;

import com.ego.goods.service.GoodsWebService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

/**
 * 〈〉
 *
 * @author coach tam
 * @email 327395128@qq.com
 * @create 2019/4/15
 * @since 1.0.0
 * 〈坚持灵活 灵活坚持〉
 */
@Controller
@RequestMapping("/item")
public class GoodsWebController {
    @Autowired
    private GoodsWebService goodsWebService;
    @Autowired
    private GoodsWebHtmlService goodsWebHtmlService;

    @RequestMapping("{id}.html")
    public String page(@PathVariable("id")Long id, Model model)
    {
        //准备模型数据
        Map<String, Object> map = goodsWebService.loadModel(id);
        //将模型数据传给模板
        model.addAllAttributes(map);
        //生成静态页面
        goodsWebHtmlService.generateStaticHtml(id,map);
        return "item";
    }

}
