package com.uestc.sdcs.grpc;

import cache.CacheServiceGrpc;
import com.uestc.sdcs.service.CacheService;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class GrpcClientManager {

    // 缓存所有节点的Channel（单例模式）
    private final Map<String,ManagedChannel> channelMap=new ConcurrentHashMap<>();
    // 缓存所有节点的stub（可复用）
    private final Map<String, CacheServiceGrpc.CacheServiceBlockingStub> stubMap=new ConcurrentHashMap<>();

    public CacheServiceGrpc.CacheServiceBlockingStub getStub(String host, int port){
        String key = host + ":" + port;

        // 若已有，就复用
        return stubMap.computeIfAbsent(key, k -> {
            ManagedChannel channel=ManagedChannelBuilder
                    .forAddress(host, port)
                    .usePlaintext()
                    .build();
            channelMap.put(key, channel);
            return CacheServiceGrpc.newBlockingStub(channel);
        });
    }

    // 程序关闭时释放连接
    public void shutdownAll() {
        channelMap.values().forEach(ManagedChannel::shutdown);
    }


}
