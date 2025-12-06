package com.uestc.sdcs.service;


import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class CacheService {
    // 无需持久化存储，故使用ConcurrentHashMap作为缓存
    private final ConcurrentHashMap<String,Object> cache = new ConcurrentHashMap<>();

    public void put(String key,Object value){
        cache.put(key,value);
    }

    public String get(String key){
        Object v = cache.get(key);
        return v == null ? null : v.toString();
    }

    public int delete(String key){
        // 为什么函数返回类型为int？可扩展如删除行数的内容
        return cache.remove(key) == null ? 0 : 1;
    }
}
