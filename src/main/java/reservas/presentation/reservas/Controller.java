package reservas.presentation.reservas;

import reservas.logic.Categoria;
import reservas.logic.Funcionario;
import reservas.logic.Reserva;
import reservas.logic.Service;
import reservas.logic.Sesion;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Controller {

    private final View view;
    private final Model model;

    private final DateTimeFormatter formatoFecha =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public Controller(View view, Model model) {

        this.view = view;
        this.model = model;

        view.setModel(model);
        view.setController(this);

        cargarCategorias();
        cargarReservas();
        limpiar();
    }

    // =========================================================
    // AGREGAR CATEGORÍA
    // =========================================================

    public void agregarCategoria() {

        Categoria categoria = view.getCategoriaSeleccionada();

        if (categoria == null) {
            return;
        }

        List<Categoria> seleccionadas =
                new ArrayList<>(model.getCategoriasSeleccionadas());

        if (!seleccionadas.contains(categoria)) {
            seleccionadas.add(categoria);
        }

        model.setCategoriasSeleccionadas(seleccionadas);
    }

    // =========================================================
    // RESERVAR
    // =========================================================

    public void reservar() {
        try {
            Funcionario funcionario = (Funcionario) Sesion.getUsuario();

            Reserva reserva = new Reserva();

            reserva.setFuncionario(funcionario);

            reserva.setActividad(view.getActividad());

            reserva.setFecha(LocalDate.parse(view.getFecha(), formatoFecha));

            reserva.setHoraInicio(LocalTime.parse(view.getHoraInicio()));

            reserva.setHoraFin(LocalTime.parse(view.getHoraFinal()));

            Service.instance().crearReserva(reserva, model.getCategoriasSeleccionadas());

            view.mostrarMensaje("Reserva realizada correctamente");

            cargarReservas();
            limpiar();

        } catch (Exception ex) {
            view.mostrarError(ex.getMessage());
        }
    }

    // =========================================================
    // CANCELAR RESERVA
    // =========================================================

    public void cancelarReserva() {
        try {
            Reserva reserva = model.getCurrent();

            if (reserva == null || reserva.getId() == null || reserva.getId().trim().isEmpty()) {

                throw new Exception("Debe seleccionar una reserva");
            }

            Service.instance().cancelarReserva(reserva);

            view.mostrarMensaje("Reserva cancelada correctamente");

            cargarReservas();
            limpiar();

        } catch (Exception ex) {
            view.mostrarError(ex.getMessage());
        }
    }

    // =========================================================
    // SELECCIONAR RESERVA
    // =========================================================

    public void seleccionar(int fila) {
        if (fila < 0 || fila >= model.getReservas().size()) {
            return;
        }

        Reserva reserva = model.getReservas().get(fila);

        model.setCurrent(reserva);
    }

    // =========================================================
    // LIMPIAR
    // =========================================================

    public void limpiar() {
        model.setCurrent(new Reserva());

        model.setCategoriasSeleccionadas(new ArrayList<>());

        view.limpiarFormulario();
        view.limpiarSeleccionTabla();
    }

    // =========================================================
    // CARGAR CATEGORÍAS
    // =========================================================

    private void cargarCategorias() {
        model.setCategorias(Service.instance().listarCategorias());
    }

    // =========================================================
    // CARGAR RESERVAS DEL FUNCIONARIO
    // =========================================================

    private void cargarReservas() {
        if (!(Sesion.getUsuario() instanceof Funcionario)) {
            model.setReservas(new ArrayList<>());
            return;
        }

        Funcionario funcionario = (Funcionario) Sesion.getUsuario();
        model.setReservas(Service.instance().listarReservasFuncionario(funcionario));
    }
}