package com.example.haus.util;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.haus.constant.MediaType;
import com.example.haus.exception.UploadFileException;
import com.example.haus.service.FileValidatorService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Log4j2
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UploadFileUtil {

    Cloudinary cloudinary;

    FileValidatorService fileValidatorService;


    public String uploadFile(MultipartFile multipartFile) {
        fileValidatorService.validateFile(multipartFile, MediaType.IMAGE);

        try {
            String safeFilename = fileValidatorService.generateSafeFileName(multipartFile.getOriginalFilename());
            String publicId = extractPublicId(safeFilename);

            Map<String, Object> uploadParams = ObjectUtils.asMap(
                    "folder", "haus/products",
                    "resource_type", "image",
                    "overwrite", true,
                    "public_id", publicId,
                    "transformation", "w_400,h_400,c_fill,q_auto");

            Map result = cloudinary.uploader().upload(
                    multipartFile.getBytes(), uploadParams);

            String secureUrl = result.get("secure_url").toString();
            log.info("File uploaded successfully: {} -> {}", multipartFile.getOriginalFilename(), secureUrl);

            return secureUrl;
        } catch (IOException e) {
            log.error("Upload failed for file: {}", multipartFile.getOriginalFilename(), e);
            throw new UploadFileException("Upload file failed!", e.getCause());
        }
    }

    public List<String> uploadMultipleFiles(List<MultipartFile> multipartFiles) {
        List<String> imageUrls = new ArrayList<>();

        if (multipartFiles == null || multipartFiles.isEmpty()) {
            return imageUrls;
        }

        for (MultipartFile file : multipartFiles) {
            if (file != null && !file.isEmpty()) {
                fileValidatorService.validateFile(file, MediaType.IMAGE);

                try {
                    String safeFilename = fileValidatorService.generateSafeFileName(
                            file.getOriginalFilename());
                    String publicId = extractPublicId(safeFilename);

                    Map<String, Object> uploadParams = ObjectUtils.asMap(
                            "folder", "haus/products",
                            "resource_type", "image",
                            "overwrite", true,
                            "public_id", publicId,
                            "transformation", "w_600,h_400,c_fill,q_auto");

                    var result = cloudinary.uploader().upload(
                            file.getBytes(), uploadParams);

                    String secureUrl = result.get("secure_url").toString();
                    imageUrls.add(secureUrl);

                    log.info("File uploaded: {} -> {}", file.getOriginalFilename(), secureUrl);

                } catch (IOException e) {
                    log.error("Failed to upload file: {}", file.getOriginalFilename(), e);
                    throw new UploadFileException("Upload file failed: " + file.getOriginalFilename(), e.getCause());
                }
            }
        }

        log.info("Uploaded {} files successfully", imageUrls.size());
        return imageUrls;
    }

    public void destroyFileWithUrl(String url) {
        String publicId = extractPublicIdFromUrl(url);
        try {
            Map result = cloudinary.uploader().destroy(
                    publicId, ObjectUtils.emptyMap());
            log.info("Destroyed image public_id={}, result={}", publicId, result);
        } catch (IOException e) {
            log.error("Failed to destroy file: {}", url, e);
            throw new UploadFileException("Remove file failed!", e.getCause());
        }
    }

    private String extractPublicId(String safeFilename) {
        if(safeFilename == null || !safeFilename.contains(".")){
            return safeFilename;
        }
        return safeFilename.substring(0, safeFilename.lastIndexOf('.'));
    }

    private String extractPublicIdFromUrl(String url) {
        int startIndex = url.lastIndexOf("/") + 1;
        int endIndex = url.lastIndexOf(".");
        if(endIndex > startIndex){
            return url.substring(startIndex, endIndex);
        }
        return url.substring(startIndex);
    }
}
