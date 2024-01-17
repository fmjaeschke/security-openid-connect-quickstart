package org.acme.security.openid;

import io.smallrye.jwt.build.Jwt;
import io.smallrye.jwt.build.JwtClaimsBuilder;

import java.time.Instant;

import static java.lang.String.format;

public abstract class AbstractApiIntegrationTest {

    String getAccessToken(String username) {
        return getAccessToken(username, "Pam Manager");
    }

    String getAccessToken(String username, String fullName) {
        return getAccessToken(username, fullName, null);
    }

    String getExpiredAccessToken(String username) {
        return getAccessToken(username, "Pam Manager", Instant.now().minusSeconds(10));
    }

    String getAccessToken(String username, String fullName, Instant expiresAt) {
        return getJwtClaimsBuilder(username, fullName, expiresAt).sign();
    }

    private static JwtClaimsBuilder getJwtClaimsBuilder(String username, String fullName, Instant expiresAt) {
        JwtClaimsBuilder jwtClaimsBuilder = Jwt.preferredUserName(username).claim("preferred_username", username)
                .claim("name", fullName)
                .claim("email", format("%s@redhat.com", username)).issuer("https://server.example.com")
                .audience("https://service.example.com");

        if (expiresAt != null) {
            jwtClaimsBuilder.expiresAt(expiresAt);
        }

        return jwtClaimsBuilder;
    }


}
