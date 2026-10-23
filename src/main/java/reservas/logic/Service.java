package reservas.logic;

import reservas.ai.ReservaExtraccion;
import reservas.ai.ReservaExtractorService;

import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;

import reservas.data.Data;
import reservas.data.Database;
import reservas.data.MySqlPersistencia;
import reservas.data.Persistencia;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.time.LocalTime;

public class Service {

    private static volatile Service INSTANCE;

    private final Data data;
    private final Persistencia persistencia;
    private ReservaExtractorService reservaExtractorService;

    private Service(Persistencia persistencia) {

        try {

            this.persistencia = persistencia;
            Data dataCargada = persistencia.cargar();

            if (dataCargada != null) {

                // Ya existe reservas.xml
                data = dataCargada;

            } else {

                // Primera ejecución
                data = new Data();

            }

        } catch (Exception e) {

            throw new RuntimeException(
                    "Error al cargar los datos",
                    e
            );
        }
    }
    public static Service instance() {
        Service local = INSTANCE;
        if (local == null) synchronized (Service.class) {
            local = INSTANCE;
            if (local == null) INSTANCE = local = new Service(new MySqlPersistencia(Database.desarrollo()));
        }
        return local;
    }
    public static Service crearParaPruebas(Persistencia persistencia) { return new Service(persistencia); }

    // =========================================================
    // LOGIN
    // =========================================================

    public synchronized Usuario login(String id, String clave) throws Exception {

        if (id == null || id.trim().isEmpty()) {
            throw new Exception("El ID es requerido");
        }

        if (clave == null || clave.trim().isEmpty()) {
            throw new Exception("La clave es requerida");
        }

        String idBusqueda = id.trim();

        for (Usuario usuario : data.getUsuarios()) {

            if (usuario.getId().equals(idBusqueda)
                    && usuario.getClave().equals(clave)) {

                return usuario;
            }
        }

        throw new Exception("ID o clave incorrectos");
    }
    //Agregue esta para que tambien funcione con admins
    public synchronized Usuario buscarUsuarioPorId(String id) throws Exception {

        if (id == null || id.trim().isEmpty()) {
            throw new Exception("El ID es requerido");
        }

        String idBusqueda = id.trim();

        for (Usuario usuario : data.getUsuarios()) {
            if (usuario.getId().equals(idBusqueda)) {
                return usuario;
            }
        }

        throw new Exception("Usuario no encontrado");
    }

    public synchronized void cambiarClave(
            Usuario usuario,
            String claveActual,
            String claveNueva,
            String confirmarClave
    ) throws Exception {

        if (usuario == null) {
            throw new Exception("Usuario requerido");
        }

        if (claveActual == null || claveActual.isEmpty()) {
            throw new Exception("La clave actual es requerida");
        }

        if (claveNueva == null || claveNueva.trim().isEmpty()) {
            throw new Exception("La nueva clave es requerida");
        }

        if (!claveNueva.equals(confirmarClave)) {
            throw new Exception(
                    "La clave nueva y la confirmación no coinciden"
            );
        }

        Usuario existente = buscarUsuarioPorId(usuario.getId());
        if (!existente.getClave().equals(claveActual)) throw new Exception("La clave actual es incorrecta");
        Usuario actualizado = existente instanceof Funcionario f ? new Funcionario(f.getId(), claveNueva.trim(), f.getNombre(), f.getTelefono()) : new Usuario(existente.getId(), claveNueva.trim(), existente.getRol());
        persistencia.actualizarUsuario(actualizado);
        existente.setClave(actualizado.getClave());
        usuario.setClave(actualizado.getClave());
    }

    // =========================================================
    // FUNCIONARIOS
    // =========================================================

    public synchronized void crearFuncionario(
            Funcionario funcionario
    ) throws Exception {

        validarFuncionario(funcionario);

        String id = funcionario.getId().trim();

        if (existeUsuario(id)) {
            throw new Exception(
                    "Ya existe un usuario con el ID " + id
            );
        }

        Funcionario nuevo = new Funcionario(id, id, funcionario.getNombre().trim(), funcionario.getTelefono().trim());
        persistencia.insertarUsuario(nuevo);
        funcionario.setId(nuevo.getId());
        funcionario.setNombre(nuevo.getNombre());
        funcionario.setTelefono(nuevo.getTelefono());
        funcionario.setClave(nuevo.getClave());
        funcionario.setRol(nuevo.getRol());
        data.getUsuarios().add(funcionario);
    }

