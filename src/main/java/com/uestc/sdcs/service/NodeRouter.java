package com.uestc.sdcs.service;

import cache.Cache;
import com.uestc.sdcs.config.NodeConfig;
import com.uestc.sdcs.grpc.GrpcClientManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NodeRouter {

    private final NodeConfig nodeConfig;
    private final CacheService local;
    private final GrpcClientManager grpcClientManager;

    /**
     * hash算法，获取key的hash值，然后对节点数取模，得到key应该被存储的节点id
     * @param key 传入的key
     * @return 应该被存储的节点id
     */
    private int route(String key) {
        return Math.abs(key.hashCode()) % nodeConfig.getNodes().size();
    }

    /**
     * 读取缓存
     * @param key 传入的key
     * @return 缓存中存储的value
     */
    public String read(String key) {
        int target = route(key);
        if (target == nodeConfig.getNodeId()) {
            return local.get(key);
        } else {
            var stub = grpcClientManager.getStub(nodeConfig.getHost(target), nodeConfig.getGrpcPort(target));
            var resp = stub.get(Cache.GetRequest.newBuilder().setKey(key).build());
            return resp.getExists() ? resp.getValue() : null;
        }
    }

    /**
     * 删除缓存
     * @param key 删除的key
     * @return 0/1，表示删除成功/失败
     */
    public int delete(String key) {
        int target = route(key);
        if (target == nodeConfig.getNodeId()) {
            return local.delete(key);
        } else {
            var stub = grpcClientManager.getStub(nodeConfig.getHost(target), nodeConfig.getGrpcPort(target));
            return stub.delete(Cache.DeleteRequest.newBuilder().setKey(key).build()).getDeleted();
        }
    }

    /**
     * 写入缓存
     * @param key 传入的key
     * @param value 传入的value
     */
    public void write(String key, String value) {
        //获得key应该被存储的节点id
        int target = route(key);
        log.info("write: {} to {}", key, target);
        //当应被存储的节点id等于本节点id时，写入本节点缓存
        if (target == nodeConfig.getNodeId()) {
            local.put(key, value);
        } else {
            //否则，将数据写入对应节点的缓存
            var stub=grpcClientManager.getStub(nodeConfig.getHost(target), nodeConfig.getGrpcPort(target));
            stub.put(Cache.PutRequest.newBuilder().setKey(key).setValue(value).build());
        }
    }
}
