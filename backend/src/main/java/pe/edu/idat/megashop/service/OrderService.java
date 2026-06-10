package pe.edu.idat.megashop.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import pe.edu.idat.megashop.dto.OrderRequest;
import pe.edu.idat.megashop.exception.NotFoundException;
import pe.edu.idat.megashop.model.Order;
import pe.edu.idat.megashop.model.OrderItem;
import pe.edu.idat.megashop.model.PaymentInfo;
import pe.edu.idat.megashop.model.StatusHistory;
import pe.edu.idat.megashop.repository.OrderRepository;
import pe.edu.idat.megashop.repository.StatusHistoryRepository;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrderService {
    private final OrderRepository orders;
    private final StatusHistoryRepository history;
    private final StringRedisTemplate redis;

    public OrderService(
            OrderRepository orders,
            StatusHistoryRepository history,
            StringRedisTemplate redis
    ) {
        this.orders = orders;
        this.history = history;
        this.redis = redis;
    }

    public List<Order> list() {
        return orders.findTop100ByOrderByFechaPedidoDesc();
    }

    public Order get(String id) {
        return orders.findById(id).orElseThrow(() -> new NotFoundException("Pedido no encontrado"));
    }

    public List<Order> byStatus(String status) {
        return orders.findByEstadoOrderByFechaPedidoDesc(status);
    }

    public Order create(OrderRequest request) {
        List<OrderItem> items = request.items().stream()
                .map(item -> new OrderItem(item.productoId(), item.nombre(), item.cantidad(), item.precioUnitario()))
                .collect(Collectors.toList());
        double total = items.stream().mapToDouble(item -> item.cantidad() * item.precioUnitario()).sum();
        Instant now = Instant.now();

        Order order = new Order();
        order.setClienteId(request.clienteId());
        order.setRepartidorId(request.repartidorId());
        order.setItems(items);
        order.setDireccionEntrega(request.direccionEntrega());
        order.setEstado("pendiente");
        order.setPago(new PaymentInfo(request.pago().metodo(), request.pago().estado()));
        order.setTotal(total);
        order.setFechaPedido(now);
        order.setCreatedAt(now);
        order.setUpdatedAt(now);
        Order saved = orders.save(order);

        saveHistory(saved.getId(), "pendiente", "Pedido creado");
        redis.opsForHash().putAll("order_status:" + saved.getId(), Map.of(
                "estado", "pendiente",
                "total", String.valueOf(total),
                "updatedAt", now.toString()
        ));
        redis.opsForList().leftPush("client_orders:" + request.clienteId(), saved.getId());
        return saved;
    }

    public Order updateStatus(String id, String status) {
        Order order = get(id);
        order.setEstado(status);
        order.setUpdatedAt(Instant.now());
        Order saved = orders.save(order);
        saveHistory(id, status, "Estado actualizado a " + status);
        redis.opsForHash().putAll("order_status:" + id, Map.of(
                "estado", status,
                "updatedAt", Instant.now().toString()
        ));
        return saved;
    }

    private void saveHistory(String orderId, String status, String comment) {
        StatusHistory item = new StatusHistory();
        item.setPedidoId(orderId);
        item.setEstado(status);
        item.setFecha(Instant.now());
        item.setComentario(comment);
        history.save(item);
    }
}
