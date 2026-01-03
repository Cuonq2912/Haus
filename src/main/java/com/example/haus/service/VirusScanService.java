package com.example.haus.service;

import com.example.haus.domain.dto.VirusScanResult;
import org.springframework.web.multipart.MultipartFile;

public interface VirusScanService {
    VirusScanResult scan(MultipartFile file);
}