    public synchronized Funcionario buscarFuncionarioPorId(
            String id
    ) throws Exception {

        if (id == null || id.trim().isEmpty()) {
            throw new Exception("El ID es requerido");
        }

        String idBusqueda = id.trim();

        for (Usuario usuario : data.getUsuarios()) {

            if (usuario instanceof Funcionario
                    && usuario.getId().equals(idBusqueda)) {

                return (Funcionario) usuario;
            }
        }

        throw new Exception("Funcionario no encontrado");
    }

    public synchronized List<Funcionario> buscarFuncionarios(
            String id,
            String nombre
    ) {

        List<Funcionario> resultado =
                new ArrayList<>();

        String idBusqueda =
                id == null
                        ? ""
                        : id.trim().toLowerCase();

        String nombreBusqueda =
                nombre == null
                        ? ""
                        : nombre.trim().toLowerCase();

        for (Usuario usuario : data.getUsuarios()) {

            if (usuario instanceof Funcionario) {

                Funcionario funcionario =
                        (Funcionario) usuario;

                boolean coincideId =
                        funcionario.getId()
                                .toLowerCase()
                                .contains(idBusqueda);

                boolean coincideNombre =
                        funcionario.getNombre()
                                .toLowerCase()
                                .contains(nombreBusqueda);

                if (coincideId && coincideNombre) {
                    resultado.add(funcionario);
                }
            }
        }

        return resultado;
    }

    public synchronized List<Funcionario> listarFuncionarios() {

        List<Funcionario> resultado =
                new ArrayList<>();

        for (Usuario usuario : data.getUsuarios()) {

            if (usuario instanceof Funcionario) {
                resultado.add(
                        (Funcionario) usuario
                );
            }
        }

        return resultado;
    }

    public synchronized void modificarFuncionario(
            Funcionario funcionario
    ) throws Exception {

        validarFuncionario(funcionario);

        Funcionario existente =
                buscarFuncionarioPorId(
                        funcionario.getId()
                );

        Funcionario actualizado = new Funcionario(existente.getId(), existente.getClave(), funcionario.getNombre().trim(), funcionario.getTelefono().trim());
        persistencia.actualizarUsuario(actualizado);
        existente.setNombre(actualizado.getNombre());
        existente.setTelefono(actualizado.getTelefono());
        funcionario.setNombre(actualizado.getNombre());
        funcionario.setTelefono(actualizado.getTelefono());
    }

    public synchronized void eliminarFuncionario(
            String id
    ) throws Exception {

        Funcionario funcionario =
                buscarFuncionarioPorId(id);

        for (Reserva reserva : data.getReservas()) if (reserva.getFuncionario().equals(funcionario)) throw new Exception("No se puede eliminar el funcionario porque tiene reservas asociadas");
        persistencia.eliminarUsuario(funcionario.getId());
        boolean eliminado = data.getUsuarios().remove(funcionario);

        if (!eliminado) {
            throw new Exception(
                    "No fue posible eliminar el funcionario"
            );
        }

    }

    // =========================================================
    // CATEGORÍAS
    // =========================================================

    public synchronized void crearCategoria(
            Categoria categoria
    ) throws Exception {

        validarCategoria(categoria);

        Categoria nueva = new Categoria(generarIdCategoria(), categoria.getDescripcion().trim());
        persistencia.insertarCategoria(nueva);
        categoria.setDescripcion(nueva.getDescripcion());
        categoria.setId(nueva.getId());
        data.getCategorias().add(categoria);
    }

    public synchronized Categoria buscarCategoriaPorId(
            String id
    ) throws Exception {

        if (id == null || id.trim().isEmpty()) {
            throw new Exception(
                    "El ID de categoría es requerido"
            );
        }

        String idBusqueda = id.trim();

        for (Categoria categoria :
                data.getCategorias()) {

            if (categoria.getId()
                    .equals(idBusqueda)) {

                return categoria;
            }
        }

        throw new Exception(
                "Categoría no encontrada"
        );
    }

    public synchronized List<Categoria> buscarCategorias(
            String descripcion
    ) {

        List<Categoria> resultado =
                new ArrayList<>();

        String descripcionBusqueda =
                descripcion == null
                        ? ""
                        : descripcion
                        .trim()
                        .toLowerCase();

        for (Categoria categoria :
                data.getCategorias()) {

            if (categoria
                    .getDescripcion()
                    .toLowerCase()
                    .contains(descripcionBusqueda)) {

                resultado.add(categoria);
            }
        }

        return resultado;
    }

    public synchronized List<Categoria> listarCategorias() {
        return new ArrayList<>(
                data.getCategorias()
        );
    }

