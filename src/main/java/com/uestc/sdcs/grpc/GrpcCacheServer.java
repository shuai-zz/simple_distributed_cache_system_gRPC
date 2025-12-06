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

    @Override
    public void put(Cache.PutRequest request, StreamObserver<Cache.PutResponse> responseObserver) {
        cacheService.put(request.getKey(), request.getValue());
        responseObserver.onNext(Cache.PutResponse.newBuilder().setOk(true).build());
        responseObserver.onCompleted();
    }

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

    @Override
    public void delete(Cache.DeleteRequest request, StreamObserver<Cache.DeleteResponse> responseObserver) {
        responseObserver.onNext(Cache.DeleteResponse.newBuilder()
                .setDeleted(cacheService.delete(request.getKey()))
                .build());
        responseObserver.onCompleted();
    }
}
