package com.uestc.sdcs.grpc;

import cache.CacheServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.stereotype.Component;

@Component
public class GrpcClientManager {

    public CacheServiceGrpc.CacheServiceBlockingStub connect(String host, int port){
        ManagedChannel channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();
        return CacheServiceGrpc.newBlockingStub(channel);
    }
}
