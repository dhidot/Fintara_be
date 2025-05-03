package com.fintara.controllers;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.fintara.responses.ApiResponse;
import jakarta.annotation.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("v1/upload")
public class FileUploadController {

    @Resource
    private Cloudinary cloudinary;

    @PostMapping("/ktp")
    public ResponseEntity<ApiResponse<Map<String, Object>>> uploadKtp(@RequestParam("file") MultipartFile file) throws IOException {
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                "folder", "ktp" // folder khusus di cloudinary
        ));

        Map<String, Object> response = Map.of("url", uploadResult.get("secure_url"));
        return ResponseEntity.ok(ApiResponse.success("KTP berhasil diupload", response));
    }
}
