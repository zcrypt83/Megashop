package pe.edu.idat.megashop.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document("historial_estados")
@CompoundIndex(name = "order_history_idx", def = "{'pedidoId': 1, 'fecha': -1}")
public class StatusHistory {
    @Id
    private String id;
    private String pedidoId;
    private String estado;
    private Instant fecha;
    private String comentario;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getPedidoId() { return pedidoId; }
    public void setPedidoId(String pedidoId) { this.pedidoId = pedidoId; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public Instant getFecha() { return fecha; }
    public void setFecha(Instant fecha) { this.fecha = fecha; }
    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }
}
