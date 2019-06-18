package com.ego.goods.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 〈〉
 *
 * @author coach tam
 * @email 327395128@qq.com
 * @create 2019/4/15
 * @since 1.0.0
 * 〈坚持灵活 灵活坚持〉
 */
public class ThreadUtils {
    private static ExecutorService executorService = Executors.newFixedThreadPool(10);

    public static void asycnExecute(Runnable runnable) {
        executorService.submit(runnable);
    }
}
