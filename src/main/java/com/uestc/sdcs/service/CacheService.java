package com.uestc.sdcs.service;


import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class CacheService {
    private final ConcurrentHashMap<String,Object> cache = new ConcurrentHashMap<>();

    public void put(String key,Object value){
        cache.put(key,value);
    }

    public String get(String key){
        Object v = cache.get(key);
        return v == null ? null : v.toString();
    }

    public int delete(String key){
        return cache.remove(key) == null ? 0 : 1;
    }
}
