package com.example.haus.util;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.List;

@Slf4j
public class RateLimitUtil {

    private static final List<String> IP_HEADER_CANDIDATES = Arrays.asList(
        "X-Forwarded-For", // Khi có nginx/load balancer phía trước, IP thực được gửi qua header này
        "Proxy-Client-IP",
        "WL-Proxy-Client-IP",
        "HTTP_X_FORWARDED_FOR",
        "HTTP_X_FORWARDED",
        "HTTP_X_CLUSTER_CLIENT_IP",
        "HTTP_CLIENT_IP",
        "HTTP_FORWARDED_FOR",
        "HTTP_FORWARDED",
        "HTTP_VIA",
        "REMOTE_ADDR"
    );

    public static String getClientIp(HttpServletRequest request) {
        for(String header : IP_HEADER_CANDIDATES){
            String ip = request.getHeader(header);
            if(ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)){
                // X-Forwarded-For có thể chứa nhiều IPs: "client, proxy1, proxy2"
                if(ip.contains(",")){
                    ip = ip.split(",")[0].trim();
                }
                log.info("Client IP extracted from header {}: {}", header, ip);
                return ip;
            }
        }

        String remoteAddr = request.getRemoteAddr(); // từ connection
        log.debug("Client IP extracted from RemoteAddr: {}", remoteAddr);
        return remoteAddr;
    }

    public static String getUserId(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        Object principal = authentication.getPrincipal();
        if(principal instanceof String){
            return (String) principal;
        }
        try {
            return authentication.getName();
        } catch (Exception e) {
            log.warn("Failed to extract user ID from authentication", e);
            return null;
        }
    }

    /**
     * Kiểm tra IP có trong whitelist không (hỗ trợ CIDR notation)
     */
    public static boolean isIpWhitelisted(String clientIp, List<String> whitelistPatterns) {
        if(whitelistPatterns == null || whitelistPatterns.isEmpty()) {
            return false;
        }
        
        for(String pattern : whitelistPatterns) {
            if (pattern.equals(clientIp)) {
                return true;
            }
            
            if(pattern.contains("/")) {
                if (matchesCidr(clientIp, pattern)) {
                    return true;
                }
            }
        }
        
        return false;
    }

    // Helper: Kiểm tra IP có thuộc CIDR range không
    // CIDR matching: Cho phép whitelist cả subnet (vd: 10.0.0.0/8 = tất cả IP bắt đầu bằng 10.x.x.x)
    private static boolean matchesCidr(String ip, String cidr) {
        try {
            String[] cidrParts = cidr.split("/");
            String network = cidrParts[0];
            int prefixLength = Integer.parseInt(cidrParts[1]);
            
            long ipLong = ipToLong(ip);
            long networkLong = ipToLong(network);
            long mask = -1L << (32 - prefixLength);
            
            return (ipLong & mask) == (networkLong & mask);
        } catch (Exception e) {
            log.warn("Failed to match CIDR pattern: {} for IP: {}", cidr, ip, e);
            return false;
        }
    }


    private static long ipToLong(String ip) {
        String[] octets = ip.split("\\.");
        return (Long.parseLong(octets[0]) << 24)
            + (Long.parseLong(octets[1]) << 16)
            + (Long.parseLong(octets[2]) << 8)
            + Long.parseLong(octets[3]);
    }


    public static String buildKey(String identifier, String endpoint) {
        return String.format("rate_limit:%s:%s", identifier, endpoint);
    }
}
