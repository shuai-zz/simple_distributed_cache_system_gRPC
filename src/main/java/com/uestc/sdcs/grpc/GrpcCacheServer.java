package com.uestc.sdcs.grpc;


import cache.Cache;
import cache.CacheServiceGrpc;
import com.uestc.sdcs.service.CacheService;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
@RequiredArgsConstructor
public class GrpcCacheServer extends CacheServiceGrpc.CacheServiceImplBase {

    private final CacheService cacheService;


    /**
     * 将通过gRPC接收到的kv存入缓存中
     * 
     * @param request gRPC请求对象
     * @param responseObserver gRPC的返回内容（用于发送响应的流观察者，包含值ok=true）
     */
    @Override
    public void put(Cache.PutRequest request, StreamObserver<Cache.PutResponse> responseObserver) {
        cacheService.put(request.getKey(), request.getValue());
        responseObserver.onNext(Cache.PutResponse.newBuilder().setOk(true).build());
        responseObserver.onCompleted();
    }

    /**
     * 获取缓存中的值
     * @param request gRPC请求对象
     * @param responseObserver gRPC的返回内容（用于发送响应的流观察者，包含值value和exists）
     */
    @Override
    public void get(Cache.GetRequest request, StreamObserver<Cache.GetResponse> responseObserver) {
        String v = cacheService.get(request.getKey());
        if (v == null) {
            responseObserver.onNext(Cache.GetResponse.newBuilder().setExists(false).build());
        } else {
            responseObserver.onNext(Cache.GetResponse.newBuilder().setExists(true).setValue(v).build());
        }
        responseObserver.onCompleted();
    }

    /**
     * 删除缓存中的值
     * @param request gRPC请求对象
     * @param responseObserver gRPC的返回内容（用于发送响应的流观察者，包含值deleted）
     */
    @Override
    public void delete(Cache.DeleteRequest request, StreamObserver<Cache.DeleteResponse> responseObserver) {
        responseObserver.onNext(Cache.DeleteResponse.newBuilder()
                .setDeleted(cacheService.delete(request.getKey()))
                .build());
        responseObserver.onCompleted();
    }
}
