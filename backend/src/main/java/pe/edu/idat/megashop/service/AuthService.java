package pe.edu.idat.megashop.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pe.edu.idat.megashop.dto.AuthDtos.AuthResponse;
import pe.edu.idat.megashop.dto.AuthDtos.LoginRequest;
import pe.edu.idat.megashop.dto.AuthDtos.RegisterRequest;
import pe.edu.idat.megashop.dto.AuthDtos.UserView;
import pe.edu.idat.megashop.exception.ConflictException;
import pe.edu.idat.megashop.exception.NotFoundException;
import pe.edu.idat.megashop.exception.UnauthorizedException;
import pe.edu.idat.megashop.model.User;
import pe.edu.idat.megashop.repository.UserRepository;
import pe.edu.idat.megashop.security.JwtService;

import java.time.Duration;
import java.time.Instant;
import java.util.Set;

@Service
public class AuthService {
    private static final Set<String> ROLES = Set.of("admin", "cliente", "repartidor");
    private final UserRepository users;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final StringRedisTemplate redis;

    public AuthService(
            UserRepository users,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            StringRedisTemplate redis
    ) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.redis = redis;
    }

    public AuthResponse register(RegisterRequest request) {
        String email = request.email().trim().toLowerCase();
        if (users.existsByEmail(email)) throw new ConflictException("El correo ya esta registrado");

        User user = new User();
        user.setNombre(request.nombre().trim());
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setRol(ROLES.contains(request.rol()) ? request.rol() : "cliente");
        user.setActivo(true);
        user.setCreatedAt(Instant.now());
        user.setUpdatedAt(Instant.now());
        return issueTokens(users.save(user));
    }

    public AuthResponse login(LoginRequest request) {
        User user = users.findByEmail(request.email().trim().toLowerCase())
                .orElseThrow(() -> new UnauthorizedException("Credenciales invalidas"));
        if (!user.isActivo() || !passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new UnauthorizedException("Credenciales invalidas");
        }
        return issueTokens(user);
    }

    public AuthResponse refresh(String refreshToken) {
        String userId;
        try {
            userId = jwtService.parseRefresh(refreshToken).getSubject();
        } catch (Exception exception) {
            throw new UnauthorizedException("Refresh token invalido");
        }
        String saved = redis.opsForValue().get(sessionKey(userId));
        if (!refreshToken.equals(saved)) throw new UnauthorizedException("Sesion expirada");
        User user = users.findById(userId).orElseThrow(() -> new NotFoundException("Usuario no encontrado"));
        return issueTokens(user);
    }

    public void logout(String userId) {
        redis.delete(sessionKey(userId));
    }

    private AuthResponse issueTokens(User user) {
        String accessToken = jwtService.accessToken(user);
        String refreshToken = jwtService.refreshToken(user);
        redis.opsForValue().set(
                sessionKey(user.getId()),
                refreshToken,
                Duration.ofMillis(jwtService.refreshExpirationMs())
        );
        return new AuthResponse(
                new UserView(user.getId(), user.getNombre(), user.getEmail(), user.getRol()),
                accessToken,
                refreshToken
        );
    }

    private String sessionKey(String userId) {
        return "session:" + userId;
    }
}
