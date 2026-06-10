package pe.edu.idat.megashop.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pe.edu.idat.megashop.model.User;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtService {
    private final SecretKey accessKey;
    private final SecretKey refreshKey;
    private final long accessExpirationMs;
    private final long refreshExpirationMs;

    public JwtService(
            @Value("${megashop.jwt.secret}") String secret,
            @Value("${megashop.jwt.refresh-secret}") String refreshSecret,
            @Value("${megashop.jwt.access-expiration-ms}") long accessExpirationMs,
            @Value("${megashop.jwt.refresh-expiration-ms}") long refreshExpirationMs
    ) {
        this.accessKey = key(secret);
        this.refreshKey = key(refreshSecret);
        this.accessExpirationMs = accessExpirationMs;
        this.refreshExpirationMs = refreshExpirationMs;
    }

    public String accessToken(User user) {
        return build(user.getId(), accessKey, accessExpirationMs)
                .claim("role", user.getRol())
                .claim("email", user.getEmail())
                .compact();
    }

    public String refreshToken(User user) {
        return build(user.getId(), refreshKey, refreshExpirationMs)
                .claim("tokenType", "refresh")
                .compact();
    }

    public Claims parseAccess(String token) {
        return Jwts.parser().verifyWith(accessKey).build().parseSignedClaims(token).getPayload();
    }

    public Claims parseRefresh(String token) {
        return Jwts.parser().verifyWith(refreshKey).build().parseSignedClaims(token).getPayload();
    }

    public long refreshExpirationMs() {
        return refreshExpirationMs;
    }

    private io.jsonwebtoken.JwtBuilder build(String subject, SecretKey key, long expirationMs) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(subject)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(expirationMs)))
                .signWith(key);
    }

    private SecretKey key(String secret) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(secret.getBytes(StandardCharsets.UTF_8));
            return Keys.hmacShaKeyFor(digest);
        } catch (Exception exception) {
            throw new IllegalStateException("No se pudo crear la llave JWT", exception);
        }
    }
}
