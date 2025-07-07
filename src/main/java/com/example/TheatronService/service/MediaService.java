package com.example.TheatronService.service;

import com.example.TheatronService.model.TheatronMedia;
import com.example.TheatronService.model.mediaDTO.GenerateS3UploadUrlResponse;
import com.example.TheatronService.model.mediaDTO.MediaDetails;
import com.example.TheatronService.model.mediaDTO.ProcessUploadedMediaRequest;
import com.example.TheatronService.repo.MediaRepo;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
public class MediaService {

    private final MediaRepo mediaRepo;

    private final S3BucketService s3BucketService;

    private final JwtService jwtService;

    public MediaService(S3BucketService s3BucketService, MediaRepo mediaRepo, JwtService jwtService) {
        this.s3BucketService = s3BucketService;
        this.mediaRepo = mediaRepo;
        this.jwtService = jwtService;
    }

    private String generateMediaID() {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(HexFormat.of().parseHex(UUID.randomUUID().toString().replace("-", "")));
    }

    public String getFileExtensionFromFiletype(String filetype) {
        return ".".concat(filetype.split("/")[1]);
    }

    public GenerateS3UploadUrlResponse generatePreSignedS3Url(final String filename, final String filetype) {
        String mediaId = generateMediaID();
        String uploadUrl = s3BucketService.generatePreSignedUrl(mediaId + getFileExtensionFromFiletype(filetype));
        return GenerateS3UploadUrlResponse.builder()
                .objectId(mediaId)
                .uploadUrl(uploadUrl)
                .build();
    }

    public void createNewMediaItem(ProcessUploadedMediaRequest request, String username) {
        TheatronMedia media = new TheatronMedia();
        media.setMediaId(request.getId());
        media.setUsername(username);
        media.setOwner(username);
        media.setMediaUrl(request.getUploadUrl());
        media.setMediaStatus("IN_PROCESS");
        media.setMediaType(request.getType());
        media.setMediaTitle(request.getTitle());
        media.setMediaDescription(request.getDescription());
        media.setMediaDuration(request.getDuration());
        media.setMediaSize(request.getSize());
        media.setUploadDate(Instant.now().toString());
        media.setExternalUserAccessList(List.of());
        mediaRepo.addNewMedia(media);
    }

    public MediaDetails fetchMediaDetails(String username, String mediaId) {
        TheatronMedia media = mediaRepo.getUserMedia(username, mediaId);
        if (media == null)
            return null;
        String currentRoom;
        if (media.getOwner().equals(username)) {
            currentRoom = media.getCurrentRoom();
        } else {
            TheatronMedia ownersMedia = mediaRepo.getUserMedia(media.getOwner(), mediaId);
            currentRoom = ownersMedia.getCurrentRoom();
        }
        return MediaDetails.builder()
                .id(media.getMediaId())
                .owner(media.getOwner())
                .title(media.getMediaTitle())
                .description(media.getMediaDescription())
                .status(media.getMediaStatus())
                .url(media.getMediaUrl())
                .uploadedOn(media.getUploadDate())
                .duration(media.getMediaDuration())
                .currentRoom(currentRoom)
                .externalUserAccessList(media.getUsername().equals(media.getOwner()) ? media.getExternalUserAccessList() : null)
                .build();
    }

    public List<MediaDetails> fetchAllUserMedia(String username) {
        List<TheatronMedia> userMedias = mediaRepo.getAllUserMedia(username);
        return userMedias.stream()
                .map(theatronMedia ->
                        MediaDetails.builder()
                                .id(theatronMedia.getMediaId())
                                .title(theatronMedia.getMediaTitle())
                                .owner(theatronMedia.getOwner())
                                .duration(theatronMedia.getMediaDuration())
                                .status(theatronMedia.getMediaStatus())
                                .thumbnail(theatronMedia.getMediaThumbnail())
                                .build())
                .toList();
    }

    private String buildS3MediaKeyFromMediaId(String mediaId) {
        return "".concat(mediaId).concat("/").concat(mediaId).concat(".m3u8");
    }

    public void updateMediaProcessStatusToProcessed(String mediaId, String username) {
        updateMediaProcessStatus(mediaId, username, "PROCESSED");
    }

    public void updateMediaProcessStatus(String mediaId, String username, String status) {
        TheatronMedia media = mediaRepo.getUserMedia(username, mediaId);
        media.setMediaStatus(status);
        String s3MediaKey = buildS3MediaKeyFromMediaId(mediaId);
        media.setMediaUrl(s3BucketService.getS3UrlFromKey(s3MediaKey));
        mediaRepo.updateItem(media);
    }

    public List<String> shareMediaWithUser(String username, String mediaId, String userToAdd) {
        TheatronMedia existingMedia = mediaRepo.getUserMedia(username, mediaId);
        List<String> externalUserAccessList = existingMedia.getExternalUserAccessList();
        if (!externalUserAccessList.contains(userToAdd)) {
            externalUserAccessList.add(userToAdd);
            existingMedia.setExternalUserAccessList(externalUserAccessList);
            mediaRepo.updateItem(existingMedia);
        }
        existingMedia.setUsername(userToAdd);
        existingMedia.setExternalUserAccessList(List.of());
        mediaRepo.addNewMedia(existingMedia);
        return externalUserAccessList;
    }

    public List<String> removeUserFromMedia(String username, String mediaId, String userToRemove) {
        TheatronMedia existingMedia = mediaRepo.getUserMedia(username, mediaId);
        List<String> externalUserAccessList = existingMedia.getExternalUserAccessList();
        if (externalUserAccessList.contains(userToRemove)) {
            externalUserAccessList.remove(userToRemove);
            existingMedia.setExternalUserAccessList(externalUserAccessList);
            mediaRepo.updateItem(existingMedia);
        }
        mediaRepo.removeMedia(userToRemove, mediaId);
        return externalUserAccessList;
    }

    public String createNewRoom(String username, String mediaId, String roomName) {
        TheatronMedia media = mediaRepo.getUserMedia(username, mediaId);
        if (!media.getOwner().equals(username) || (media.getCurrentRoom() != null && !media.getCurrentRoom().isEmpty())) {
            return null;
        }
        media.setCurrentRoom(roomName);
        mediaRepo.updateItem(media);
        return roomName;
    }

    public String deleteRoom(String username, String mediaId, String roomName) {
        TheatronMedia media = mediaRepo.getUserMedia(username, mediaId);
        if (!media.getOwner().equals(username) || media.getCurrentRoom().isEmpty() || !media.getCurrentRoom().equals(roomName)) {
            return null;
        }
        media.setCurrentRoom(null);
        mediaRepo.updateItem(media);
        return roomName;
    }

    public String checkIfRoomActive(String username, String mediaId, String roomName) {
        TheatronMedia media = mediaRepo.getUserMedia(username, mediaId);
        if (media.getOwner().equals(username)) {
            return jwtService.generateSharedToken(mediaId + "|" + roomName, username, true);
        } else {
            TheatronMedia ownerMedia = mediaRepo.getUserMedia(media.getOwner(), mediaId);
            return jwtService.generateSharedToken(mediaId + "|" + roomName, username, false);
        }
    }
}
