package com.uestc.sdcs.service;

import cache.Cache;
import com.uestc.sdcs.config.NodeConfig;
import com.uestc.sdcs.grpc.GrpcClientManager;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NodeRouter {

    private final NodeConfig nodeConfig;
    private final CacheService local;
    private final GrpcClientManager grpcClientManager;

    private int route(String key){
        return Math.abs(key.hashCode())%nodeConfig.getNodes().size();
    }
    public String read(String key) {
        int target=route(key);
        if(target== nodeConfig.getNodeId()){
            return local.get(key);
        }else{
            var stub=grpcClientManager.connect(nodeConfig.getHost(target), nodeConfig.getGrpcPort(target));
            var resp=stub.get(Cache.GetRequest.newBuilder().setKey(key).build());
            return resp.getExists() ? resp.getValue() : null;
        }
    }

    public @Nullable Object delete(String key) {
        int target=route(key);
        if(target== nodeConfig.getNodeId()){
            return local.delete(key);
        }else{
            var stub=grpcClientManager.connect(nodeConfig.getHost(target), nodeConfig.getGrpcPort(target));
            return stub.delete(Cache.DeleteRequest.newBuilder().setKey(key).build()).getDeleted();
        }
    }

    public void write(String key, String value) {
        int target=route(key);
        if(target== nodeConfig.getNodeId()){
            local.put(key,value);
        }else{
            var stub=grpcClientManager.connect(nodeConfig.getHost(target), nodeConfig.getGrpcPort(target));
            stub.put(Cache.PutRequest.newBuilder().setKey(key).setValue(value).build());
        }
    }
}
