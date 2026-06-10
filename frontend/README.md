# Megashop Frontend

Interfaz web de Megashop construida como una SPA con React 19 y Vite. El
frontend permite explorar el catalogo, administrar un carrito visual, consultar
el seguimiento de pedidos, iniciar sesion y visualizar indicadores operativos.

## Tecnologias

- React 19.
- React DOM 19.
- React Router 7.
- Vite 6.
- Bootstrap 5.
- Lucide React.
- Fetch API para comunicacion HTTP.

## Funcionalidades

- Catalogo conectado a la API.
- Busqueda de productos por nombre.
- Filtro visual por categoria.
- Carrito local con cantidades, eliminacion y total.
- Vista de seguimiento del pedido.
- Inicio de sesion mediante JWT.
- Dashboard administrativo.
- Datos de demostracion cuando el catalogo no esta disponible.
- Diseño responsive para escritorio y dispositivos moviles.

## Estructura

```text
frontend/
|-- src/
|   |-- api/
|   |   `-- client.js
|   |-- pages/
|   |   |-- Dashboard.jsx
|   |   |-- Login.jsx
|   |   |-- Products.jsx
|   |   `-- TrackOrder.jsx
|   |-- App.jsx
|   |-- main.jsx
|   `-- styles.css
|-- index.html
|-- vite.config.js
|-- package.json
`-- Dockerfile
```

- `main.jsx`: monta React y configura `BrowserRouter`.
- `App.jsx`: define el shell, navegacion y rutas.
- `api/client.js`: centraliza llamadas HTTP y cabeceras JWT.
- `pages/`: contiene las vistas principales.
- `styles.css`: define layout, componentes y breakpoints.
- `vite.config.js`: activa el plugin oficial de React.

## Requisitos

- Node.js 22 o superior.
- npm 10 o superior.
- Backend disponible para usar datos reales.

## Instalacion

Desde `frontend/`:

```powershell
npm.cmd install
```

También puedes instalar todos los paquetes desde la raiz:

```powershell
npm.cmd run install:all
```

## Configuracion

La variable opcional `VITE_API_URL` indica la base de la API:

```text
VITE_API_URL=http://localhost:4000/api/v1
```

Si no se define, el cliente usa:

```text
http://localhost:4000/api/v1
```

Para una configuracion local crea `frontend/.env.local`:

```dotenv
VITE_API_URL=http://localhost:4000/api/v1
```

Los archivos `.env*`, salvo las plantillas, estan excluidos de Git. Nunca
coloques tokens, contraseñas ni secretos del backend en variables `VITE_*`,
porque Vite las incorpora al codigo que recibe el navegador.

## Desarrollo

Desde `frontend/`:

```powershell
npm.cmd run dev
```

Desde la raiz:

```powershell
npm.cmd run dev:frontend
```

Vite escucha en todas las interfaces y normalmente publica:

```text
http://localhost:5173
```

## Rutas

| Ruta | Vista | Descripcion |
|---|---|---|
| `/` | Productos | Catalogo, filtros y carrito. |
| `/tracking` | Seguimiento | Estado visual de una entrega. |
| `/dashboard` | Admin | Indicadores y operacion comercial. |
| `/login` | Acceso | Inicio de sesion. |

React Router gestiona la navegacion en el cliente sin recargar la pagina.

## Cliente HTTP

Todas las solicitudes pasan por `src/api/client.js`. El helper:

1. Combina `VITE_API_URL` con la ruta solicitada.
2. Lee el access token desde `localStorage`.
3. Agrega `Authorization: Bearer <token>` cuando existe una sesion.
4. Interpreta la respuesta JSON.
5. Devuelve `payload.data` o lanza un error con el mensaje de la API.

Ejemplo:

```js
const products = await api("/productos");
```

El token se almacena bajo la clave:

```text
megashop_token
```

No almacenes contraseñas ni refresh tokens en el codigo fuente.

## Comportamiento sin backend

La pantalla de productos contiene un conjunto pequeño de datos de demostracion.
Si `/productos` no responde, el catalogo mantiene una experiencia visual
utilizable. Este fallback no reemplaza las pruebas de integracion con la API.

El dashboard también presenta valores de respaldo cuando su endpoint protegido
no esta disponible.

## Compilacion

Genera los archivos optimizados:

```powershell
npm.cmd run build
```

Desde la raiz:

```powershell
npm.cmd run build:frontend
```

El resultado se escribe en `frontend/dist/`, carpeta excluida del repositorio.

Para revisar el build:

```powershell
npm.cmd run preview
```

## Docker

La imagen usa Node 22 Alpine:

```powershell
docker compose up --build frontend
```

El contenedor instala dependencias, copia el proyecto y ejecuta Vite en el
puerto `5173`.

## Estilos y experiencia

- Sidebar fija en escritorio y navegacion adaptada en pantallas pequeñas.
- Paneles con bordes discretos y radios de 8 px.
- Iconos Lucide para acciones y navegacion.
- Bootstrap aporta estilos base; `styles.css` define la identidad visual.
- Los componentes mantienen tamaños estables para evitar saltos de layout.

## Solucion de problemas

### Pantalla blanca

Abre la consola del navegador y revisa errores de JavaScript. Confirma que:

- `vite.config.js` incluye `@vitejs/plugin-react`.
- Los componentes JSX importan React cuando sea necesario.
- `index.html` contiene el elemento `#root`.
- El servidor se inicio desde la carpeta `frontend`.

Después reinicia Vite:

```powershell
npm.cmd run dev
```

### La API no responde

- Comprueba `http://localhost:4000/api/v1/health`.
- Verifica `VITE_API_URL`.
- Confirma que `FRONTEND_URL` del backend permite `http://localhost:5173`.
- Revisa la consola por errores CORS o HTTP.

### El login devuelve 401

La contraseña no se incluye en el repositorio. Usa un usuario creado en tu base
local y comprueba que el backend tenga acceso a MongoDB y Redis.

### Los cambios de configuracion de Vite no aparecen

Los cambios en `vite.config.js` requieren reiniciar el servidor de desarrollo.

## Verificacion

Desde la raiz del repositorio:

```powershell
npm.cmd run check
```

El comando ejecuta las pruebas del backend y construye el frontend.

## Criterios de contribucion

- Mantener las llamadas HTTP dentro de `src/api/`.
- No incluir credenciales ni tokens en el repositorio.
- Reutilizar los componentes y estilos existentes.
- Verificar las vistas en escritorio y movil.
- Ejecutar `npm.cmd run build` antes de publicar cambios.
