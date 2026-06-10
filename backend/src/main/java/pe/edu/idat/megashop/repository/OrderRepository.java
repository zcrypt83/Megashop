package pe.edu.idat.megashop.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import pe.edu.idat.megashop.model.Order;

import java.util.List;

public interface OrderRepository extends MongoRepository<Order, String> {
    List<Order> findTop100ByOrderByFechaPedidoDesc();
    List<Order> findByEstadoOrderByFechaPedidoDesc(String estado);
}
