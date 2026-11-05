# Pruebas automáticas: Sistema de Reserva de Recursos (Proyecto #2)

## Ejecución

```sh
mvn test     # solo unitarias (Surefire), no necesita MySQL
mvn verify   # unitarias + integración (Failsafe), necesita MySQL y db.properties
```

- `mvn test`: **169 unitarias**, 0 fallos, 0 errores.
- `mvn verify`: las 169 unitarias más **32 de integración**, en total **201**, con 0 fallos y 0 errores.
- Probado con MySQL 8.0 y JDK 17+ (se compila con `--release 17`).
- Algunas pruebas de PDF pasan títulos con tildes a una JVM hija. En un sistema con locale ASCII/POSIX (Linux sin `LANG`) hay que ejecutar con `LANG=C.UTF-8`. En Windows/IntelliJ no hace falta.

Configuración de Maven (POM raíz):
- `maven-surefire-plugin` ejecuta `**/*Test.java` y excluye `**/*IT.java`.
- `maven-failsafe-plugin` ejecuta `**/*IT.java` con los objetivos `integration-test` y `verify`.
- JUnit Jupiter 5.11.4 y Mockito 5.20.0 (solo scope `test`). AWT corre en modo headless.

## Base de datos de pruebas

Las pruebas de integración usan la URL `db.test.url` de `db.properties` (ver `db.properties.example`). La base debe llamarse **`reservas_test`**: `TestDatabase.resetSchema()` lo verifica, borra las tablas y las vuelve a crear desde `database/reservas.sql` antes de cada prueba. Nunca toca `reservas_db`.

## Inventario

### protocol (unitarias)
| Clase | Casos | Qué verifica |
|---|---:|---|
| `DespachadorTest` | 5 | Login, crear reserva, conversión de excepciones a `Response.error`, delegación de estadísticas y propagación de sus errores |
| `OperacionTest` | 1 | Cada método de `IService` tiene su `Operacion`, y cada `Operacion` su método |
| `SerializacionTest` | 3 | Entidades, `Request`, `Response` y `Notification` sobreviven la serialización por socket |

### backend (unitarias)
| Clase | Casos | Qué verifica |
|---|---:|---|
| `AuthServiceTest` | 20 | Login, roles, entradas obligatorias, cambio de clave y confirmación |
| `FuncionarioServiceTest` | 21 | CRUD de funcionarios, búsquedas, validaciones y bloqueo del borrado cuando hay reservas |
| `CategoriaServiceTest` | 14 | IDs autogenerados, validaciones, búsqueda y bloqueo del borrado cuando hay recursos |
| `RecursoServiceTest` | 17 | Validaciones, duplicados, filtro por categoría y eliminación |
| `ReservaServiceTest` | 34 | Asignación por categoría, solapamientos, bordes, atomicidad y cancelación de reservas futuras |
| `ReservaIATest` | 5 | Extracción con IA usando un extractor simulado, sin acceso a Internet |
| `EstadisticaServiceTest` | 12 | Recursos por categoría y actividades por semana: límites inclusivos, canceladas, fechas inválidas y cambio de año |

### frontend (unitarias)
| Clase | Casos | Qué verifica |
|---|---:|---|
| `AlertasTest` | 8 | Área de alertas (MVC): agregar notificaciones, Ver detalle, Descartar con y sin confirmar, sin llamar al Backend, tabla no editable y refresco de la vista |
| `ArquitecturaFrontendTest` | 1 | La capa de presentación no referencia `Service`, `reservas.data` ni `java.sql` |
| `AutenticacionControllerTest` | 4 | Sesión al ingresar, errores y cambio de clave |
| `CalendarizacionTest` | 9 | Matriz de 24 horas por recurso, ocupación y filtros |
| `ActividadesTest` | 8 | Calendario semanal de actividades |
| `ReservaIAPresentationTest` | 7 | Interpretación de la respuesta IA en la vista de reservas |

### Integración (Failsafe, con MySQL)
| Clase | Casos | Qué verifica |
|---|---:|---|
| `DatabaseConnectionIT` | 1 | Conexión JDBC a `reservas_test` |
| `UsuarioDaoIT`, `CategoriaDaoIT`, `RecursoDaoIT`, `ReservaDaoIT` | 3 + 2 + 2 + 3 | DAOs JDBC, transacciones y relaciones |
| `FuncionarioWorkflowIT`, `ReservaWorkflowIT` | 1 + 1 | Flujos completos con recarga del Service desde MySQL en otra JVM |
| `ServerIT` | 5 | Servidor de sockets: varios clientes, respuestas y notificaciones solo a los demás clientes |
| `ServiceProxyIT` | 6 | Proxy real contra el servidor: tipos de retorno, errores, concurrencia, desconexión, notificación a otro cliente y estadísticas calculadas en el Backend |
| `ReportePdfIT` | 8 | PDFs reales de cada reporte, incluidas las dos estadísticas |

## Comportamientos conocidos
- `Service` acepta reservas en fechas pasadas. `ReservaServiceTest.fechaPasadaEsAceptadaPorLaImplementacionActual` documenta ese comportamiento.
- La extracción con IA solo se prueba con un extractor simulado. No se prueba la calidad del modelo remoto.
- No hay pruebas E2E de Swing (clics y píxeles). Los controladores se prueban con vistas simuladas (Mockito).
