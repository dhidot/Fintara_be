package com.fintara.controllers;

import com.fintara.dtos.CloudinaryUploadResponse;
import com.fintara.responses.ApiResponse;
import com.fintara.services.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("v1/cloudinary")
public class CloudinaryController {

    @Autowired
    private CloudinaryService cloudinaryService;

    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<CloudinaryUploadResponse>> uploadImages(
            @RequestParam("ktpPhoto") MultipartFile ktpPhoto,
            @RequestParam("selfiePhoto") MultipartFile selfiePhoto) {

        try {
            String ktpUrl = cloudinaryService.uploadFile(ktpPhoto);
            String selfieUrl = cloudinaryService.uploadFile(selfiePhoto);

            CloudinaryUploadResponse response = new CloudinaryUploadResponse(ktpUrl, selfieUrl);
            return ResponseEntity.ok(ApiResponse.success("Upload successful", response));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "Upload failed"));
        }
    }
}
