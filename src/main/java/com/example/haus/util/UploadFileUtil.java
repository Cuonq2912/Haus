package com.example.haus.util;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.haus.exception.UploadFileException;
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
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UploadFileUtil {

    final Cloudinary cloudinary;

    public String uploadFile(MultipartFile multipartFile) {
        try {
            String resourceType = getResourceType(multipartFile);
            Map<String, Object> uploadParams = ObjectUtils.asMap(
                    "folder", "haus/products",
                    "resource_type", "image",
                    "overwrite", true,
                    "transformation", "w_400,h_400,c_fill,q_auto"
            );

            Map result = cloudinary.uploader().upload(
                    multipartFile.getBytes(), uploadParams
            );
            return result.get("secure_url").toString();
        } catch (IOException e) {
            throw new UploadFileException("Upload file failed!", e.getCause());
        }
    }

    public String uploadImage(byte[] bytes) {
        try{
            Map result = cloudinary.uploader().upload(
                    bytes, ObjectUtils.asMap("resource_type", "image"));
            return result.get("secure_url").toString();
        } catch (IOException e) {
            throw new UploadFileException("Upload image failed!", e.getCause());
        }
    }

    public List<String> uploadMultipleFiles(List<MultipartFile> multipartFiles) {
        List<String> imageUrls = new ArrayList<>();

        if (multipartFiles == null || multipartFiles.isEmpty()) {
            return imageUrls;
        }

        for (MultipartFile file : multipartFiles) {
            if (file != null && !file.isEmpty()) {
                try {
                    Map<String, Object> uploadParams = ObjectUtils.asMap(
                            "folder", "haus/products",
                            "resource_type", "image",
                            "overwrite", true,
                            "transformation", "w_600,h_400,c_fill,q_auto");

                    Map result = cloudinary.uploader().upload(
                            file.getBytes(), uploadParams);
                    imageUrls.add(result.get("secure_url").toString());
                } catch (IOException e) {
                    log.error("Failed to upload file: {}", file.getOriginalFilename(), e);
                    throw new UploadFileException("Upload file failed: " + file.getOriginalFilename(), e.getCause());
                }
            }
        }

        return imageUrls;
    }

    public void destroyFileWithUrl(String url) {
        int startIndex = url.lastIndexOf("/") + 1;
        int endIndex = url.lastIndexOf(".");
        String publicId = url.substring(startIndex, endIndex);
        try {
            Map result = cloudinary.uploader().destroy(
                    publicId, ObjectUtils.emptyMap());
            log.info("Destroy image public id {} {}", publicId, result.toString());
        } catch (IOException e) {
            throw new UploadFileException("Remove file failed!", e.getCause());
        }
    }

    private static String getResourceType(MultipartFile multipartFile) throws IOException {
        String contentType = multipartFile.getContentType();
        if (contentType != null) {
            if (contentType.startsWith("image/")) {
                return "image";
            } else if (contentType.startsWith("video/")) {
                return "video";
            } else {
                return "auto";
            }
        }
        return null;
    }
}
