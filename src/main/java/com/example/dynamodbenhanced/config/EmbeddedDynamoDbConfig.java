package com.example.dynamodbenhanced.config;

import com.amazonaws.services.dynamodbv2.local.main.ServerRunner;
import com.amazonaws.services.dynamodbv2.local.server.DynamoDBProxyServer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Slf4j
@Configuration
@RequiredArgsConstructor
@ConditionalOnClass(name = "com.amazonaws.services.dynamodbv2.local.server.DynamoDBProxyServer")
public class EmbeddedDynamoDbConfig {
    private DynamoDBProxyServer server;
    private static final int RETRY_CNT = 30;
    private int port = 8000;

    @PostConstruct
    public void startDynamoDB() {
        if (server != null) {
            return;
        }
        AwsDynamoDbLocalTestUtils.initSqLite();
        int retry = 1;
        Exception currentException = null;
        while (retry++ < RETRY_CNT) {
            try {
                server = ServerRunner.createServerFromCommandLineArgs(new String[]{"-inMemory", "-sharedDb", "-port", String.valueOf(port)});
                server.start();
                return;
            } catch (Exception e) {
                currentException = e;
                port++;
            }
        }
        throw new IllegalStateException("Fail Start Embedded DyanoDB", currentException);
    }

    @PreDestroy
    public void stop() {
        if (server == null) {
            return;
        }
        try {
            server.stop();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public int getPort() {
        return port;
    }
}
