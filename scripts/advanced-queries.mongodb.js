use("megashop");

db.productos.find(
  { categoriaId: "tecnologia", activo: true, stock: { $gt: 0 } },
  { nombre: 1, precio: 1, stock: 1 }
).sort({ precio: 1 }).limit(20);

db.pedidos.aggregate([
  { $match: { estado: { $ne: "cancelado" } } },
  { $unwind: "$items" },
  { $group: { _id: "$items.productoId", unidades: { $sum: "$items.cantidad" }, ventas: { $sum: { $multiply: ["$items.cantidad", "$items.precioUnitario"] } } } },
  { $sort: { ventas: -1 } },
  { $limit: 10 }
]);

db.pedidos.aggregate([
  { $lookup: { from: "clientes", localField: "clienteId", foreignField: "_id", as: "cliente" } },
  { $unwind: "$cliente" },
  { $group: { _id: "$cliente.email", cliente: { $first: "$cliente.nombre" }, totalCompras: { $sum: "$total" }, pedidos: { $sum: 1 } } },
  { $sort: { totalCompras: -1 } },
  { $limit: 10 }
]);

db.historial_estados.aggregate([
  { $sort: { pedidoId: 1, fecha: 1 } },
  { $group: { _id: "$pedidoId", estados: { $push: { estado: "$estado", fecha: "$fecha" } } } },
  { $limit: 20 }
]);
