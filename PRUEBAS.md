# Pruebas automáticas de Sistema de Reserva de Recursos

## Ejecución y resultados

```sh
mvn test
mvn verify
```

- `mvn test`: **150 ejecuciones unitarias**, 0 fallos, 0 errores, 0 omitidas.
- `mvn verify`: las mismas 150 unitarias y **15 ejecuciones de integración**, 0 fallos, 0 errores, 0 omitidas. **165 en total**.
- Los casos de una prueba parametrizada se cuentan individualmente, como los informa JUnit/Maven.
- Se ejecutó `mvn clean` antes de la comprobación final: Maven recompiló los 48 archivos productivos, incluido `Application.java`, y los 22 archivos Java de pruebas.
- Entorno comprobado: Windows, JDK 25.0.4, Maven integrado de IntelliJ, compilación con `--release 17`. No se afirma ejecución en una JVM 17: no estaba instalada. `release 17` comprueba las APIs y el formato de bytecode de Java 17.
- Las tres pruebas originales de `EstadisticaServiceTest` se conservan sin cambios en sus cuerpos.

Informes por caso: `target/surefire-reports/TEST-*.xml` y `target/failsafe-reports/TEST-*.xml`.
Los logs completos de la validación se copian a `target/test-audit/` (salidas generadas, no fuentes).

## Separación Maven

`maven-surefire-plugin:3.2.5` incluye `**/*Test.java` y excluye `**/*IT.java`.
`maven-failsafe-plugin:3.2.5` incluye únicamente `**/*IT.java`, con objetivos `integration-test` y `verify`.
Ambos usan JUnit Jupiter **5.11.4** y modo AWT headless.
La configuración conserva los dos directorios de recursos y los iconos.

Otros cambios del POM:

- Mockito **5.20.0**, exclusivamente con scope `test`, permite sustituir adaptadores estáticos sin modificar producción.
- `maven.compiler.release=17` verifica compatibilidad de APIs y bytecode, además de source/target existentes.
- `itext7-core:7.2.3` se declara con `type=pom`: es el agregador de los módulos iText, no un JAR. La declaración anterior impedía resolver dependencias. Se conserva la versión y el código PDF.

