package pe.edu.idat.megashop.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public final class AuthDtos {
    private AuthDtos() {}

    public record RegisterRequest(
            @NotBlank @Size(min = 2) String nombre,
            @NotBlank @Email String email,
            @NotBlank @Size(min = 8) String password,
            String rol
    ) {}

    public record LoginRequest(
            @NotBlank @Email String email,
            @NotBlank @Size(min = 8) String password
    ) {}

    public record RefreshRequest(@NotBlank String refreshToken) {}

    public record UserView(String id, String nombre, String email, String rol) {}

    public record AuthResponse(UserView user, String accessToken, String refreshToken) {}
}
