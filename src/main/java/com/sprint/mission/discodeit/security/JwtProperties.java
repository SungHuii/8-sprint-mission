package com.sprint.mission.discodeit.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(
    String secret,
    long accessTokenExpiry,
    long refreshTokenExpiry
) {

}
