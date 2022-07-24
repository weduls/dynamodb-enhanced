package com.example.dynamodbenhanced.domain;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.CreateTableEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.EnhancedGlobalSecondaryIndex;
import software.amazon.awssdk.services.dynamodb.model.Projection;
import software.amazon.awssdk.services.dynamodb.model.ProjectionType;
import software.amazon.awssdk.services.dynamodb.model.ProvisionedThroughput;

import java.util.Arrays;
import java.util.List;

import static com.example.dynamodbenhanced.domain.ProductProcessor.TABLE_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles({"test"})
class ProductProcessorTest {

    @Autowired
    private ProductProcessor sut;

    private static DynamoDbTable<Product> productDynamoDbTable;

    @BeforeEach
    void setupAll(@Autowired DynamoDbEnhancedClient dynamoDbEnhancedClient) {
        productDynamoDbTable = dynamoDbEnhancedClient.table(TABLE_NAME, TableSchema.fromBean(Product.class));
        productDynamoDbTable.createTable(CreateTableEnhancedRequest.builder()
                        .provisionedThroughput(ProvisionedThroughput.builder()
                                .readCapacityUnits(10L)
                                .writeCapacityUnits(10L)
                                .build())
                        .globalSecondaryIndices(Arrays.asList(EnhancedGlobalSecondaryIndex.builder()
                                        .indexName("seller_id")
                                        .projection(Projection.builder().projectionType(ProjectionType.ALL).build())
                                        .provisionedThroughput(ProvisionedThroughput.builder()
                                                .readCapacityUnits(10L)
                                                .writeCapacityUnits(10L)
                                                .build())
                                .build()))
                .build());
    }

    @AfterEach
    void afterAll() {
        productDynamoDbTable.deleteTable();
    }

    @Test
    @DisplayName("sellerId로 원하는 상품을 조회한다.")
    void returnProduct_bySellerId() {
        // given
        Long targetSellerId = 1L;
        Product foundProduct1 = Product.builder()
                .productId(1L)
                .sellerId(targetSellerId)
                .rating(5L)
                .ttl(3L)
                .build();

        Product foundProduct2 = Product.builder()
                .productId(2L)
                .sellerId(targetSellerId)
                .rating(3L)
                .ttl(4L)
                .build();

        Product notFoundProduct1 = Product.builder()
                .productId(3L)
                .sellerId(33L)
                .rating(5L)
                .ttl(6L)
                .build();

        sut.save(foundProduct1);
        sut.save(foundProduct2);
        sut.save(notFoundProduct1);

        // when
        List<Product> result = sut.findQuery(ProductQuery.builder()
                        .sellerId(targetSellerId)
                        .limit(2)
                .build());
        // then
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get(0)).usingRecursiveComparison().isEqualTo(foundProduct1);
        assertThat(result.get(1)).usingRecursiveComparison().isEqualTo(foundProduct2);
    }

}