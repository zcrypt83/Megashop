# Megashop

Megashop es una plataforma de comercio electronico y delivery construida con
MongoDB, Redis, Spring Boot y React.

## Tecnologias

- Frontend: React 19, React Router, Bootstrap 5, Vite y Lucide.
- Backend: Java 21, Spring Boot 3.5, Spring MVC y Spring Security.
- Seguridad: JWT, refresh tokens, RBAC, BCrypt, CORS y rate limiting.
- Datos: MongoDB con documentos, referencias, indices y agregaciones.
- Cache: Redis 7 para sesiones, tracking y cache.
- DevOps: Docker Compose y contenedores multi-stage.

## Requisitos

- Docker Desktop con el motor Linux iniciado.
- Node.js 22 o superior.
- Java 21 o superior y Maven 3.9 para ejecutar el backend sin Docker.

## Configuracion

Crea la configuracion local a partir de la plantilla y reemplaza todos los
valores de ejemplo:

```powershell
Copy-Item .env.example .env
```

El archivo `.env` esta excluido de Git. No publiques contrasenas, tokens,
claves privadas ni cadenas de conexion reales.

## Inicio rapido

```powershell
npm.cmd run install:all
docker compose up --build
```

Servicios:

- Frontend: `http://localhost:5173`
- Backend: `http://localhost:4000/api/v1`
- Health check: `http://localhost:4000/api/v1/health`
- MongoDB: `localhost:27017`
- Redis: `localhost:6379`

## Recursos

- [Prototipo para Figma Make](prototype/megashop-frontend.make)
- [Coleccion Postman](postman/megashop.postman_collection.json)

Los datasets generados, documentos academicos y configuraciones locales se
mantienen fuera del repositorio publico.

## Estructura

```text
backend/    Aplicacion Spring Boot organizada por capas
frontend/   SPA React para catalogo, tracking, login y dashboard
scripts/    Inicializacion, consultas y generacion de datos
postman/    Coleccion de solicitudes REST
prototype/  Prototipo para Figma Make
```
