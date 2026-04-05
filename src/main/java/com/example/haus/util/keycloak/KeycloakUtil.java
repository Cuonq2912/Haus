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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.haus.constant.CommonConstant.ADMIN_REALM;
import static com.example.haus.constant.CommonConstant.USER_END_POINT;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Component
@Slf4j(topic = "EMAIL-KEYCLOAK")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class KeycloakUtil {

    RestTemplate restTemplate;
    KeycloakProperties keycloakProperties;

    private String serverUrl() {
        String baseUrl = keycloakProperties.serverUrl();
        if (baseUrl == null || baseUrl.isBlank()) {
            throw new KeycloakException("Keycloak server URL is not configured");
        }
        return baseUrl.endsWith("/") ? baseUrl : baseUrl + "/";
    }

    public void sendResetPasswordEmail(String userId) {
        final String url = serverUrl() + ADMIN_REALM + keycloakProperties.realm() + USER_END_POINT + userId
                + "/execute-actions-email";

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, CommonConstant.BEARER_TOKEN + " " + getAdminToken());
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Gửi action UPDATE_PASSWORD
        HttpEntity<List<String>> entity = new HttpEntity<>(List.of("UPDATE_PASSWORD"), headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            log.info("Reset password email sent successfully to userId = {}", userId);
        } else {
            log.error("Reset password email sent failed to userId = {}, status = {}, body = {}", userId,
                    response.getStatusCode(), response.getBody());
            throw new KeycloakException(ErrorMessage.Auth.ERR_CAN_NOT_SEND_RESET_PASSWORD_EMAIL);
        }
    }

    public boolean verifyEmail(String userId, boolean status) {
        final String url = serverUrl() + ADMIN_REALM + keycloakProperties.realm() + USER_END_POINT + userId;

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, CommonConstant.BEARER_TOKEN + " " + getAdminToken());
        headers.setContentType(MediaType.APPLICATION_JSON);

        String jsonBody = "{\"emailVerified\": " + status + "}";

        HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Verification email in Keycloak successfully by userId = {}", userId);
                return true;
            } else {
                log.error("Verification email in Keycloak failed by userId = {}, status = {}, body = {}", userId,
                        response.getStatusCode(), response.getBody());
                return false;
            }
        } catch (Exception ex) {
            log.error("Error verifying email in Keycloak for userId = {}", userId, ex);
            return false;
        }
    }

    public boolean resetPassword(String userId, String newPassword) {
        final String url = serverUrl() + "admin/realms/" + keycloakProperties.realm() + "/users/" + userId
                + "/reset-password";

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + getAdminToken());
        headers.setContentType(MediaType.APPLICATION_JSON);

        String jsonBody = """
                {
                  "type": "password",
                  "value": "%s",
                  "temporary": false
                }
                """.formatted(newPassword);

        HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Password reset successfully in Keycloak for userId = {}", userId);
                return true;
            } else {
                log.error("Reset password failed in Keycloak for userId = {}, status = {}, body = {}", userId,
                        response.getStatusCode(), response.getBody());
                return false;
            }
        } catch (Exception ex) {
            log.error("Error calling Keycloak reset password for userId = {}", userId, ex);
            return false;
        }
    }

    public void deleteUser(String userId) {
        final String url = serverUrl() + "admin/realms/" + keycloakProperties.realm() + "/users/" + userId;

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, CommonConstant.BEARER_TOKEN + " " + getAdminToken());

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.DELETE, entity, String.class);
            if (response.getStatusCode().is2xxSuccessful() || response.getStatusCode() == HttpStatus.NO_CONTENT) {
                log.info("Deleted Keycloak user {}", userId);
                return;
            }

            log.error("Failed to delete Keycloak user {}, status = {}, body = {}", userId, response.getStatusCode(),
                    response.getBody());
            throw new KeycloakException("Failed to delete user in Keycloak");
        } catch (Exception ex) {
            log.error("Error deleting Keycloak user {}", userId, ex);
            throw new KeycloakException("Failed to delete user in Keycloak");
        }
    }

    public String getAdminToken() {
        final String adminUrl = serverUrl() + "realms/" + keycloakProperties.realm() + "/protocol/openid-connect/token";

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

        return (String) response.getBody().get("access_token");
    }

    public String getUserId(String username) {
        String url = serverUrl() + "admin/realms/" + keycloakProperties.realm() + "/users?username=" + username;

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, CommonConstant.BEARER_TOKEN + " " + getAdminToken());

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<List> response = restTemplate.exchange(url, HttpMethod.GET, entity, List.class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null && !response.getBody().isEmpty()) {
            Map<String, Object> user = (Map<String, Object>) response.getBody().get(0);
            return (String) user.get("id");
        }

        url = serverUrl() + "admin/realms/" + keycloakProperties.realm() + "/users?email=" + username;

        entity = new HttpEntity<>(headers);
        response = restTemplate.exchange(url, HttpMethod.GET, entity, List.class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null && !response.getBody().isEmpty()) {
            Map<String, Object> user = (Map<String, Object>) response.getBody().get(0);
            return (String) user.get("id");
        }

        throw new RuntimeException("User not found in Keycloak with provided username or email");
    }

    public String resolveUsername(String usernameOrEmail) {
        String url = serverUrl() + "admin/realms/" + keycloakProperties.realm() + "/users?username=" + usernameOrEmail;

        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, CommonConstant.BEARER_TOKEN + " " + getAdminToken());

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<List> response = restTemplate.exchange(url, HttpMethod.GET, entity, List.class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null && !response.getBody().isEmpty()) {
            Map<String, Object> user = (Map<String, Object>) response.getBody().get(0);
            return (String) user.get("username");
        }

        url = serverUrl() + "admin/realms/" + keycloakProperties.realm() + "/users?email=" + usernameOrEmail;
        response = restTemplate.exchange(url, HttpMethod.GET, entity, List.class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null && !response.getBody().isEmpty()) {
            Map<String, Object> user = (Map<String, Object>) response.getBody().get(0);
            return (String) user.get("username");
        }

        return usernameOrEmail;
    }

    public void assignRoleToUser(String userId, String roleId) {

        String url = serverUrl() + "admin/realms/" + keycloakProperties.realm() + "/users/" + userId
                + "/role-mappings/realm";

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, CommonConstant.BEARER_TOKEN + " " + getAdminToken());
        headers.setContentType(MediaType.APPLICATION_JSON);

        // JSON body: [{ "id": roleId, "name": "USER" }]
        Map<String, Object> role = new HashMap<>();
        role.put("id", roleId);
        role.put("name", "USER");

        List<Map<String, Object>> roles = List.of(role);

        HttpEntity<List<Map<String, Object>>> entity = new HttpEntity<>(roles, headers);

        try {
            restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            log.info("Assigned role USER to user {}", userId);
        } catch (Exception ex) {
            log.error("Failed to assign role: {}", ex.getMessage());
            throw new KeycloakException("Failed to assign role to user in Keycloak");
        }
    }

    public String getRoleId(String roleName) {

        String url = serverUrl() + "admin/realms/" + keycloakProperties.realm() + "/roles/" + roleName;

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, CommonConstant.BEARER_TOKEN + " " + getAdminToken());

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        // Dùng Map để Spring tự parse JSON trả về
        var response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new KeycloakException("Failed to get role from Keycloak");
        }

        var role = response.getBody();

        if (role == null) {
            return null;
        }

        return (String) role.get("id");
    }

}