    public synchronized void modificarCategoria(
            Categoria categoria
    ) throws Exception {

        validarCategoria(categoria);

        Categoria existente =
                buscarCategoriaPorId(
                        categoria.getId()
                );

        Categoria actualizada = new Categoria(existente.getId(), categoria.getDescripcion().trim());
        persistencia.actualizarCategoria(actualizada);
        existente.setDescripcion(actualizada.getDescripcion());
        categoria.setDescripcion(actualizada.getDescripcion());
    }

    public synchronized void eliminarCategoria(
            String id
    ) throws Exception {

        Categoria categoria =
                buscarCategoriaPorId(id);

        /*
         * Por ahora evitamos eliminar una categoría
         * si existen recursos asociados.
         */
        for (Recurso recurso :
                data.getRecursos()) {

            if (recurso.getCategoria()
                    .equals(categoria)) {

                throw new Exception(
                        "No se puede eliminar la categoría "
                                + "porque tiene recursos asociados"
                );
            }
        }

        persistencia.eliminarCategoria(categoria.getId());
        data.getCategorias().remove(categoria);
    }

    // =========================================================
    // RECURSOS
    // =========================================================

    public synchronized void crearRecurso(
            Recurso recurso
    ) throws Exception {

        validarRecurso(recurso);

        String id = recurso.getId().trim();

        if (existeRecurso(id)) {
            throw new Exception(
                    "Ya existe un recurso con el ID " + id
            );
        }

        /*
         * Confirmamos que la categoría exista
         * dentro del sistema.
         */
        Categoria categoria =
                buscarCategoriaPorId(
                        recurso
                                .getCategoria()
                                .getId()
                );

        Recurso nuevo = new Recurso(id, categoria, recurso.getDescripcion().trim());
        persistencia.insertarRecurso(nuevo);
        recurso.setId(nuevo.getId());
        recurso.setCategoria(nuevo.getCategoria());
        recurso.setDescripcion(nuevo.getDescripcion());
        data.getRecursos().add(recurso);
    }

    public synchronized Recurso buscarRecursoPorId(
            String id
    ) throws Exception {

        if (id == null || id.trim().isEmpty()) {
            throw new Exception(
                    "El ID del recurso es requerido"
            );
        }

        String idBusqueda = id.trim();

        for (Recurso recurso :
                data.getRecursos()) {

            if (recurso.getId()
                    .equals(idBusqueda)) {

                return recurso;
            }
        }

        throw new Exception(
                "Recurso no encontrado"
        );
    }

    public synchronized List<Recurso> listarRecursos() {
        return new ArrayList<>(
                data.getRecursos()
        );
    }

    public synchronized List<Recurso> buscarRecursosPorCategoria(
            Categoria categoria
    ) {

        List<Recurso> resultado =
                new ArrayList<>();

        if (categoria == null) {
            return resultado;
        }

        for (Recurso recurso :
                data.getRecursos()) {

            if (recurso
                    .getCategoria()
                    .equals(categoria)) {

                resultado.add(recurso);
            }
        }

        return resultado;
    }

    public synchronized void modificarRecurso(
            Recurso recurso
    ) throws Exception {

        validarRecurso(recurso);

        Recurso existente =
                buscarRecursoPorId(
                        recurso.getId()
                );

        Categoria categoria =
                buscarCategoriaPorId(
                        recurso
                                .getCategoria()
                                .getId()
                );

        Recurso actualizado = new Recurso(existente.getId(), categoria, recurso.getDescripcion().trim());
        persistencia.actualizarRecurso(actualizado);
        existente.setCategoria(categoria);
        existente.setDescripcion(actualizado.getDescripcion());
        recurso.setCategoria(categoria);
        recurso.setDescripcion(actualizado.getDescripcion());
    }

    public synchronized void eliminarRecurso(
            String id
    ) throws Exception {

        Recurso recurso =
                buscarRecursoPorId(id);

        /*
         * Más adelante ampliaremos esta validación
         * considerando reservas activas.
         */
        for (Reserva reserva : data.getReservas()) {
            if (reserva.getRecursos().contains(recurso)) throw new Exception("No se puede eliminar el recurso porque tiene reservas asociadas");
        }
        persistencia.eliminarRecurso(recurso.getId());
        data.getRecursos().remove(recurso);
    }

    // =========================================================
    // MÉTODOS AUXILIARES
    // =========================================================

    public synchronized ReservaExtraccion extraerReservaConIA(String frase) throws Exception {

        if (frase == null || frase.trim().isEmpty()) {
            throw new Exception("Debe escribir una descripción de la reserva");
        }

        String categoriasDisponibles = data.getCategorias()
                .stream()
                .map(Categoria::getDescripcion)
                .toList()
                .toString();

        return obtenerReservaExtractorService().extraer(
                frase.trim(),
                categoriasDisponibles,
                LocalDate.now().toString()
        );
    }

