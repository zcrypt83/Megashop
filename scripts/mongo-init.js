db = db.getSiblingDB("megashop");

db.createCollection("clientes");
db.createCollection("usuarios");
db.createCollection("productos");
db.createCollection("categorias");
db.createCollection("pedidos");
db.createCollection("pagos");
db.createCollection("repartidores");
db.createCollection("historial_estados");
db.createCollection("notificaciones");

db.usuarios.createIndex({ email: 1 }, { unique: true });
db.clientes.createIndex({ email: 1 }, { unique: true });
db.productos.createIndex({ categoriaId: 1, activo: 1 });
db.productos.createIndex({ nombre: "text", descripcion: "text", tags: "text" });
db.pedidos.createIndex({ fechaPedido: -1 });
db.pedidos.createIndex({ estado: 1, fechaPedido: -1 });
db.pedidos.createIndex({ repartidorId: 1, estado: 1 });
db.historial_estados.createIndex({ pedidoId: 1, fecha: -1 });
db.notificaciones.createIndex({ expiresAt: 1 }, { expireAfterSeconds: 0 });

db.categorias.insertMany([
  { _id: "tecnologia", nombre: "Tecnologia" },
  { _id: "hogar", nombre: "Hogar" },
  { _id: "moda", nombre: "Moda" },
  { _id: "supermercado", nombre: "Supermercado" },
  { _id: "deportes", nombre: "Deportes" }
]);
