package com.example.dynamodbenhanced.domain;

import lombok.Builder;
import lombok.Getter;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Getter
public class ProductQuery {

    private final int limit;
    private final Long sellerId;
    private final Long lastReadProductId;
    private final Long rating;

    @Builder
    public ProductQuery(int limit, Long sellerId, Long lastReadProductId, Long rating) {
        this.limit = limit;
        this.sellerId = sellerId;
        this.lastReadProductId = lastReadProductId;
        this.rating = rating;
    }

    public Map<String, AttributeValue> exclusiveStartKey() {
        if (Objects.nonNull(lastReadProductId)) {
            Map<String, AttributeValue> exclusiveStartKeyMap = new HashMap<>();
            exclusiveStartKeyMap.put("sellerId", AttributeValue.builder().n(String.valueOf(sellerId)).build());
            exclusiveStartKeyMap.put("productId", AttributeValue.builder().n(String.valueOf(lastReadProductId)).build());
            exclusiveStartKeyMap.put("rating", AttributeValue.builder().n(String.valueOf(rating)).build());
            return exclusiveStartKeyMap;
        }
        return null;
    }

    public Expression getFilterExpression() {
        Map<String, AttributeValue> expressionValue = new HashMap<>();

        if (Objects.nonNull(sellerId)) {
            expressionValue.put(":sellerId", AttributeValue.builder()
                            .n(String.valueOf(sellerId))
                    .build());
            return Expression.builder()
                    .expression(":sellerId = sellerId")
                    .expressionValues(expressionValue)
                    .build();

        }
        return null;
    }
}
