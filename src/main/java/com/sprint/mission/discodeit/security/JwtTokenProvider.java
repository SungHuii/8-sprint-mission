package com.sprint.mission.discodeit.security;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTClaimsSet.Builder;
import com.nimbusds.jwt.SignedJWT;
import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.enums.AuthErrorCode;
import jakarta.servlet.http.Cookie;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {

  public static final String REFRESH_TOKEN_COOKIE_NAME = "REFRESH_TOKEN";
  private static final String ACCESS_TOKEN_CLAIM_KEY = "userId";

  private static final String SECRET_KEY = "${jwt.secret}";
  private static final String ACCESS_TOKEN_EXPIRY = "${jwt.access-token-expiry:3600000}";
  private static final String REFRESH_TOKEN_EXPIRY = "${jwt.refresh-token-expiry:604800000}";

  @Value(SECRET_KEY)
  private String secretKey;
  @Value(ACCESS_TOKEN_EXPIRY)
  private long accessTokenExpiry;
  @Value(REFRESH_TOKEN_EXPIRY)
  private long refreshTokenExpiry;


  // HMAC 서명 알고리즘
  private final JWSAlgorithm algorithm = JWSAlgorithm.HS256;

  // 액세스 토큰 발급
  public String generateAccessToken(UUID userId, String username) {

    Map<String, Object> extraClaims = Map.of(ACCESS_TOKEN_CLAIM_KEY, userId.toString());

    try {
      return generateToken(username, accessTokenExpiry, extraClaims);
    } catch (JOSEException e) {
      throw new DiscodeitException(AuthErrorCode.AUTHENTICATION_FAILED, e.getMessage());
    }
  }

  // 리프레시 토큰 발급
  public String generateRefreshToken(UUID userId) {

    try {
      return generateToken(userId.toString(), refreshTokenExpiry, null);
    } catch (JOSEException e) {
      throw new DiscodeitException(AuthErrorCode.AUTHENTICATION_FAILED, e.getMessage());
    }
  }

  // 토큰에서 username 추출 (AccessToken)
  public String extractUsername(String token) {

    try {
      return SignedJWT.parse(token).getJWTClaimsSet().getSubject();
    } catch (Exception e) {
      throw new DiscodeitException(AuthErrorCode.INVALID_TOKEN, e.getMessage());
    }
  }

  // 토큰에서 userId 추출 (RefreshToken)
  public UUID extractUserId(String token) {

    try {
      String subject = SignedJWT.parse(token).getJWTClaimsSet().getSubject();
      return UUID.fromString(subject);
    } catch (Exception e) {
      throw new DiscodeitException(AuthErrorCode.INVALID_TOKEN, e.getMessage());
    }
  }

  // 토큰 만료 시각 추출
  public Date extractExpiration(String token) {

    try {
      return SignedJWT.parse(token).getJWTClaimsSet().getExpirationTime();
    } catch (Exception e) {
      throw new DiscodeitException(AuthErrorCode.INVALID_TOKEN, e.getMessage());
    }
  }

  // RefreshToken을 HttpOnly 쿠키로 만듬
  public Cookie buildRefreshTokenCookie(String refreshToken) {

    Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, refreshToken);
    cookie.setHttpOnly(true);
    cookie.setPath("/");
    cookie.setMaxAge((int) (refreshTokenExpiry / 1000));
    return cookie;
  }

  // 빈 RefreshToken 쿠키 -> 삭제용도
  public Cookie buildExpiredRefreshTokenCookie() {

    Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, "");
    cookie.setHttpOnly(true);
    cookie.setPath("/");
    cookie.setMaxAge(0);
    return cookie;
  }

  // 토큰 유효성 검증
  public boolean validateToken(String token) {

    try {
      SignedJWT signedJWT = SignedJWT.parse(token);
      JWSVerifier verifier = new MACVerifier(secretKey.getBytes());
      return signedJWT.verify(verifier) &&
          signedJWT.getJWTClaimsSet().getExpirationTime().after(new Date());
    } catch (Exception e) {
      return false;
    }
  }

  // 토큰 발급 공통 로직 헬퍼 메서드
  private String generateToken(String subject, long expiryMillis, Map<String, Object> extraClaims)
      throws JOSEException {

    Instant now = Instant.now();
    JWSSigner signer = new MACSigner(secretKey.getBytes());

    JWTClaimsSet.Builder builder = new Builder()
        .subject(subject)
        .issueTime(Date.from(now))
        .expirationTime(Date.from(now.plusMillis(expiryMillis)));

    // 추가 클레임이 들어오면 Builder에 넣어줌
    if (extraClaims != null && !extraClaims.isEmpty()) {
      extraClaims.forEach(builder::claim);
    }

    SignedJWT signedJWT = new SignedJWT(new JWSHeader(algorithm), builder.build());
    signedJWT.sign(signer);

    return signedJWT.serialize();
  }
}
