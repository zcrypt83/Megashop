package pe.edu.idat.megashop.model;

public record OrderItem(
        String productoId,
        String nombre,
        int cantidad,
        double precioUnitario
) {
}
