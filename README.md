# Sistema de Reserva de Recursos: Proyecto #2 (EIF206 Programación 3)

Universidad Nacional, Escuela de Informática.

Integrantes:
- Lorenzo Aguilar Garro
- Yoel Gustavo García Rodríguez
- Jose Mauricio Ramirez Chinchilla

## Arquitectura

Es una aplicación distribuida con dos procesos independientes que se comunican por sockets:

| Módulo | Contenido |
|---|---|
| `protocol` | Entidades (`Usuario`, `Funcionario`, `Categoria`, `Recurso`, `Reserva`), la interfaz `IService` y los mensajes `Request`, `Response` y `Notification`, además de `Operacion` y `Despachador` |
| `backend` | Capas de Lógica (`Service`, `EstadisticaService`) y Datos (DAOs JDBC sobre MySQL). `Server` atiende cada cliente en su propio hilo (`Worker`). Cuando alguien crea una reserva, envía una notificación asíncrona a todos los **demás** clientes. No tiene interfaz gráfica |
| `frontend` | Presentación en Swing con MVC. `ServiceProxy` (patrón Proxy) implementa `IService`: envía las peticiones por socket y un hilo lector recibe las respuestas y las notificaciones. El área de **Alertas** (`presentation.alertas`) muestra las reservas que crean los demás usuarios. El frontend no tiene ninguna referencia a la base de datos |

## Requisitos
- JDK 17 o superior, y Maven 3.9 o superior.
- MySQL 8.x.

## 1. Base de datos

```sh
mysql -u root -p < database/reservas.sql
```

El script crea `reservas_db` con sus tablas y el usuario inicial `admin` / `admin`. Para las pruebas de integración también hace falta una base vacía llamada `reservas_test`:

```sql
CREATE DATABASE reservas_test CHARACTER SET utf8mb4;
```

Copie `db.properties.example` como `db.properties` en la raíz del proyecto y escriba el usuario y la clave de MySQL. Este archivo no se sube al repositorio.

## 2. Compilar

```sh
mvn -DskipTests install
```

## 3. Ejecutar

Backend (sin interfaz). Escucha en el puerto 5050 si no se indica otro:

```sh
mvn -pl backend exec:java -Dexec.mainClass=reservas.server.ServerApplication -Dexec.args="5050"
```

Frontend. Se pueden abrir varios, en la misma máquina o en otras de la red:

```sh
mvn -pl frontend exec:java -Dexec.mainClass=reservas.Application -Dexec.args="IP_DEL_BACKEND 5050"
```

Desde IntelliJ también se pueden ejecutar directamente `ServerApplication` (con argumento `[puerto]`) y `Application` (con argumentos `[host] [puerto]`).

## 4. Pruebas

```sh
mvn test
mvn verify
```

`mvn test` corre solo las pruebas unitarias. `mvn verify` corre además las de integración y necesita MySQL. Ver [PRUEBAS.md](PRUEBAS.md).
