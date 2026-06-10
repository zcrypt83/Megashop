package pe.edu.idat.megashop.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import pe.edu.idat.megashop.model.Order;
import pe.edu.idat.megashop.repository.ClientRepository;
import pe.edu.idat.megashop.repository.OrderRepository;
import pe.edu.idat.megashop.repository.ProductRepository;

import java.time.Duration;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DashboardService {
    private final ClientRepository clients;
    private final ProductRepository products;
    private final OrderRepository orders;
    private final StringRedisTemplate redis;
    private final ObjectMapper objectMapper;

    public DashboardService(
            ClientRepository clients,
            ProductRepository products,
            OrderRepository orders,
            StringRedisTemplate redis,
            ObjectMapper objectMapper
    ) {
        this.clients = clients;
        this.products = products;
        this.orders = orders;
        this.redis = redis;
        this.objectMapper = objectMapper;
    }

    public Map<String, Object> summary() {
        String key = "dashboard:resumen";
        String cached = redis.opsForValue().get(key);
        if (cached != null) {
            try {
                return objectMapper.readValue(cached, new TypeReference<>() {});
            } catch (Exception ignored) {
                redis.delete(key);
            }
        }
        List<Order> allOrders = orders.findAll();
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("clientes", clients.count());
        result.put("productos", products.count());
        result.put("pedidos", allOrders.size());
        result.put("ventas", allOrders.stream().mapToDouble(Order::getTotal).sum());
        result.put("hotProducts", redis.opsForZSet().reverseRangeWithScores("most_viewed_products", 0, 4));
        try {
            redis.opsForValue().set(key, objectMapper.writeValueAsString(result), Duration.ofSeconds(60));
        } catch (Exception ignored) {
        }
        return result;
    }

    public List<Map<String, Object>> sales() {
        return orders.findAll().stream()
                .filter(order -> !"cancelado".equals(order.getEstado()))
                .collect(Collectors.groupingBy(
                        order -> order.getFechaPedido().atZone(ZoneOffset.UTC).toLocalDate().toString(),
                        Collectors.toList()
                ))
                .entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("_id", entry.getKey());
                    row.put("total", entry.getValue().stream().mapToDouble(Order::getTotal).sum());
                    row.put("pedidos", entry.getValue().size());
                    return row;
                })
                .limit(30)
                .toList();
    }

    public List<Map<String, Object>> topClients() {
        Map<String, List<Order>> grouped = orders.findAll().stream()
                .collect(Collectors.groupingBy(Order::getClienteId));
        List<Map<String, Object>> result = new ArrayList<>();
        grouped.forEach((clientId, clientOrders) -> clients.findById(clientId).ifPresent(client -> {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("nombre", client.getNombre());
            row.put("email", client.getEmail());
            row.put("totalCompras", clientOrders.stream().mapToDouble(Order::getTotal).sum());
            row.put("pedidos", clientOrders.size());
            result.add(row);
        }));
        result.sort(Comparator.comparingDouble(row -> -((Number) row.get("totalCompras")).doubleValue()));
        return result.stream().limit(10).toList();
    }
}
