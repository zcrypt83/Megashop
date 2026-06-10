package pe.edu.idat.megashop.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.idat.megashop.dto.ProductRequest;
import pe.edu.idat.megashop.model.Product;
import pe.edu.idat.megashop.service.ProductService;
import pe.edu.idat.megashop.web.ApiResponse;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/productos")
public class ProductController {
    private final ProductService products;

    public ProductController(ProductService products) {
        this.products = products;
    }

    @GetMapping
    public ApiResponse<List<Product>> list(
            @RequestParam(required = false, name = "q") String query,
            @RequestParam(required = false, name = "categoria") String category
    ) {
        return ApiResponse.of(products.list(query, category));
    }

    @GetMapping("/{id}")
    public ApiResponse<Product> get(@PathVariable String id) {
        return ApiResponse.of(products.get(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Product> create(@Valid @RequestBody ProductRequest request) {
        return ApiResponse.of(products.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Product> update(@PathVariable String id, @Valid @RequestBody ProductRequest request) {
        return ApiResponse.of(products.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Map<String, Boolean>> delete(@PathVariable String id) {
        return ApiResponse.of(Map.of("deleted", products.delete(id)));
    }
}
