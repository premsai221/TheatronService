package com.example.TheatronService.controller;

import com.example.TheatronService.model.mediaDTO.*;
import com.example.TheatronService.service.MediaService;
import com.example.TheatronService.service.UserService;
import com.example.TheatronService.service.VideoProcessingMQService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/media")
public class MediaController {

    private final MediaService mediaService;

    private final VideoProcessingMQService videoProcessingMQService;

    private final UserService userService;

    public MediaController(MediaService mediaService, VideoProcessingMQService videoProcessingMQService, UserService userService) {
        this.mediaService = mediaService;
        this.videoProcessingMQService = videoProcessingMQService;
        this.userService = userService;
    }

    @GetMapping("/videos")
    public GetUserMediasResponse getUserVideos(HttpServletRequest request) {
        String username = request.getUserPrincipal().getName();
        List<MediaDetails> mediaDetails = mediaService.fetchAllUserMedia(username);
        return GetUserMediasResponse.builder().username(username).videos(mediaDetails).build();
    }

    @PostMapping("/generate/url")
    public GenerateS3UploadUrlResponse generateS3UploadURL(@RequestBody GenerateS3UploadUrlRequest generateS3UploadURLRequest) {
        System.out.println(generateS3UploadURLRequest);
        String filename =  generateS3UploadURLRequest.getFilename();
        String filetype = generateS3UploadURLRequest.getFiletype();
        return mediaService.generatePreSignedS3Url(filename, filetype);
    }

    @PostMapping("/process/upload")
    public String processUploadedMedia(@RequestBody ProcessUploadedMediaRequest processUploadedMediaRequest, HttpServletRequest request) {
        String username = request.getUserPrincipal().getName();
        mediaService.createNewMediaItem(processUploadedMediaRequest, username);
        String message = username.concat("|")
                .concat(processUploadedMediaRequest.getId())
                .concat(mediaService.getFileExtensionFromFiletype(processUploadedMediaRequest.getType()));
        videoProcessingMQService.sendMessage(message);
        return "DONE";
    }

    @PostMapping("/details")
    public MediaDetails getMediaDetails(@RequestBody GetMediaDetailsRequest getMediaDetailsRequest, HttpServletRequest request) {
        String username = request.getUserPrincipal().getName();
        String mediaId = getMediaDetailsRequest.getMediaId();
        return mediaService.fetchMediaDetails(username, mediaId);
    }

    @PostMapping("/share/user")
    public ShareMediaResponse shareMediaWithUser(@RequestBody ShareMediaRequest shareMediaRequest, HttpServletRequest request) {
        String username = request.getUserPrincipal().getName();
        String mediaId = shareMediaRequest.getMediaId();
        String userToAdd = shareMediaRequest.getUsername();
        List<String> accessList = null;
        if (!username.equals(userToAdd) && userService.loadUserByUsername(userToAdd) != null) {
            accessList = mediaService.shareMediaWithUser(username, mediaId, userToAdd);
        }
        return ShareMediaResponse.builder().updatedAccessList(accessList).build();
    }

    @PostMapping("/remove/user")
    public ShareMediaResponse removeMediaAccessToUser(@RequestBody ShareMediaRequest shareMediaRequest, HttpServletRequest request) {
        String username = request.getUserPrincipal().getName();
        String mediaId = shareMediaRequest.getMediaId();
        String userToRemove = shareMediaRequest.getUsername();
        List<String> accessList = null;
        if (!username.equals(userToRemove) && userService.loadUserByUsername(userToRemove) != null) {
            accessList = mediaService.removeUserFromMedia(username, mediaId, userToRemove);
        }
        return ShareMediaResponse.builder().updatedAccessList(accessList).build();
    }

}
