package com.ego.goods.controller;


import com.ego.goods.utils.ThreadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
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
@Service
public class GoodsWebHtmlService {

    @Autowired
    private TemplateEngine templateEngine;

    public void generateStaticHtml(Long id, Map<String, Object> map) {

       ThreadUtils.asycnExecute(()->{
           Context context = new Context();
           context.setVariables(map);
           try {
               PrintWriter printWriter = new PrintWriter(new File("E:\\workspace\\html\\item\\" + id + ".html"));
               templateEngine.process("item", context, printWriter);
               printWriter.close();
           } catch (FileNotFoundException e) {
               e.printStackTrace();
           }
       });

    }
}
