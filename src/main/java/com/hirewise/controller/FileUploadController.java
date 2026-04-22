package com.hirewise.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.hirewise.exception.FileStorageException;
import com.hirewise.util.FileUtils;

@RestController
@RequestMapping("/api/upload")
//@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class FileUploadController {

    @Autowired
    private FileUtils fileUtils;

    @PostMapping
    public ResponseEntity<?> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "type", defaultValue = "resume") String type) {

        try {
            // Validate file
            if (file.isEmpty()) {
                throw new FileStorageException("Failed to store empty file");
            }

            // Validate file type for resumes
            String contentType = file.getContentType();
            if (type.equals("resume")) {
                if (!contentType.equals("application/pdf") &&
                    !contentType.equals("application/msword") &&
                    !contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document")) {
                    throw new FileStorageException("Only PDF and DOC files are allowed");
                }
            }

            // Save file using FileUtils
            String fileUrl = fileUtils.saveFile(file, type);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("url", fileUrl);
            response.put("fileName", file.getOriginalFilename());
            response.put("message", "File uploaded successfully");

            return ResponseEntity.ok(response);

        } catch (IOException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to upload file: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (FileStorageException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}