package com.sakuBCA.controllers;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import jakarta.annotation.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/v1/upload")
public class FileUploadController {

    @Resource
    private Cloudinary cloudinary;

    @PostMapping("/ktp")
    public ResponseEntity<Map<String, Object>> uploadKtp(@RequestParam("file") MultipartFile file) throws IOException {
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                "folder", "ktp" // folder khusus di cloudinary
        ));
        return ResponseEntity.ok(Map.of("url", uploadResult.get("secure_url")));
    }
}
