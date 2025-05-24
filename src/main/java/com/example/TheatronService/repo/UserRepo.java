package com.example.TheatronService.repo;

import com.example.TheatronService.model.TheatronUser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.services.dynamodb.model.ProjectionType;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;

import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
public class UserRepo {

    private final String TABLE_NAME;

    private final DynamoDbEnhancedClient enhancedClient;

    private final DynamoDbTable<TheatronUser> userTable;

    private final DynamoDbIndex<TheatronUser> userEmailIndex;

    public UserRepo(DynamoDbEnhancedClient enhancedClient, @Value("${user.table.name}") final String TABLE_NAME) {
        this.TABLE_NAME = TABLE_NAME;
        this.enhancedClient = enhancedClient;
        userTable = enhancedClient.table(TABLE_NAME, TableSchema.fromBean(TheatronUser.class));
        userEmailIndex = userTable.index("UserEmailIndex");
        try {
            System.out.println(userTable.describeTable().toString());
        } catch (ResourceNotFoundException exception) {
            createTable();
        }
    }

    private void createTable() {
        userTable.createTable(builder -> builder
                .globalSecondaryIndices(gsi -> gsi
                        .indexName("UserEmailIndex")
                        .projection(projection -> projection
                                .projectionType(ProjectionType.KEYS_ONLY)
                                .build())));

    }

    public TheatronUser getUserFromUsername(final String username) {
        return userTable.getItem(Key.builder().partitionValue(username).build());
    }

    public boolean isEmailTaken(final String email) {
        QueryConditional queryConditional = QueryConditional.keyEqualTo(k -> k.partitionValue(email));
        for (Page<TheatronUser> theatronUserPage : userEmailIndex.query(queryConditional)) {
            if (theatronUserPage.count() > 0)
                return true;
        }
        return false;
    }

    public void addUser(TheatronUser user) {
        userTable.putItem(user);
    }

}
