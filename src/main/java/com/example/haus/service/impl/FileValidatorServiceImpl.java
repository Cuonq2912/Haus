package com.example.haus.service.impl;

import com.example.haus.config.properties.FileUploadProperties;
import com.example.haus.constant.MediaType;
import com.example.haus.domain.dto.VirusScanResult;
import com.example.haus.exception.FileValidationException;
import com.example.haus.exception.FileValidationException.FileValidationErrorCode;
import com.example.haus.service.FileValidatorService;
import com.example.haus.service.VirusScanService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation của FileValidatorService
 * Cung cấp validation cho MIME type, magic bytes, file size và virus scanning
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FileValidatorServiceImpl implements FileValidatorService {

    private final FileUploadProperties properties;
    private final Optional<VirusScanService> virusScanService;

    private static final Map<String, byte[]> MAGIC_BYTES = Map.of(
        "image/jpeg", new byte[] { (byte) 0xFF, (byte) 0xD8, (byte) 0xFF },
        "image/png", new byte[] { (byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A },
        "image/gif", new byte[] { 0x47, 0x49, 0x46, 0x38 },
        "image/webp", new byte[] { 0x52, 0x49, 0x46, 0x46 },
        "application/pdf", new byte[] { 0x25, 0x50, 0x44, 0x46 });

    @Override
    public void validateFile(MultipartFile file, MediaType mediaType) {
        if (!properties.isEnabled()) {
            log.debug("File validation is disabled");
            return;
        }

        validateNotEmpty(file);
        validateFileSize(file, mediaType);
        validateMimeType(file, mediaType);
        validateMagicBytes(file);
        scanForVirus(file);

        log.info("File validation passed: name={}, size={}, type={}",
            file.getOriginalFilename(), file.getSize(), file.getContentType());
    }

    @Override
    public String generateSafeFileName(String originalFilename) {
        String extension = extractSafeExtension(originalFilename);
        String safeFileName = UUID.randomUUID().toString() + extension;
        log.debug("Generated safe filename: {} -> {}", originalFilename, safeFileName);
        return safeFileName;
    }

    private void validateNotEmpty(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            log.warn("File validation failed: file is empty or null");
            throw new FileValidationException(
                "File không được để trống",
                FileValidationErrorCode.EMPTY_FILE);
        }
    }

    private void validateFileSize(MultipartFile file, MediaType mediaType) {
        long maxSize = getMaxSizeForType(mediaType);

        if (file.getSize() > maxSize) {
            log.warn("File validation failed: size {} exceeds max {} for type {}",
                file.getSize(), maxSize, mediaType);
            throw new FileValidationException(
                String.format("Kích thước file (%s) vượt quá giới hạn cho phép (%s)",
                    formatFileSize(file.getSize()), formatFileSize(maxSize)),
                FileValidationErrorCode.FILE_TOO_LARGE);
        }
    }

    private void validateMimeType(MultipartFile file, MediaType mediaType) {
        String contentType = file.getContentType();
        List<String> allowedTypes = getAllowedTypesForType(mediaType);

        if (contentType == null || !allowedTypes.contains(contentType)) {
            log.warn("File validation failed: MIME type '{}' not allowed. Allowed: {}",
                contentType, allowedTypes);
            throw new FileValidationException(
                String.format("Loại file '%s' không được hỗ trợ. Các loại được phép: %s",
                    contentType, String.join(", ", allowedTypes)),
                FileValidationErrorCode.INVALID_MIME_TYPE);
        }
    }

    private void validateMagicBytes(MultipartFile file) {
        try {
            byte[] fileBytes = file.getBytes();
            String contentType = file.getContentType();

            byte[] expectedMagic = MAGIC_BYTES.get(contentType);
            if (expectedMagic == null) {
                log.debug("No magic bytes defined for type: {}, skipping check", contentType);
                return;
            }

            if (fileBytes.length < expectedMagic.length) {
                log.warn("File too small to validate magic bytes: {} bytes", fileBytes.length);
                throw new FileValidationException(
                    "File quá nhỏ để xác thực nội dung",
                    FileValidationErrorCode.INVALID_MAGIC_BYTES);
            }

            for (int i = 0; i < expectedMagic.length; i++) {
                if (fileBytes[i] != expectedMagic[i]) {
                    log.warn("Magic bytes mismatch for file: {}", file.getOriginalFilename());
                    throw new FileValidationException(
                        "Nội dung file không khớp với loại file được khai báo",
                        FileValidationErrorCode.INVALID_MAGIC_BYTES);
                }
            }

            log.debug("Magic bytes validation passed for type: {}", contentType);

        } catch (IOException e) {
            log.error("Failed to read file for magic bytes validation", e);
            throw new FileValidationException(
                "Không thể đọc file để xác thực",
                FileValidationErrorCode.INVALID_MAGIC_BYTES,
                e);
        }
    }

    private void scanForVirus(MultipartFile file) {
        if (!properties.getVirusScan().isEnabled()) {
            log.debug("Virus scan is disabled");
            return;
        }

      virusScanService.ifPresent(scanner -> {
          log.info("Scanning file for viruses: {}", file.getOriginalFilename());
          VirusScanResult result = scanner.scan(file);

          if (result.isInfected()) {
              log.error("Virus detected in file {}: {}",
                  file.getOriginalFilename(), result.getVirusName());
              throw new FileValidationException(
                  "Phát hiện virus trong file: " + result.getVirusName(),
                  FileValidationErrorCode.VIRUS_DETECTED);
          }

          log.info("Virus scan passed for file: {}", file.getOriginalFilename());
        });
    }

    private String extractSafeExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }

        String ext = filename.substring(filename.lastIndexOf('.'));

        if (ext.matches("\\.[a-zA-Z0-9]{1,10}")) {
            return ext.toLowerCase();
        }

        log.warn("Unsafe extension detected: {}, returning empty", ext);
        return "";
    }

    private long getMaxSizeForType(MediaType mediaType) {
        return switch (mediaType) {
            case IMAGE -> properties.getMaxImageSize();
            case DOCUMENT, VIDEO -> properties.getMaxFileSize();
        };
    }

    private List<String> getAllowedTypesForType(MediaType mediaType) {
        return switch (mediaType) {
            case IMAGE -> properties.getAllowedImageTypes();
            case DOCUMENT -> properties.getAllowedDocumentTypes();
            case VIDEO -> List.of();
        };
    }

    private String formatFileSize(long bytes) {
        if (bytes < 1024)
            return bytes + " B";
        if (bytes < 1024 * 1024)
            return String.format("%.1f KB", bytes / 1024.0);
        return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
    }
}
