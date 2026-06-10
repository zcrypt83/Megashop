# Megashop Backend

API REST de Megashop desarrollada con Java 21 y Spring Boot 3.5. El servicio
centraliza autenticacion, catalogo, clientes, pedidos, seguimiento y metricas
administrativas. MongoDB almacena la informacion del negocio y Redis conserva
sesiones y datos temporales.

## Tecnologias

- Java 21.
- Spring Boot 3.5.
- Spring Web y Spring Validation.
- Spring Security con autorizacion por roles.
- Spring Data MongoDB.
- Spring Data Redis.
- JJWT 0.12.6.
- Maven 3.9.
- JUnit 5 y Spring Security Test.

## Arquitectura

El backend sigue una arquitectura por capas:

```text
HTTP Request
    |
Controller
    |
DTO + Bean Validation
    |
Service
    |------ Repository ------ MongoDB
    |
    `------ RedisTemplate --- Redis
```

- `controller`: expone los endpoints HTTP.
- `dto`: define y valida los cuerpos de entrada.
- `service`: contiene las reglas de negocio.
- `repository`: abstrae la persistencia con Spring Data.
- `model`: representa documentos MongoDB y objetos embebidos.
- `security`: procesa JWT, sesiones y limite de solicitudes.
- `config`: configura Spring Security, BCrypt y CORS.
- `exception`: transforma errores en respuestas JSON uniformes.
- `web`: contiene contratos comunes de la API.

## Requisitos

- Java 21 o superior.
- Maven 3.9 o el Maven portable incluido por el proyecto local.
- MongoDB accesible mediante una URI autenticada.
- Redis accesible desde el backend.

## Variables de entorno

El backend no incluye secretos predeterminados. Antes de iniciarlo configura:

| Variable | Obligatoria | Descripcion |
|---|---:|---|
| `MONGO_URI` | Si | URI completa de MongoDB con base y autenticacion. |
| `JWT_SECRET` | Si | Secreto para firmar access tokens. |
| `JWT_REFRESH_SECRET` | Si | Secreto independiente para refresh tokens. |
| `REDIS_URL` | No | Conexion Redis. Predeterminado: `redis://localhost:6379`. |
| `PORT` | No | Puerto HTTP. Predeterminado: `4000`. |
| `FRONTEND_URL` | No | Origen permitido por CORS. |
| `JWT_ACCESS_EXPIRATION_MS` | No | Vigencia del access token. |
| `JWT_REFRESH_EXPIRATION_MS` | No | Vigencia del refresh token. |

Usa secretos aleatorios de al menos 32 caracteres. La plantilla general está
en `../.env.example`; el archivo `.env` real no debe subirse a Git.

## Ejecucion local

Desde la raiz del repositorio:

```powershell
docker compose up -d redis
powershell -ExecutionPolicy Bypass -File scripts/run-maven.ps1 -f backend/pom.xml spring-boot:run
```

El wrapper `scripts/run-maven.ps1` usa Maven global cuando esta disponible y
recurre al Maven portable del entorno local en caso contrario.

La API queda disponible en:

```text
http://localhost:4000/api/v1
```

Comprueba el servicio con:

```powershell
Invoke-RestMethod http://localhost:4000/api/v1/health
```

## Ejecucion con Docker

La imagen usa una compilacion multi-stage:

1. Maven 3.9.9 y Eclipse Temurin 21 compilan el JAR.
2. Eclipse Temurin 21 JRE Alpine ejecuta el artefacto.

Desde la raiz:

```powershell
Copy-Item .env.example .env
docker compose up --build
```

Reemplaza todos los marcadores de `.env` antes de iniciar los contenedores.

## Endpoints principales

Base URL: `/api/v1`

| Metodo | Ruta | Acceso | Funcion |
|---|---|---|---|
| `GET` | `/health` | Publico | Estado del servicio. |
| `POST` | `/auth/register` | Publico | Registro y emision de tokens. |
| `POST` | `/auth/login` | Publico | Inicio de sesion. |
| `POST` | `/auth/refresh` | Publico | Renovacion de tokens. |
| `POST` | `/auth/logout` | Autenticado | Invalida la sesion Redis. |
| `GET` | `/productos` | Publico | Lista y filtra productos. |
| `GET` | `/productos/{id}` | Publico | Detalle de producto. |
| `POST/PUT/DELETE` | `/productos/**` | Admin | Gestion del catalogo. |
| `GET` | `/clientes/{id}` | Autenticado | Consulta un cliente. |
| `GET/POST/PUT/DELETE` | `/clientes/**` | Admin | Administracion de clientes. |
| `GET` | `/pedidos/{id}` | Autenticado | Consulta un pedido. |
| `GET` | `/pedidos/estado/{estado}` | Autenticado | Pedidos por estado. |
| `POST` | `/pedidos` | Autenticado | Crea un pedido. |
| `PUT` | `/pedidos/{id}` | Admin/Repartidor | Actualiza el estado. |
| `DELETE` | `/pedidos/{id}` | Admin | Elimina un pedido. |
| `GET` | `/dashboard/**` | Admin | Resumen y agregaciones. |

## Respuestas

Las respuestas correctas se envuelven en:

```json
{
  "data": {}
}
```

Los errores siguen esta estructura:

```json
{
  "error": {
    "message": "Descripcion del error",
    "status": 400,
    "details": {}
  }
}
```

Los estados habituales son `400`, `401`, `403`, `404`, `409` y `500`.

## Seguridad

- API sin estado mediante `SessionCreationPolicy.STATELESS`.
- Access token y refresh token firmados con secretos diferentes.
- Refresh token asociado a una sesion en Redis.
- Contraseñas almacenadas con BCrypt y factor de costo 12.
- Autorizacion declarativa mediante `@PreAuthorize`.
- CORS limitado al origen configurado.
- Rate limiting aplicado antes de la autenticacion.
- CSRF deshabilitado porque la API usa tokens Bearer.

Los endpoints protegidos esperan:

```http
Authorization: Bearer <access_token>
```

## Pruebas y compilacion

Desde la raiz:

```powershell
npm.cmd run test:backend
npm.cmd run build:backend
```

O directamente:

```powershell
powershell -ExecutionPolicy Bypass -File scripts/run-maven.ps1 -f backend/pom.xml clean test
```

El artefacto ejecutable se genera en:

```text
backend/target/megashop-backend.jar
```

## Solucion de problemas

### La aplicacion solicita `MONGO_URI` o secretos JWT

Las variables son obligatorias deliberadamente. Crea `.env` desde la plantilla
y asigna valores locales seguros.

### Error de conexion con MongoDB

Comprueba que el servicio esta activo, que el usuario tiene permisos sobre la
base `megashop` y que `authSource` corresponde a la base de autenticacion.

### Error de conexion con Redis

Inicia Redis y verifica:

```powershell
docker compose up -d redis
docker exec megashop-redis redis-cli ping
```

La respuesta esperada es `PONG`.

### Respuesta 401 o 403

- `401`: el token falta, expiro o no es valido.
- `403`: el usuario esta autenticado, pero no tiene el rol requerido.

## Criterios de contribucion

- Mantener controladores delgados.
- Colocar reglas de negocio en servicios.
- Usar DTO para entradas externas.
- Validar entradas con Bean Validation.
- Evitar secretos y credenciales en el codigo.
- Añadir pruebas cuando cambie autenticacion o comportamiento compartido.
