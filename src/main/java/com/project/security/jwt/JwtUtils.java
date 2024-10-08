package com.project.security.jwt;

import com.project.entity.concretes.user.User;
import com.project.security.service.UserDetailsImpl;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Date;

@Component
public class JwtUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtUtils.class);

    private static final int RESET_CODE_LENGTH = 6;
    private static final String RESET_CODE_CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    @Value("${backendapi.app.jwtExpirationMs}")
    private long jwtExpirationMs;

    @Value("${backendapi.app.jwtSecret}")
    private String jwtSecret;

    public String generateJwtToken(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return generateTokenFromUsername(userDetails.getUsername());
    }

    public String generateTokenFromUsername(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret.getBytes()) // Ensure the secret is in bytes
                .compact();
    }

    public boolean validateJwtToken(String jwtToken) {
        try {
            Jwts.parser()
                    .setSigningKey(jwtSecret.getBytes()) // Anahtarın byte dizisi olarak kullanılması
                    .parseClaimsJws(jwtToken);
            return true;
        } catch (ExpiredJwtException e) {
            LOGGER.error("Jwt token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            LOGGER.error("Jwt token is unsupported: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            LOGGER.error("Jwt token is invalid: {}", e.getMessage());
        } catch (SignatureException e) {
            LOGGER.error("Jwt Signature is invalid: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            LOGGER.error("Jwt is empty: {}", e.getMessage());
        }
        return false;
    }

    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser()
                .setSigningKey(jwtSecret.getBytes()) // Anahtarın byte dizisi olarak kullanılması
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public String generateResetCode(User user) {
        SecureRandom random = new SecureRandom();
        StringBuilder resetCode = new StringBuilder(RESET_CODE_LENGTH);

        for (int i = 0; i < RESET_CODE_LENGTH; i++) {
            int index = random.nextInt(RESET_CODE_CHARACTERS.length());
            resetCode.append(RESET_CODE_CHARACTERS.charAt(index));
        }

        // Generate JWT token using the reset code
        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("resetCode", resetCode.toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + jwtExpirationMs)) // Expiration can be adjusted
                .signWith(SignatureAlgorithm.HS512, jwtSecret.getBytes())
                .compact();
    }
}
