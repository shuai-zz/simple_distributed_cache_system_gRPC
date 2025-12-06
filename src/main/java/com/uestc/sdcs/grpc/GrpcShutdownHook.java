package com.uestc.sdcs.grpc;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;

@Component
@RequiredArgsConstructor
public class GrpcShutdownHook {
    private final GrpcClientManager grpcClientManager;

    @PreDestroy
    public void onExit(){
        grpcClientManager.shutdownAll();
    }
}
