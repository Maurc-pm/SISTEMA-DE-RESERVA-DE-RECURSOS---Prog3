package reservas.logic;

import reservas.data.Data;
import reservas.data.XmlStorage;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.time.LocalTime;

public class Service {

    private static final Service INSTANCE = new Service();

    private final Data data;

    private Service() {

        try {

            Data dataCargada = XmlStorage.cargar();

            if (dataCargada != null) {

                // Ya existe reservas.xml
                data = dataCargada;

            } else {

                // Primera ejecución
                data = new Data();

                cargarDatosIniciales();

                XmlStorage.guardar(data);
            }

        } catch (Exception e) {

            throw new RuntimeException(
                    "Error al cargar los datos XML",
                    e
            );
        }
    }
    public static Service instance() {
        return INSTANCE;
    }

    // =========================================================
    // LOGIN
    // =========================================================

    public Usuario login(String id, String clave) throws Exception {

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

    public void cambiarClave(
            Usuario usuario,
            String claveActual,
            String claveNueva
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

        if (!usuario.getClave().equals(claveActual)) {
            throw new Exception("La clave actual es incorrecta");
        }

        usuario.setClave(claveNueva.trim());

        guardarCambios();
    }

    // =========================================================
    // FUNCIONARIOS
    // =========================================================

    public void crearFuncionario(
            Funcionario funcionario
    ) throws Exception {

        validarFuncionario(funcionario);

        String id = funcionario.getId().trim();

        if (existeUsuario(id)) {
            throw new Exception(
                    "Ya existe un usuario con el ID " + id
            );
        }

        funcionario.setId(id);
        funcionario.setNombre(
                funcionario.getNombre().trim()
        );
        funcionario.setTelefono(
                funcionario.getTelefono().trim()
        );

        /*
         * Regla del proyecto:
         * al crear un funcionario, su clave inicial
         * debe ser igual a su ID.
         */
        funcionario.setClave(id);
        funcionario.setRol(Usuario.FUNCIONARIO);

        data.getUsuarios().add(funcionario);

        guardarCambios();
    }

    public Funcionario buscarFuncionarioPorId(
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

    public List<Funcionario> buscarFuncionarios(
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

    public List<Funcionario> listarFuncionarios() {

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

    public void modificarFuncionario(
            Funcionario funcionario
    ) throws Exception {

        validarFuncionario(funcionario);

        Funcionario existente =
                buscarFuncionarioPorId(
                        funcionario.getId()
                );

        existente.setNombre(
                funcionario.getNombre().trim()
        );

        existente.setTelefono(
                funcionario.getTelefono().trim()
        );

        guardarCambios();
    }

    public void eliminarFuncionario(
            String id
    ) throws Exception {

        Funcionario funcionario =
                buscarFuncionarioPorId(id);

        boolean eliminado =
                data.getUsuarios()
                        .remove(funcionario);

        if (!eliminado) {
            throw new Exception(
                    "No fue posible eliminar el funcionario"
            );
        }

        guardarCambios();
    }

    // =========================================================
    // CATEGORÍAS
    // =========================================================

    public void crearCategoria(
            Categoria categoria
    ) throws Exception {

        validarCategoria(categoria);

        categoria.setDescripcion(
                categoria.getDescripcion().trim()
        );

        categoria.setId(
                generarIdCategoria()
        );

        data.getCategorias().add(categoria);

        guardarCambios();
    }

    public Categoria buscarCategoriaPorId(
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

    public List<Categoria> buscarCategorias(
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

    public List<Categoria> listarCategorias() {
        return new ArrayList<>(
                data.getCategorias()
        );
    }

    public void modificarCategoria(
            Categoria categoria
    ) throws Exception {

        validarCategoria(categoria);

        Categoria existente =
                buscarCategoriaPorId(
                        categoria.getId()
                );

        existente.setDescripcion(
                categoria
                        .getDescripcion()
                        .trim()
        );

        guardarCambios();
    }

    public void eliminarCategoria(
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

        data.getCategorias()
                .remove(categoria);

        guardarCambios();
    }

    // =========================================================
    // RECURSOS
    // =========================================================

    public void crearRecurso(
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

        recurso.setId(id);
        recurso.setCategoria(categoria);
        recurso.setDescripcion(
                recurso
                        .getDescripcion()
                        .trim()
        );

        data.getRecursos().add(recurso);

        guardarCambios();
    }

    public Recurso buscarRecursoPorId(
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

    public List<Recurso> listarRecursos() {
        return new ArrayList<>(
                data.getRecursos()
        );
    }

    public List<Recurso> buscarRecursosPorCategoria(
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

    public void modificarRecurso(
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

        existente.setCategoria(categoria);
        existente.setDescripcion(
                recurso
                        .getDescripcion()
                        .trim()
        );

        guardarCambios();
    }

    public void eliminarRecurso(
            String id
    ) throws Exception {

        Recurso recurso =
                buscarRecursoPorId(id);

        /*
         * Más adelante ampliaremos esta validación
         * considerando reservas activas.
         */
        data.getRecursos().remove(recurso);

        guardarCambios();
    }

    // =========================================================
    // MÉTODOS AUXILIARES
    // =========================================================

    private void guardarCambios() throws Exception {
        XmlStorage.guardar(data);
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

    private void cargarDatosIniciales() {

        Usuario administrador =
                new Usuario(
                        "admin",
                        "admin",
                        Usuario.ADMINISTRADOR
                );

        data.getUsuarios()
                .add(administrador);

    }

    // =========================================================
// RESERVAS
// =========================================================

    public List<Reserva> listarReservas() {
        return new ArrayList<>(data.getReservas());
    }

    public List<Reserva> listarReservasFuncionario(Funcionario funcionario) {
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

    public void crearReserva(
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

        reserva.setId(generarIdReserva());
        reserva.setRecursos(recursosAsignados);
        reserva.setEstado(Reserva.ACTIVA);
        data.getReservas().add(reserva);

        guardarCambios();
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

    public void cancelarReserva(Reserva reserva) throws Exception {
        if (reserva == null) {
            throw new Exception("Reserva requerida");
        }

        if (!reserva.estaActiva()) {
            throw new Exception("La reserva ya está cancelada");
        }

        LocalDate hoy = LocalDate.now();
        LocalTime ahora = LocalTime.now();

        boolean esFutura = reserva.getFecha().isAfter(hoy) || (reserva.getFecha().equals(hoy) && reserva.getHoraInicio().isAfter(ahora));

        if (!esFutura) {throw new Exception("Solo se pueden cancelar reservas futuras");
        }

        reserva.setEstado(Reserva.CANCELADA);

        guardarCambios();
    }
}