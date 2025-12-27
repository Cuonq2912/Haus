package com.example.haus.config.properties;

import com.example.haus.constant.CommonConstant;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.List;


@Data
@Validated
@FieldDefaults(level = AccessLevel.PRIVATE)
@ConfigurationProperties(prefix = "file-upload")
public class FileUploadProperties {

    boolean enabled = CommonConstant.TRUE;

    long maxFileSize = 10 * 1024 * 1024;

    long maxImageSize = 5 * 1024 * 1024;

    List<String> allowedImageTypes = List.of(
        "image/jpeg",
        "image/png",
        "image/gif",
        "image/webp");

    List<String> allowedDocumentTypes = List.of("application/pdf");

    VirusScanConfig virusScan = new VirusScanConfig();

    @Data
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class VirusScanConfig {

        boolean enabled = CommonConstant.FALSE;

        String clamavHost = "localhost";

        int clamavPort = 3310;

        int timeout = 30000;
    }
}
