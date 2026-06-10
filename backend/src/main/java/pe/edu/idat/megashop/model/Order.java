package pe.edu.idat.megashop.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Document("pedidos")
@CompoundIndex(name = "status_date_idx", def = "{'estado': 1, 'fechaPedido': -1}")
@CompoundIndex(name = "delivery_idx", def = "{'repartidorId': 1, 'estado': 1}")
public class Order {
    @Id
    @JsonProperty("_id")
    private String id;
    private String clienteId;
    private String repartidorId;
    private List<OrderItem> items = new ArrayList<>();
    private Address direccionEntrega;
    private String estado;
    private PaymentInfo pago;
    private double total;
    private Instant fechaPedido;
    private Instant createdAt;
    private Instant updatedAt;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getClienteId() { return clienteId; }
    public void setClienteId(String clienteId) { this.clienteId = clienteId; }
    public String getRepartidorId() { return repartidorId; }
    public void setRepartidorId(String repartidorId) { this.repartidorId = repartidorId; }
    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }
    public Address getDireccionEntrega() { return direccionEntrega; }
    public void setDireccionEntrega(Address direccionEntrega) { this.direccionEntrega = direccionEntrega; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public PaymentInfo getPago() { return pago; }
    public void setPago(PaymentInfo pago) { this.pago = pago; }
    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
    public Instant getFechaPedido() { return fechaPedido; }
    public void setFechaPedido(Instant fechaPedido) { this.fechaPedido = fechaPedido; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
