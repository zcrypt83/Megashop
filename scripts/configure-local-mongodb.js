import fs from "node:fs";
import path from "node:path";
import { fileURLToPath } from "node:url";
import { MongoClient } from "mongodb";

const root = path.resolve(path.dirname(fileURLToPath(import.meta.url)), "..");
const dataDir = path.join(root, "data");

function requiredEnvironment(name) {
  const value = process.env[name];
  if (!value) throw new Error(`Falta la variable de entorno ${name}.`);
  return value;
}

const adminUser = requiredEnvironment("MONGO_ADMIN_USER");
const adminPassword = requiredEnvironment("MONGO_ADMIN_PASSWORD");
const authenticatedUri =
  process.env.MONGO_AUTH_URI ||
  `mongodb://${adminUser}:${adminPassword}@localhost:27017/?authSource=admin`;
const unauthenticatedUri = "mongodb://localhost:27017";

const collections = [
  "clientes",
  "usuarios",
  "productos",
  "categorias",
  "pedidos",
  "pagos",
  "repartidores",
  "historial_estados"
];

async function connect() {
  if (process.env.MONGO_BOOTSTRAP_URI) {
    const configuredClient = new MongoClient(process.env.MONGO_BOOTSTRAP_URI);
    await configuredClient.connect();
    return configuredClient;
  }

  const authenticatedClient = new MongoClient(authenticatedUri);
  try {
    await authenticatedClient.connect();
    await authenticatedClient.db("admin").command({ ping: 1 });
    return authenticatedClient;
  } catch {
    await authenticatedClient.close();
    const bootstrapClient = new MongoClient(unauthenticatedUri);
    await bootstrapClient.connect();
    return bootstrapClient;
  }
}

const client = await connect();

try {
  await client.connect();

  const adminDb = client.db("admin");
  const users = await adminDb.command({ usersInfo: { user: adminUser, db: "admin" } });
  if (users.users.length === 0) {
    await adminDb.command({
      createUser: adminUser,
      pwd: adminPassword,
      roles: [{ role: "root", db: "admin" }]
    });
    console.log(`Usuario ${adminUser} creado en admin.`);
  } else {
    console.log(`Usuario ${adminUser} ya existe; no se modificó su contraseña.`);
  }

  const db = client.db("megashop");
  for (const collectionName of collections) {
    const file = path.join(dataDir, `${collectionName}.generated.json`);
    const rows = JSON.parse(fs.readFileSync(file, "utf8"));
    const collection = db.collection(collectionName);
    await collection.deleteMany({});
    if (rows.length > 0) await collection.insertMany(rows, { ordered: false });
    console.log(`${collectionName}: ${rows.length} documentos.`);
  }

  await db.collection("usuarios").createIndex({ email: 1 }, { unique: true });
  await db.collection("clientes").createIndex({ email: 1 }, { unique: true });
  await db.collection("productos").createIndex({ categoriaId: 1, activo: 1 });
  await db.collection("productos").createIndex(
    { nombre: "text", descripcion: "text", tags: "text" },
    { name: "product_search_text" }
  );
  await db.collection("pedidos").createIndex({ fechaPedido: -1 });
  await db.collection("pedidos").createIndex({ estado: 1, fechaPedido: -1 });
  await db.collection("pedidos").createIndex({ repartidorId: 1, estado: 1 });
  await db.collection("historial_estados").createIndex({ pedidoId: 1, fecha: -1 });

  const existing = await db.listCollections({ name: "notificaciones" }).toArray();
  if (existing.length === 0) await db.createCollection("notificaciones");
  await db.collection("notificaciones").createIndex(
    { expiresAt: 1 },
    { expireAfterSeconds: 0 }
  );

  console.log("MongoDB local preparado para Megashop.");
} finally {
  await client.close();
}
