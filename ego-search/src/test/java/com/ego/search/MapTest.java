package com.ego.search;

import java.util.HashMap;
import java.util.Map;

public class MapTest {
    public static void main(String[] args) {
        Map<String,Integer> map=new HashMap<>();
        map.put("哈哈", 5);
        Integer aa = map.get("哈哈");
        System.out.println(aa);

    }
}
