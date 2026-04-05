package com.example.haus.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VirusScanResult {

    private boolean infected;

    private String virusName;

    public static VirusScanResult clean() {
        return new VirusScanResult(false, null);
    }

    public static VirusScanResult infected(String virusName) {
        return new VirusScanResult(true, virusName);
    }
}