    private ReservaExtractorService obtenerReservaExtractorService() {

        if (reservaExtractorService == null) {

            OpenAiChatModel modelo = OpenAiChatModel.builder()
                    .baseUrl("http://langchain4j.dev/demo/openai/v1")
                    .apiKey("demo")
                    .modelName("gpt-4o-mini")
                    .build();

            reservaExtractorService =
                    AiServices.create(
                            ReservaExtractorService.class,
                            modelo
                    );
        }

        return reservaExtractorService;
    }

    private boolean existeUsuario(
            String id
    ) {

        for (Usuario usuario :
                data.getUsuarios()) {

            if (usuario.getId().equals(id)) {
                return true;
            }
        }

        return false;
    }

    private boolean existeRecurso(
            String id
    ) {

        for (Recurso recurso :
                data.getRecursos()) {

            if (recurso.getId().equals(id)) {
                return true;
            }
        }

        return false;
    }

    private void validarFuncionario(
            Funcionario funcionario
    ) throws Exception {

        if (funcionario == null) {
            throw new Exception(
                    "Funcionario requerido"
            );
        }

        if (funcionario.getId() == null
                || funcionario
                .getId()
                .trim()
                .isEmpty()) {

            throw new Exception(
                    "El ID es requerido"
            );
        }

        if (funcionario.getNombre() == null
                || funcionario
                .getNombre()
                .trim()
                .isEmpty()) {

            throw new Exception(
                    "El nombre es requerido"
            );
        }

        if (funcionario.getTelefono() == null
                || funcionario
                .getTelefono()
                .trim()
                .isEmpty()) {

            throw new Exception(
                    "El teléfono es requerido"
            );
        }
    }

    private void validarCategoria(
            Categoria categoria
    ) throws Exception {

        if (categoria == null) {
            throw new Exception(
                    "Categoría requerida"
            );
        }

        if (categoria.getDescripcion() == null
                || categoria
                .getDescripcion()
                .trim()
                .isEmpty()) {

            throw new Exception(
                    "La descripción es requerida"
            );
        }
    }

    private void validarRecurso(
            Recurso recurso
    ) throws Exception {

        if (recurso == null) {
            throw new Exception(
                    "Recurso requerido"
            );
        }

        if (recurso.getId() == null
                || recurso
                .getId()
                .trim()
                .isEmpty()) {

            throw new Exception(
                    "El ID del recurso es requerido"
            );
        }

        if (recurso.getCategoria() == null) {
            throw new Exception(
                    "La categoría es requerida"
            );
        }

        if (recurso.getDescripcion() == null
                || recurso
                .getDescripcion()
                .trim()
                .isEmpty()) {

            throw new Exception(
                    "La descripción es requerida"
            );
        }
    }

    private String generarIdCategoria() {

        int mayor = 0;

        for (Categoria categoria :
                data.getCategorias()) {

            String id = categoria.getId();

            if (id != null
                    && id.startsWith("CAT-")) {

                try {

                    int numero =
                            Integer.parseInt(
                                    id.substring(4)
                            );

                    if (numero > mayor) {
                        mayor = numero;
                    }

                } catch (NumberFormatException ignored) {
                    // Se ignoran identificadores
                    // que no sigan el formato esperado.
                }
            }
        }

        return String.format(
                "CAT-%06d",
                mayor + 1
        );
    }

    // =========================================================
    // DATOS INICIALES
    // =========================================================

    // =========================================================
// RESERVAS
// =========================================================

    public synchronized List<Reserva> listarReservas() {
        return new ArrayList<>(data.getReservas());
    }

    public synchronized List<Reserva> listarReservasFuncionario(Funcionario funcionario) {
        List<Reserva> resultado = new ArrayList<>();

        for (Reserva reserva : data.getReservas()) {
            if (reserva.getFuncionario()
                    .equals(funcionario)) {

                resultado.add(reserva);
            }
        }
        return resultado;
    }

    private boolean chocanHorarios(LocalTime inicio1, LocalTime fin1, LocalTime inicio2, LocalTime fin2) {
        return inicio1.isBefore(fin2) && fin1.isAfter(inicio2);
    }

    private boolean recursoDisponible(Recurso recurso, LocalDate fecha, LocalTime inicio, LocalTime fin) {
        for (Reserva reserva : data.getReservas()) {
            if (!reserva.estaActiva()) {
                continue;
            }

            if (!fecha.equals(reserva.getFecha())) {
                continue;
            }

            boolean chocan = chocanHorarios(inicio, fin, reserva.getHoraInicio(), reserva.getHoraFin());

            if (!chocan) {
                continue;
            }

            if (reserva.getRecursos().contains(recurso)) {
                return false;
            }
        }
        return true;
    }

