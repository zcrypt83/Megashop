package pe.edu.idat.megashop.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record StatusRequest(
        @NotBlank
        @Pattern(regexp = "pendiente|confirmado|preparando|en_ruta|entregado|cancelado")
        String estado
) {
}
