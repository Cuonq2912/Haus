package com.example.haus.util.keycloak;

import com.example.haus.config.keycloak.KeycloakProperties;
import com.example.haus.constant.CommonConstant;
import com.example.haus.constant.ErrorMessage;
import com.example.haus.exception.KeycloakException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Component
@Slf4j(topic = "EMAIL-KEYCLOAK")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class KeycloakUtil {

    RestTemplate restTemplate;
    KeycloakProperties keycloakProperties;

    public void sendRestPasswordEmail(String userId) {
        final String url = keycloakProperties.serverUrl() + "admin/realm/" + keycloakProperties.realm() + "/users/" + userId + "/execute-actions-email";

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, CommonConstant.BEARER_TOKEN + "" + getAdminToken());
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            log.info("Reset password email sent successfully to userId = {}", userId);
        } else {
            log.error("Reset password email sent failed to userId = {}", userId);
        }
    }

    public boolean verifyEmail(String userId, boolean status) {

        final String url = keycloakProperties.serverUrl() + "admin/realms/" + keycloakProperties.realm() + "/users/" + userId;

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, CommonConstant.BEARER_TOKEN + " " + getAdminToken());
        headers.setContentType(MediaType.APPLICATION_JSON);

        String jsonBody = "{\"emailVerified\": " + status + "}";

        HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            log.info("Verification email in Keycloak successfully by userId = {}", userId);
        } else {
            log.error("Verification email in Keycloak failed by userId = {}", userId);
        }

        return true;
    }
    public boolean resetPassword(String userId, String newPassword) {
        // 1. Endpoint chuyên dụng
        final String url = keycloakProperties.serverUrl() + "admin/realms/" + keycloakProperties.realm() +
                "/users/" + userId + "/reset-password";

        // 2. Header dùng Admin Token
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + getAdminToken());
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 3. Body yêu cầu Reset Password
        String jsonBody = "{\"type\": \"password\", \"value\": \"" + newPassword + "\", \"temporary\": false}";

        HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);

        // ... Thực hiện restTemplate.exchange ...
    }


    public String getAdminToken() {
        final String adminUrl = keycloakProperties.serverUrl() + "realms/Haus/protocol/openid-connect/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "password");
        params.add("client_id", keycloakProperties.clientId());
        params.add("client_secret", keycloakProperties.clientSecret());
        params.add("scope", "openid");
        params.add("username", keycloakProperties.adminUser());
        params.add("password", keycloakProperties.adminPassword());

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(params, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(adminUrl, entity, Map.class);

        log.info("access_token = {}", (String) response.getBody().get("access_token"));

        return (String) response.getBody().get("access_token");
    }

    public String getUserId(String username) {
        String url = keycloakProperties.serverUrl() + "admin/realms/" + keycloakProperties.realm() + "/users?username=" + username;

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, CommonConstant.BEARER_TOKEN + " " + getAdminToken());

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<List> response = restTemplate.exchange(url, HttpMethod.GET, entity, List.class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null && !response.getBody().isEmpty()) {
            Map<String, Object> user = (Map<String, Object>) response.getBody().get(0);
            return (String) user.get("id");
        }

        url = keycloakProperties.serverUrl() + "admin/realm/" + keycloakProperties.realm() + "/users?email=" + username;

        entity = new HttpEntity<>(headers);
        response = restTemplate.exchange(url, HttpMethod.GET, entity, List.class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null && !response.getBody().isEmpty()) {
            Map<String, Object> user = (Map<String, Object>) response.getBody().get(0);
            return (String) user.get("id");
        }

        throw new RuntimeException("User not found in Keycloak with provided username or email");
    }

}
