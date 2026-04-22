package com.hirewise.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.hirewise.exception.FileStorageException;

@Component
public class FileUtils {

    private static final String UPLOAD_DIR = "uploads/";

    /**
     * Generate unique filename
     */
    public String generateFileName(String originalFileName) {
        String extension = "";
        int dotIndex = originalFileName.lastIndexOf('.');
        if (dotIndex > 0) {
            extension = originalFileName.substring(dotIndex);
        }
        return UUID.randomUUID().toString() + extension;
    }

    /**
     * Save file to disk and return URL
     */
    public String saveFile(MultipartFile file, String subDirectory) throws IOException {
        if (file.isEmpty()) {
            throw new FileStorageException("Failed to store empty file");
        }

        // Create directory if not exists
        String uploadPath = UPLOAD_DIR + subDirectory + "/";
        File directory = new File(uploadPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Generate unique filename
        String fileName = generateFileName(file.getOriginalFilename());
        Path targetLocation = Paths.get(uploadPath + fileName);

        // Copy file
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        // Return URL that can be accessed via browser
        return "/uploads/" + subDirectory + "/" + fileName;
    }

    /**
     * Delete file from disk
     */
    public boolean deleteFile(String filePath) {
        try {
            Path path = Paths.get(filePath.replace("/uploads/", UPLOAD_DIR));
            return Files.deleteIfExists(path);
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Get file extension
     */
    public String getFileExtension(String fileName) {
        if (fileName == null) {
			return "";
		}
        int dotIndex = fileName.lastIndexOf('.');
        return dotIndex > 0 ? fileName.substring(dotIndex).toLowerCase() : "";
    }

    /**
     * Check if file type is allowed
     */
    public boolean isAllowedFileType(String fileName, String[] allowedTypes) {
        String extension = getFileExtension(fileName);
        for (String allowedType : allowedTypes) {
            if (allowedType.equalsIgnoreCase(extension)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get file size in readable format
     */
    public String getReadableFileSize(long size) {
        if (size <= 0) {
			return "0";
		}
        String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return String.format("%.1f %s", size / Math.pow(1024, digitGroups), units[digitGroups]);
    }
}