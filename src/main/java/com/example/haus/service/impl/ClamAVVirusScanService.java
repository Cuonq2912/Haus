package com.example.haus.service.impl;

import com.example.haus.config.properties.FileUploadProperties;
import com.example.haus.domain.dto.VirusScanResult;
import com.example.haus.exception.FileValidationException;
import com.example.haus.exception.FileValidationException.FileValidationErrorCode;
import com.example.haus.service.VirusScanService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;


@Service
@ConditionalOnProperty(name = "file-upload.virus-scan.enabled", havingValue = "true")
@RequiredArgsConstructor
@Slf4j
public class ClamAVVirusScanService implements VirusScanService {

    private final FileUploadProperties properties;

    @Override
    public VirusScanResult scan(MultipartFile file) {
        FileUploadProperties.VirusScanConfig config = properties.getVirusScan();

        try (Socket socket = new Socket()) {
            // Connect to ClamAV daemon
            log.debug("Connecting to ClamAV at {}:{}", config.getClamavHost(), config.getClamavPort());

            socket.connect(new InetSocketAddress(config.getClamavHost(), config.getClamavPort()), config.getTimeout());

            socket.setSoTimeout(config.getTimeout());

            // Send INSTREAM command (scan file từ stream)
            OutputStream out = socket.getOutputStream();
            out.write("zINSTREAM\0".getBytes());

            // Stream file content
            byte[] fileBytes = file.getBytes();

            // Send length prefix (4 bytes, big endian)
            ByteBuffer lengthBuffer = ByteBuffer.allocate(4);
            lengthBuffer.putInt(fileBytes.length);
            out.write(lengthBuffer.array());

            // Send file content
            out.write(fileBytes);

            // End stream (length = 0)
            lengthBuffer.clear();
            lengthBuffer.putInt(0);
            out.write(lengthBuffer.array());
            out.flush();

            // Read response
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String response = reader.readLine();

            log.debug("ClamAV response: {}", response);

            // Parse response
            if (response == null) {
                log.warn("No response from ClamAV");
                throw new FileValidationException("Không nhận được phản hồi từ virus scanner", FileValidationErrorCode.SCAN_FAILED);
            }

            if (response.contains("FOUND")) {
                // Format: stream: VirusName FOUND
                String virusName = extractVirusName(response);
                log.warn("Virus detected: {} in file: {}", virusName, file.getOriginalFilename());
                return VirusScanResult.infected(virusName);
            }

            if (response.contains("OK")) {
                log.info("File clean: {}", file.getOriginalFilename());
                return VirusScanResult.clean();
            }

            // Unexpected response
            log.warn("Unexpected ClamAV response: {}", response);
            throw new FileValidationException("Phản hồi không hợp lệ từ virus scanner: " + response,
                FileValidationErrorCode.SCAN_FAILED);
        } catch (FileValidationException e) {
            throw e;
        } catch (Exception e) {
            log.error("Virus scan failed for file: {}", file.getOriginalFilename(), e);
            throw new FileValidationException("Virus scan thất bại: " + e.getMessage(), FileValidationErrorCode.SCAN_FAILED, e);
        }
    }


    private String extractVirusName(String response) {
        try {
            // Remove "stream: " prefix if present
            String cleaned = response.replace("stream: ", "");
            // Remove " FOUND" suffix
            return cleaned.replace(" FOUND", "").trim();
        } catch (Exception e) {
            return "Unknown";
        }
    }
}