    private Recurso primerRecursoDisponible(Categoria categoria, LocalDate fecha, LocalTime inicio, LocalTime fin) {
        for (Recurso recurso : data.getRecursos()) {
            if (recurso.getCategoria().equals(categoria) && recursoDisponible(recurso, fecha, inicio, fin)) {
                return recurso;
            }
        }
        return null;
    }

    public synchronized void crearReserva(
            Reserva reserva,
            List<Categoria> categorias
    ) throws Exception {
        if (reserva == null) {
            throw new Exception("La reserva es requerida");
        }

        if (reserva.getFuncionario() == null) {
            throw new Exception("El funcionario es requerido");
        }

        if (reserva.getActividad() == null || reserva.getActividad().trim().isEmpty()) {
            throw new Exception("La actividad es requerida");
        }

        if (reserva.getFecha() == null) {
            throw new Exception("La fecha es requerida");
        }

        if (reserva.getHoraInicio() == null || reserva.getHoraFin() == null) {
            throw new Exception("Las horas son requeridas");
        }

        if (!reserva.getHoraInicio().isBefore(reserva.getHoraFin())) {
            throw new Exception("La hora de inicio debe ser anterior a la hora final");
        }

        if (categorias == null || categorias.isEmpty()) {
            throw new Exception("Debe seleccionar al menos una categoría");
        }

        List<Recurso> recursosAsignados = new ArrayList<>();
        List<Categoria> noDisponibles = new ArrayList<>();

        for (Categoria categoria : categorias) {
            Recurso recurso = primerRecursoDisponible(categoria, reserva.getFecha(), reserva.getHoraInicio(), reserva.getHoraFin());

            if (recurso == null) {
                noDisponibles.add(categoria);

            } else {
                recursosAsignados.add(recurso);
            }
        }

        if (!noDisponibles.isEmpty()) {
            StringBuilder mensaje = new StringBuilder("No hay recursos disponibles para:\n");

            for (Categoria categoria : noDisponibles) {
                mensaje.append("- ").append(categoria.getDescripcion()).append("\n");
            }

            throw new Exception(mensaje.toString());
        }

        String id = generarIdReserva();
        Funcionario funcionario = buscarFuncionarioPorId(reserva.getFuncionario().getId());
        Reserva nueva = new Reserva(id, funcionario, reserva.getActividad(), reserva.getFecha(), reserva.getHoraInicio(), reserva.getHoraFin(), recursosAsignados, Reserva.ACTIVA);
        persistencia.insertarReserva(nueva);
        reserva.setId(id);
        reserva.setFuncionario(funcionario);
        reserva.setRecursos(recursosAsignados);
        reserva.setEstado(Reserva.ACTIVA);
        data.getReservas().add(reserva);
    }

    private String generarIdReserva() {
        int mayor = 0;

        for (Reserva reserva : data.getReservas()) {
            String id = reserva.getId();

            if (id != null && id.startsWith("RES-")) {

                try {
                    int numero = Integer.parseInt(id.substring(4));

                    if (numero > mayor) {
                        mayor = numero;
                    }

                } catch (NumberFormatException ignored) {
                }
            }
        }
        return String.format("RES-%06d", mayor + 1);
    }

    public synchronized void cancelarReserva(Reserva reserva) throws Exception {
        if (reserva == null) {
            throw new Exception("Reserva requerida");
        }

        Reserva existente = data.getReservas().stream()
                .filter(almacenada -> almacenada.getId().equals(reserva.getId()))
                .findFirst()
                .orElseThrow(() -> new Exception("Reserva requerida"));
        if (!existente.estaActiva()) {
            throw new Exception("La reserva ya está cancelada");
        }

        LocalDate hoy = LocalDate.now();
        LocalTime ahora = LocalTime.now();

        boolean esFutura = existente.getFecha().isAfter(hoy) || (existente.getFecha().equals(hoy) && existente.getHoraInicio().isAfter(ahora));

        if (!esFutura) {throw new Exception("Solo se pueden cancelar reservas futuras");
        }

        if (!existente.estaActiva()) throw new Exception("La reserva ya estÃ¡ cancelada");
        persistencia.actualizarEstadoReserva(existente.getId(), Reserva.CANCELADA);
        existente.setEstado(Reserva.CANCELADA);
        reserva.setEstado(Reserva.CANCELADA);
    }
}
