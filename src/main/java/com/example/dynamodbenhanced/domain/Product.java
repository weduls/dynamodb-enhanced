package com.example.dynamodbenhanced.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@Getter
@Setter
@DynamoDbBean
@NoArgsConstructor
public class Product {

    private Long productId;
    private Long sellerId;
    private Long rating;
    private Long ttl;

    @Builder
    public Product(Long productId, Long sellerId, Long rating, Long ttl) {
        this.productId = productId;
        this.sellerId = sellerId;
        this.rating = rating;
        this.ttl = ttl;
    }

    @DynamoDbPartitionKey
    public Long getProductId() {
        return productId;
    }

    @DynamoDbSecondaryPartitionKey(indexNames = "seller_id")
    public Long getSellerId() {
        return sellerId;
    }

    @DynamoDbSortKey
    public Long getRating() {
        return rating;
    }
}
