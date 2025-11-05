package com.example.haus.util.keycloak;

import com.example.haus.config.keycloak.KeycloakProperties;
import com.example.haus.constant.CommonConstant;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j(topic = "EMAIL-KEYCLOAK")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class KeycloakUtil {

    RestTemplate restTemplate;
    KeycloakProperties keycloakProperties;

    public void sendRestPasswordEmail(String userId) {
        final String url = keycloakProperties.serverUrl() + "admin/realm/" + keycloakProperties.realm() + "/users/" + userId + "execute-actions-email";

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, CommonConstant.BEARER_TOKEN + "" + getAdmin);
    }

    public void sendRe

}
