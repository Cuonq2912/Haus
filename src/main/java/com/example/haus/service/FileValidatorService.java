package com.example.haus.service;

import com.example.haus.constant.MediaType;
import org.springframework.web.multipart.MultipartFile;


public interface FileValidatorService {

    void validateFile(MultipartFile file, MediaType mediaType);

    String generateSafeFileName(String originalFilename);
}
