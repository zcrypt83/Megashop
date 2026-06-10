package pe.edu.idat.megashop.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.idat.megashop.dto.AuthDtos.AuthResponse;
import pe.edu.idat.megashop.dto.AuthDtos.LoginRequest;
import pe.edu.idat.megashop.dto.AuthDtos.RefreshRequest;
import pe.edu.idat.megashop.dto.AuthDtos.RegisterRequest;
import pe.edu.idat.megashop.service.AuthService;
import pe.edu.idat.megashop.web.ApiResponse;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ApiResponse.of(authService.register(request));
    }

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.of(authService.login(request));
    }

    @PostMapping("/refresh")
    public ApiResponse<AuthResponse> refresh(@Valid @RequestBody RefreshRequest request) {
        return ApiResponse.of(authService.refresh(request.refreshToken()));
    }

    @PostMapping("/logout")
    public ApiResponse<Map<String, Boolean>> logout(Authentication authentication) {
        authService.logout(authentication.getName());
        return ApiResponse.of(Map.of("loggedOut", true));
    }
}
