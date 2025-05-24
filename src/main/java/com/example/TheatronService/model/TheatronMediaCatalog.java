package com.example.TheatronService.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@ToString
@Getter
@Setter
public class TheatronMediaCatalog {
    private String username; // Partition Key
    private String mediaId; // Sort Key // GSI
    private String mediaUrl;
    private String mediaType;
    private String mediaStatus; // In process, Processed
    private String mediaTitle;
    private String mediaDescription;
    private long mediaSize;
    private String uploadDate;
    private String currentRoom; // empty string ("") - no session
    private String mediaAccessType; // Public, Private, Link
    private List<String> externalUserAccessList;
}
