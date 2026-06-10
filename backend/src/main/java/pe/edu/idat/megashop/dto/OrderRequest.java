package pe.edu.idat.megashop.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import pe.edu.idat.megashop.model.Address;

import java.util.List;

public record OrderRequest(
        @NotBlank String clienteId,
        String repartidorId,
        @Valid Address direccionEntrega,
        @NotEmpty List<@Valid ItemRequest> items,
        @Valid PaymentRequest pago
) {
    public record ItemRequest(
            @NotBlank String productoId,
            @NotBlank String nombre,
            @Positive int cantidad,
            @Positive double precioUnitario
    ) {}

    public record PaymentRequest(@NotBlank String metodo, @NotBlank String estado) {}
}
