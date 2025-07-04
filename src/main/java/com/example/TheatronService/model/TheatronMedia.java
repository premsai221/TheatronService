package com.example.TheatronService.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

import java.util.List;

@ToString
@Getter
@Setter
@DynamoDbBean
public class TheatronMedia {
    private String username; // Partition Key
    private String owner;
    private String mediaId; // Sort Key // GSI
    private String mediaUrl;
    private String mediaStatus; // In process, Processed
    private String mediaType;
    private String mediaTitle;
    private String mediaThumbnail;
    private String mediaDescription;
    private long mediaDuration;
    private long mediaSize;
    private String uploadDate;
    private String currentRoom; // empty string ("") - no session
    private String mediaAccessType; // Public, Private, Link // TODO
    private List<String> externalUserAccessList;

    @DynamoDbPartitionKey
    public String getUsername() {
        return username;
    }

    @DynamoDbSortKey
    public String getMediaId() {
        return mediaId;
    }

}
