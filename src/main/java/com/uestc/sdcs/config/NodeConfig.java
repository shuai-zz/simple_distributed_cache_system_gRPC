package com.uestc.sdcs.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@Getter
public class NodeConfig {
    @Value("${node.id}")
    private int nodeId;

    @Value("${cluster.nodes}")
    private List<String> nodes;

    @Value("${cluster.grpcPorts}")
    private List<Integer> grpcPorts;

    public String getHost(int id) {
        return nodes.get(id);
    }

    public int getGrpcPort(int id) {
        return grpcPorts.get(id);
    }
}
