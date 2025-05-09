package com.fintara.controllers;

import com.fintara.dtos.CloudinaryUploadResponse;
import com.fintara.responses.ApiResponse;
import com.fintara.services.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("v1/cloudinary")
public class CloudinaryController {

    @Autowired
    private CloudinaryService cloudinaryService;

//    @PostMapping("/upload")
//    public ResponseEntity<ApiResponse<Map<String, String>>> uploadImage(@RequestParam("file") MultipartFile file) {
//        try {
//            String uploadResult = cloudinaryService.uploadFile(file);
//            String url = (String) uploadResult.get("secure_url");
//
//            return ResponseEntity.ok(ApiResponse.success("Upload successful", Map.of("url", url)));
//        } catch (Exception e) {
//            return ResponseEntity.internalServerError()
//                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "Upload failed"));
//        }
//    }
}
