package com.hirewise.service;

import org.apache.tika.Tika;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.InputStream;

@Service
public class ResumeParserService {
    
    public String extractText(MultipartFile file) throws Exception {
        Tika tika = new Tika();
        try (InputStream inputStream = file.getInputStream()) {
            String text = tika.parseToString(inputStream);
            return text;
        } catch (Exception e) {
            throw new Exception("Failed to extract text from file: " + e.getMessage());
        }
    }
}