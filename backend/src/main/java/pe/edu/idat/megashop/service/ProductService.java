package pe.edu.idat.megashop.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import pe.edu.idat.megashop.dto.ProductRequest;
import pe.edu.idat.megashop.exception.NotFoundException;
import pe.edu.idat.megashop.model.Product;
import pe.edu.idat.megashop.repository.ProductRepository;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Service
public class ProductService {
    private final ProductRepository products;
    private final StringRedisTemplate redis;
    private final ObjectMapper objectMapper;

    public ProductService(ProductRepository products, StringRedisTemplate redis, ObjectMapper objectMapper) {
        this.products = products;
        this.redis = redis;
        this.objectMapper = objectMapper;
    }

    public List<Product> list(String query, String category) {
        String q = query == null ? "" : query.trim();
        String c = category == null ? "" : category.trim();
        String cacheKey = "products:q=" + q + ":category=" + c;
        String cached = redis.opsForValue().get(cacheKey);
        if (cached != null) {
            try {
                return objectMapper.readValue(cached, new TypeReference<>() {});
            } catch (Exception ignored) {
                redis.delete(cacheKey);
            }
        }

        List<Product> result;
        if (!q.isBlank() && !c.isBlank()) {
            result = products.findTop100ByCategoriaIdAndNombreContainingIgnoreCaseOrderByNombreAsc(c, q);
        } else if (!q.isBlank()) {
            result = products.findTop100ByNombreContainingIgnoreCaseOrderByNombreAsc(q);
        } else if (!c.isBlank()) {
            result = products.findTop100ByCategoriaIdOrderByNombreAsc(c);
        } else {
            result = products.findTop100ByOrderByNombreAsc();
        }
        try {
            redis.opsForValue().set(cacheKey, objectMapper.writeValueAsString(result), Duration.ofSeconds(120));
        } catch (Exception ignored) {
            // MongoDB sigue siendo la fuente de verdad si el cache no puede serializarse.
        }
        return result;
    }

    public Product get(String id) {
        Product product = products.findById(id).orElseThrow(() -> new NotFoundException("Producto no encontrado"));
        redis.opsForZSet().incrementScore("most_viewed_products", id, 1);
        return product;
    }

    public Product create(ProductRequest request) {
        Product product = new Product();
        apply(product, request);
        product.setCreatedAt(Instant.now());
        product.setUpdatedAt(Instant.now());
        Product saved = products.save(product);
        clearCache();
        return saved;
    }

    public Product update(String id, ProductRequest request) {
        Product product = getWithoutTracking(id);
        apply(product, request);
        product.setUpdatedAt(Instant.now());
        Product saved = products.save(product);
        clearCache();
        return saved;
    }

    public boolean delete(String id) {
        if (!products.existsById(id)) return false;
        products.deleteById(id);
        clearCache();
        return true;
    }

    private Product getWithoutTracking(String id) {
        return products.findById(id).orElseThrow(() -> new NotFoundException("Producto no encontrado"));
    }

    private void apply(Product product, ProductRequest request) {
        product.setNombre(request.nombre());
        product.setDescripcion(request.descripcion());
        product.setCategoriaId(request.categoriaId());
        product.setPrecio(request.precio());
        product.setStock(request.stock());
        product.setImagenUrl(request.imagenUrl());
        product.setActivo(request.activo() == null || request.activo());
        product.setTags(request.tags() == null ? List.of() : request.tags());
    }

    private void clearCache() {
        var keys = redis.keys("products:*");
        if (keys != null && !keys.isEmpty()) redis.delete(keys);
    }
}