Referencias: [uso de Failsafe 3.2.5](https://maven.apache.org/surefire-archives/surefire-3.2.5/maven-failsafe-plugin/usage.html), [Mockito 5.20.0](https://javadoc.io/static/org.mockito/mockito-core/5.20.0/org.mockito/module-summary.html).

## Inventario y cobertura

Todos los siguientes archivos Java están bajo `src/test/java/reservas/`.

| Archivo | Casos | Reglas verificadas |
|---|---:|---|
| `logic/AuthServiceTest.java` | 20 | Login correcto/incorrecto/inexistente, administrador, entradas obligatorias, cambio de clave, confirmación, conservación de la clave ante error y persistencia solicitada |
| `logic/FuncionarioServiceTest.java` | 21 | Creación, normalización, clave inicial=id, rol, colisiones también con administradores, búsquedas parciales combinadas, modificación conservando credenciales, eliminación, validaciones |
| `logic/CategoriaServiceTest.java` | 14 | IDs autogenerados/únicos, máximo existente y formato legado, descripción obligatoria, búsqueda, modificación, relaciones y bloqueo de eliminación con recursos |
| `logic/RecursoServiceTest.java` | 17 | Validaciones, duplicados, categoría real/canónica, filtros, modificación, inexistentes y eliminación según comportamiento actual |
| `logic/ReservaServiceTest.java` | 33 | Asignación por categoría, cinco formas de solapamiento, ambos bordes exactos, segundo recurso, fechas distintas, agotamiento, múltiples categorías, atomicidad en ambos órdenes, datos obligatorios, intervalo inválido, propietario, cancelación y reutilización |
| `logic/ReservaIATest.java` | 5 | Entrada vacía sin invocar extractor, frase/categorías/fecha enviadas, respuesta y error del extractor; ninguna llamada externa |
| `logic/EstadisticaServiceTest.java` (ampliado) | 12 | Tres originales más extremos inclusivos, canceladas, fechas nulas/invertidas, semanas vacías/cambio de año, orden cronológico, recursos inválidos y conteo de actividades independiente del número de recursos |
| `presentation/ActividadesTest.java` | 8 | Lunes/domingo desde distintos días, 24×7 celdas, días/horas/minutos, varias actividades en una celda, semanas externas, canceladas y fecha requerida |
| `presentation/AutenticacionControllerTest.java` | 4 | Sesión al ingresar, error sin cierre, cancelación y cambio de clave desde el controlador |
| `presentation/CalendarizacionTest.java` | 9 | 24 horas, columnas por categoría, libres/ocupadas, actividad/responsable, minutos y extremos del día, otras fechas, canceladas, categorías vacías y filtros obligatorios |
| `presentation/ReservaIAPresentationTest.java` | 7 | Fecha ISO, respuesta incompleta, categorías conocidas/desconocidas, fecha inválida, horas inválidas al reservar y selección sin duplicados |
| `integration/XmlPersistenceIT.java` | 5 | Escritura/lectura reales, usuarios/roles/credenciales, categorías, recursos, reservas, estados, identidad compartida de relaciones, caracteres XML, segunda escritura, archivo ausente, XML malformado, inicio del sistema y caracterización de referencias huérfanas |
| `integration/ReservaWorkflowIT.java` | 1 | Flujo real completo: alta, dos recursos, solapamiento, agotamiento sin alterar XML, cancelación, reutilización y recarga en otra JVM |
| `integration/FuncionarioWorkflowIT.java` | 1 | Alta, login, nueva clave, modificación, búsqueda, recarga, eliminación y nueva recarga; coherencia Usuario/Funcionario |
| `integration/ReportePdfIT.java` | 8 | PDF reales de Reservas, Funcionarios, Categorías, Recursos, Calendarización, Actividades y los dos reportes de Estadísticas |

**Archivos nuevos de soporte (no cuentan como pruebas adicionales):**

- `support/ServiceTestSupport.java`
- `support/FixedTime.java`
- `support/IsolatedJvm.java`
- `support/IntegrationFixture.java`
- `support/XmlScenario.java`
- `support/WorkflowScenario.java`
- `support/PdfScenario.java`

En total se crearon 14 clases de pruebas y siete clases auxiliares; se amplió una clase de pruebas existente.
Los únicos archivos previamente existentes modificados son `pom.xml` y `src/test/java/reservas/logic/EstadisticaServiceTest.java`.
Se agregó este documento. No se modificó ningún archivo de `src/main`, controlador, vista, modelo ni `.form`.

## Aislamiento y alcance de las comprobaciones

### Unitarias

`ServiceTestSupport` intercepta `XmlStorage` **antes de inicializar Service**. Invoca el constructor privado existente mediante reflexión con un `Data` nuevo y sustituye `Service.instance()` por esa instancia durante la prueba. El código de negocio de Service se ejecuta realmente; no se reemplazan sus operaciones por respuestas prefabricadas.

La reflexión se limita a construir instancias aisladas y asignar el campo existente `reservaExtractorService`. No se cambia el campo final del singleton ni se invocan métodos de negocio privados.

El almacenamiento está simulado solamente en unitarias. Los casos de atomicidad y rechazo comprueban además que no se solicitó guardar. `@AfterEach` cierra mocks y limpia `Sesion`; ninguna prueba requiere datos de otra.

`FixedTime` sustituye temporalmente `Clock.systemDefaultZone()` por **2026-09-16 12:00 UTC**, sin cambiar el reloj de Windows. Permite distinguir reserva futura, pasada, empezada y comienzo exactamente igual a ahora. Se libera al terminar cada prueba.

Los controladores se ejercitan mediante sus métodos públicos con modelos reales y vistas simuladas. No se ejecutan constructores Swing ni código generado por GUI Designer. Los errores capturados por controladores se verifican expresamente, de modo que un catch no convierte una operación fallida en una prueba exitosa.

### Integración

Cada prueba obtiene `@TempDir`. `IsolatedJvm` inicia procesos Java cuyo directorio de trabajo **real** es ese temporal. Antes de inicializar Service/XmlStorage, el hijo comprueba su directorio y el modo headless. Así, las rutas productivas `data/reservas.xml` y `*.pdf` solo escriben en el temporal. No se depende de modificar `user.dir` ni de respaldar/restaurar datos del usuario.

XML, Service, entidades, generadores PDF, iText y gráficos JFreeChart son reales. En PDF se simulan las entradas de las vistas; se ejecutan los métodos reales que crean los gráficos. El lector iText vuelve a abrir cada PDF y comprueba firma, páginas, títulos y registros. En estadísticas también verifica el stream de imagen de 700×350 con contenido. En los calendarios comprueba que no aparezcan las actividades canceladas. No se simula `PdfWriter`.

Las aserciones de los escenarios auxiliares se ejecutan en el hijo; cualquier excepción o aserción fallida produce un código de salida distinto de cero y falla la prueba JUnit del padre, mostrando el log. El timeout de 45 segundos protege frente a bloqueos, no es una espera usada para sincronizar datos. Las pruebas de flujo recargan en JVM nuevas para comprobar persistencia, no solo el estado del singleton en memoria.

Los procesos usan `java.awt.headless=true`; no abren visores PDF ni ventanas.

### Repetibilidad y ejecución sin Internet

La extracción IA siempre se sustituye por un `ReservaExtractorService` controlado. Ninguna prueba utiliza OpenAI o Internet.
Maven necesita descargar sus dependencias y plugins en la primera ejecución. Una vez disponibles localmente, se puede verificar sin resolver nada por red y cambiando el orden:

```sh
mvn -o -Dsurefire.runOrder=random -Dfailsafe.runOrder=random '-Djunit.jupiter.testmethod.order.default=org.junit.jupiter.api.MethodOrderer$Random' -Djunit.jupiter.execution.order.random.seed=42 verify
```

Las fechas y datos son constantes. Los únicos nombres temporales generados corresponden al aislamiento de archivos y logs. No se usa `Thread.sleep` ni azar en las reglas que se prueban.

## Defectos y límites que NO deben ocultarse por un BUILD SUCCESS

### Pérdida de historial al borrar un funcionario con reservas

- Caso reproducido: crear funcionario, recurso y reserva; eliminar el funcionario; recargar XML.
- Expectativa de integridad: no perder silenciosamente una reserva existente (la solución podría impedir el borrado o conservar el historial).
- Comportamiento actual: `eliminarFuncionario` permite el borrado. La reserva sigue en memoria y se escribe con `funcionarioId`, pero `XmlStorage.cargarReservas` omite la reserva si el funcionario ya no existe.
- Consecuencia comprobada: una reserva antes de recargar, cero después.

### Pérdida de recursos asignados al borrar un recurso reservado

- Caso reproducido: crear reserva con R1; eliminar R1; recargar XML.
- Expectativa de integridad: conservar la relación histórica o impedir una eliminación incompatible con ella.
- Comportamiento actual: `eliminarRecurso` no valida reservas; al recargar, `cargarReservas` descarta el recurso que ya no existe.
- Consecuencia comprobada: un recurso asignado en memoria, cero después de recargar; la reserva permanece activa.

Ambos casos se reproducen explícitamente en `XmlPersistenceIT.documentaPerdidaActualDeReferenciasAlEliminarEntidadesReservadas`. Es una **prueba de caracterización de defectos conocidos**, no una afirmación de que ese comportamiento sea correcto. Se conserva evidencia de las cantidades antes y después. No se alteraron expectativas después de descubrir un fallo ni se deshabilitaron pruebas.

No se corrigieron estas reglas porque decidir entre prohibir el borrado, mantener historial o hacer otra operación cambia el comportamiento productivo y requiere definir la política de eliminación. Ningún cambio de negocio fue necesario para la batería.

### Comportamientos actuales y cobertura excluida

- Service acepta reservas en fechas pasadas. La prueba correspondiente lo identifica expresamente; no se inventó una prohibición inexistente.
- Cancelar se permite solo antes de comenzar. Las canceladas no bloquean recursos ni aparecen como ocupadas o contadas en calendarios/estadísticas.
- La interpretación IA prueba el contrato y el procesamiento local. No comprueba la precisión del modelo remoto, la comprensión de lenguaje natural ni su disponibilidad; las fechas relativas son una responsabilidad del servicio externo.
- No es una batería E2E de Swing: no comprueba píxeles, navegación, foco, listeners por clic ni instrumentación `.form`. Maven compila los Java, pero la aplicación sigue usando el mecanismo GUI Designer de IntelliJ para inicializar componentes en ejecución. No se añadió un compilador de formularios a Maven solo para las pruebas.
- No se afirma cobertura del 100 % de líneas, concurrencia, fallos de disco durante una escritura, archivos XML hostiles ni todos los errores de infraestructura. La atomicidad verificada se refiere a disponibilidad de todas las categorías antes de crear una reserva, no a transacciones frente a fallos de I/O.
- Los resultados exitosos prueban los casos enumerados; no eliminan los dos defectos de historial documentados.
