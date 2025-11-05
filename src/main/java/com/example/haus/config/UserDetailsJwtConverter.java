package com.example.haus.config;

import com.example.haus.security.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UserDetailsJwtConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private static final String PREFERRED_USERNAME_CLAIM = "preferred_username";
    private static final String REALM_ACCESS = "realm_access";
    private static final String ROLES = "roles";

    private final CustomUserDetailsService userDetailsService;

    public UserDetailsJwtConverter(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        String username = jwt.getClaimAsString(PREFERRED_USERNAME_CLAIM);

        if (username == null) {
            throw new IllegalStateException("JWT missing preferred_username claim.");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        Collection<? extends GrantedAuthority> authorities = extractAuthorities(jwt);

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                authorities
        );

        authenticationToken.setDetails(jwt);

        return authenticationToken;
    }

    /**
     * Phương thức trích xuất Authorities/Roles từ Keycloak JWT claims.
     */
    private Collection<? extends GrantedAuthority> extractAuthorities(Jwt jwt) {
        Map<String, Object> realmAccessMap = jwt.getClaimAsMap(REALM_ACCESS);

        if (realmAccessMap == null || !realmAccessMap.containsKey(ROLES)) {
            return Collections.emptyList();
        }

        Object roles = realmAccessMap.get(ROLES);

        if (roles instanceof List<?> stringRoles){
            return ((List<String>) stringRoles)
                    .stream()
                    .map(s ->  new SimpleGrantedAuthority(s)) // Map Role string sang GrantedAuthority
                    .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }
}