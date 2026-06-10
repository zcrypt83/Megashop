package pe.edu.idat.megashop.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.util.List;

public record ProductRequest(
        @NotBlank @Size(min = 2) String nombre,
        @NotBlank @Size(min = 5) String descripcion,
        @NotBlank String categoriaId,
        @Positive double precio,
        @PositiveOrZero int stock,
        String imagenUrl,
        Boolean activo,
        List<String> tags
) {
}
