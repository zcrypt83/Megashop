package pe.edu.idat.megashop.security;

import org.junit.jupiter.api.Test;
import pe.edu.idat.megashop.model.User;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {
    private final JwtService jwtService = new JwtService(
            "access-secret-for-megashop-testing",
            "refresh-secret-for-megashop-testing",
            900_000,
            604_800_000
    );

    @Test
    void createsAndParsesAccessToken() {
        User user = new User();
        user.setId("000000000000000000000001");
        user.setEmail("admin@megashop.pe");
        user.setRol("admin");

        String token = jwtService.accessToken(user);
        var claims = jwtService.parseAccess(token);

        assertThat(claims.getSubject()).isEqualTo(user.getId());
        assertThat(claims.get("role", String.class)).isEqualTo("admin");
        assertThat(claims.get("email", String.class)).isEqualTo(user.getEmail());
    }
}
