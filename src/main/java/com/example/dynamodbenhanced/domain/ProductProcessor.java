package com.example.dynamodbenhanced.domain;

import org.springframework.stereotype.Component;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductProcessor {

    public static final String TABLE_NAME = "product";
    private final DynamoDbTable<Product> dynamoDbTable;
    private final DynamoDbIndex<Product> sellerIdIndex;

    public ProductProcessor(DynamoDbEnhancedClient dynamoDbEnhancedClient) {
        this.dynamoDbTable = dynamoDbEnhancedClient.table(TABLE_NAME, TableSchema.fromBean(Product.class));
        this.sellerIdIndex = dynamoDbTable.index("seller_id");
    }

    public List<Product> findQuery(ProductQuery query) {
        QueryEnhancedRequest queryEnhancedRequest = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(Key.builder()
                        .partitionValue(query.getSellerId())
                        .build()
                ))
                .exclusiveStartKey(query.exclusiveStartKey())
                .limit(query.getLimit())
                .scanIndexForward(false)
                .build();
        return sellerIdIndex.query(queryEnhancedRequest).stream().flatMap(d -> d.items().stream()).collect(Collectors.toList());
    }

    public void save(Product product) {
        dynamoDbTable.putItem(product);
    }

}
