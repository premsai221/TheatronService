package com.example.TheatronService.repo;

import com.example.TheatronService.model.TheatronMedia;
import com.example.TheatronService.model.TheatronUser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.services.dynamodb.model.ProjectionType;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class MediaRepo {

    private final String TABLE_NAME;

    private final DynamoDbEnhancedClient enhancedClient;

    private final DynamoDbTable<TheatronMedia> mediaTable;

    public MediaRepo(DynamoDbEnhancedClient enhancedClient, @Value("${media.table.name}") final String TABLE_NAME) {
        this.TABLE_NAME = TABLE_NAME;
        this.enhancedClient = enhancedClient;
        mediaTable = enhancedClient.table(TABLE_NAME, TableSchema.fromBean(TheatronMedia.class));
        try {
            mediaTable.describeTable();
        } catch (ResourceNotFoundException exception) {
            createTable();
        }
    }

    private void createTable() {
        mediaTable.createTable();
    }

    public void addNewMedia(TheatronMedia theatronMedia) {
        mediaTable.putItem(theatronMedia);
    }

    public void removeMedia(String username, String mediaId) {
        mediaTable.deleteItem(Key.builder().partitionValue(username).sortValue(mediaId).build());
    }

    public TheatronMedia getUserMedia(String username, String mediaId) {
        Key compositeKey = Key.builder().partitionValue(username).sortValue(mediaId).build();
        return mediaTable.getItem(compositeKey);
    }

    public List<TheatronMedia> getAllUserMedia(String username) {
        Key partitionKey = Key.builder().partitionValue(username).build();
        QueryConditional queryConditional = QueryConditional.keyEqualTo(partitionKey);
        Iterator<Page<TheatronMedia>> mediaPageIterator = mediaTable.query(queryConditional).stream().iterator();
        List<TheatronMedia> userMedias = new ArrayList<TheatronMedia>();
        while (mediaPageIterator.hasNext()) {
            userMedias.addAll(mediaPageIterator.next().items().stream().toList());
        }
        return userMedias;
    }

    public void updateItem(TheatronMedia theatronMedia) {
        mediaTable.updateItem(theatronMedia);
    }

}
