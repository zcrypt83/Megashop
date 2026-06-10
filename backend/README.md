# Megashop Backend

API REST construida con Java 21 y Spring Boot 3.5.

## Ejecutar

```powershell
powershell -ExecutionPolicy Bypass -File ..\scripts\run-maven.ps1 spring-boot:run
```

Requiere MongoDB y Redis. La configuracion se encuentra en `src/main/resources/application.yml` y puede sobrescribirse con variables de entorno.

## Verificar

```powershell
npm.cmd --prefix .. run test:backend
npm.cmd --prefix .. run build:backend
```

El JAR se genera como `target/megashop-backend.jar`.

## Paquetes

- `controller`: endpoints REST.
- `dto`: entradas validadas.
- `service`: reglas de negocio.
- `repository`: persistencia MongoDB.
- `model`: documentos y objetos embebidos.
- `security`: JWT y rate limiting.
- `config`: Spring Security y CORS.
- `exception`: manejo uniforme de errores.

Las variables requeridas se describen en `../.env.example`.
