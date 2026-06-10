package pe.edu.idat.megashop.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.idat.megashop.dto.OrderRequest;
import pe.edu.idat.megashop.dto.StatusRequest;
import pe.edu.idat.megashop.model.Order;
import pe.edu.idat.megashop.service.OrderService;
import pe.edu.idat.megashop.web.ApiResponse;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/pedidos")
public class OrderController {
    private final OrderService orders;

    public OrderController(OrderService orders) {
        this.orders = orders;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<Order>> list() {
        return ApiResponse.of(orders.list());
    }

    @GetMapping("/{id}")
    public ApiResponse<Order> get(@PathVariable String id) {
        return ApiResponse.of(orders.get(id));
    }

    @GetMapping("/estado/{estado}")
    public ApiResponse<List<Order>> byStatus(@PathVariable String estado) {
        return ApiResponse.of(orders.byStatus(estado));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<Order> create(@Valid @RequestBody OrderRequest request) {
        return ApiResponse.of(orders.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'REPARTIDOR')")
    public ApiResponse<Order> updateStatus(@PathVariable String id, @Valid @RequestBody StatusRequest request) {
        return ApiResponse.of(orders.updateStatus(id, request.estado()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> delete(@PathVariable String id) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(Map.of(
                "error", Map.of(
                        "message", "Use cancelacion por estado para preservar trazabilidad",
                        "status", HttpStatus.NOT_IMPLEMENTED.value()
                )
        ));
    }
}
