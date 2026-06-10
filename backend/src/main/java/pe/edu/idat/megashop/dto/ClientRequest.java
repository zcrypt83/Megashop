package pe.edu.idat.megashop.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import pe.edu.idat.megashop.model.Address;

import java.util.List;

public record ClientRequest(
        @NotBlank @Size(min = 2) String nombre,
        @NotBlank @Email String email,
        @NotBlank @Size(min = 6) String telefono,
        List<@Valid Address> direcciones
) {
}
