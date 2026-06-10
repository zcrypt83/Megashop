import fs from "node:fs";
import path from "node:path";
import { fileURLToPath } from "node:url";

const __dirname = path.dirname(fileURLToPath(import.meta.url));
const outDir = path.join(__dirname, "..", "data");
fs.mkdirSync(outDir, { recursive: true });

const firstNames = ["Ana", "Luis", "Maria", "Carlos", "Valeria", "Jorge", "Lucia", "Diego", "Rosa", "Mateo"];
const lastNames = ["Torres", "Rojas", "Vega", "Castillo", "Mendoza", "Flores", "Salazar", "Campos", "Quispe", "Reyes"];
const categories = [
  { _id: "tecnologia", nombre: "Tecnologia" },
  { _id: "hogar", nombre: "Hogar" },
  { _id: "moda", nombre: "Moda" },
  { _id: "supermercado", nombre: "Supermercado" },
  { _id: "deportes", nombre: "Deportes" }
];
const estados = ["pendiente", "confirmado", "preparando", "en_ruta", "entregado", "cancelado"];
const metodos = ["tarjeta", "yape", "plin", "efectivo"];

function pick(list, index) {
  return list[index % list.length];
}

function id(prefix, index) {
  const offsets = { cli: 100000, rep: 200000, prd: 300000, ped: 400000, pag: 500000, his: 600000 };
  return String((offsets[prefix] || 900000) + index).toString(16).padStart(24, "0");
}

const clientes = Array.from({ length: 100 }, (_, index) => {
  const nombre = `${pick(firstNames, index)} ${pick(lastNames, index * 3)}`;
  return {
    _id: id("cli", index + 1),
    nombre,
    email: `${nombre.toLowerCase().replaceAll(" ", ".")}.${index + 1}@megashop.pe`,
    telefono: `9${String(10000000 + index * 731).slice(0, 8)}`,
    direcciones: [
      {
        alias: "Casa",
        linea1: `Av. Central ${100 + index}`,
        distrito: pick(["Miraflores", "Surco", "San Miguel", "Los Olivos", "Ate"], index),
        ciudad: "Lima",
        referencia: "Referencia validada para delivery"
      }
    ],
    createdAt: new Date().toISOString()
  };
});

const repartidores = Array.from({ length: 50 }, (_, index) => ({
  _id: id("rep", index + 1),
  nombre: `${pick(firstNames, index + 4)} ${pick(lastNames, index + 2)}`,
  telefono: `9${String(20000000 + index * 419).slice(0, 8)}`,
  placa: `MS-${String(100 + index)}`,
  zona: pick(["Norte", "Centro", "Sur", "Este", "Oeste"], index),
  estado: pick(["disponible", "ocupado", "descanso"], index)
}));

const productos = Array.from({ length: 200 }, (_, index) => {
  const categoria = pick(categories, index);
  return {
    _id: id("prd", index + 1),
    nombre: `${categoria.nombre} Producto ${index + 1}`,
    descripcion: `Producto optimizado para venta online con inventario trazable ${index + 1}.`,
    categoriaId: categoria._id,
    precio: Number((12 + ((index * 19) % 480) + 0.9).toFixed(2)),
    stock: 10 + ((index * 7) % 140),
    activo: index % 17 !== 0,
    tags: [categoria._id, pick(["nuevo", "hot", "promo", "regular"], index)],
    createdAt: new Date(Date.now() - index * 86400000).toISOString()
  };
});

const pedidos = Array.from({ length: 500 }, (_, index) => {
  const cliente = clientes[index % clientes.length];
  const repartidor = repartidores[index % repartidores.length];
  const itemCount = 1 + (index % 4);
  const items = Array.from({ length: itemCount }, (_, itemIndex) => {
    const product = productos[(index + itemIndex * 11) % productos.length];
    const cantidad = 1 + ((index + itemIndex) % 3);
    return {
      productoId: product._id,
      nombre: product.nombre,
      cantidad,
      precioUnitario: product.precio
    };
  });
  const total = Number(items.reduce((sum, item) => sum + item.cantidad * item.precioUnitario, 0).toFixed(2));
  return {
    _id: id("ped", index + 1),
    clienteId: cliente._id,
    repartidorId: repartidor._id,
    items,
    direccionEntrega: cliente.direcciones[0],
    estado: pick(estados, index),
    pago: {
      metodo: pick(metodos, index),
      estado: index % 9 === 0 ? "pendiente" : "pagado"
    },
    total,
    fechaPedido: new Date(Date.now() - index * 3600000).toISOString()
  };
});

const pagos = pedidos.slice(0, 150).map((pedido, index) => ({
  _id: id("pag", index + 1),
  pedidoId: pedido._id,
  metodo: pedido.pago.metodo,
  estado: pedido.pago.estado,
  monto: pedido.total,
  fecha: pedido.fechaPedido
}));

const historial_estados = pedidos.flatMap((pedido, index) => {
  const flow = estados.slice(0, Math.max(1, estados.indexOf(pedido.estado) + 1));
  return flow.map((estado, step) => ({
    _id: id("his", index * 10 + step + 1),
    pedidoId: pedido._id,
    estado,
    fecha: new Date(new Date(pedido.fechaPedido).getTime() + step * 900000).toISOString(),
    comentario: `Transicion ${estado}`
  }));
});

const usuarios = [
  {
    nombre: "Admin Megashop",
    email: "admin@megashop.pe",
    passwordHash: process.env.DEMO_PASSWORD_HASH || "REPLACE_BEFORE_IMPORTING",
    rol: "admin",
    activo: true,
    createdAt: new Date().toISOString()
  }
];

const collections = { clientes, repartidores, categorias: categories, productos, pedidos, pagos, historial_estados, usuarios };

for (const [name, rows] of Object.entries(collections)) {
  fs.writeFileSync(path.join(outDir, `${name}.generated.json`), JSON.stringify(rows, null, 2));
}

console.log(`Dataset generado en ${outDir}`);
console.log(`Total documentos: ${Object.values(collections).reduce((sum, rows) => sum + rows.length, 0)}`);
