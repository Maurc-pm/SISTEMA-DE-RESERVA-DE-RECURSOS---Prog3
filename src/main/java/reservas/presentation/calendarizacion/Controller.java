package reservas.presentation.calendarizacion;

import reservas.logic.Categoria;
import reservas.logic.Recurso;
import reservas.logic.Reserva;
import reservas.logic.Service;
import reservas.presentation.Celda;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class Controller {

    private static final int HORAS_DIA = 24;

    private final View view;
    private final Model model;

    public Controller(View view, Model model) {

        this.view = view;
        this.model = model;

        view.setModel(model);
        view.setController(this);

        cargarCategorias();

        /**
         * mientras esta pestaña permanece oculta, se refresca
         * el combo al volver a mostrarla.
         */
        view.getPanel().addHierarchyListener(e -> {

            if ((e.getChangeFlags()
                    & java.awt.event.HierarchyEvent.SHOWING_CHANGED) != 0
                    && view.getPanel().isShowing()) {

                cargarCategorias();
            }
        });
    }

    // =========================================================
    // CARGAR MATRIZ
    // =========================================================

    public void cargar() {

        try {

            LocalDate fecha = view.getFecha();
            Categoria categoria = view.getCategoriaSeleccionada();

            if (fecha == null) {
                throw new Exception("Debe seleccionar una fecha");
            }

            if (categoria == null
                    || categoria.getId() == null
                    || categoria.getId().trim().isEmpty()) {

                throw new Exception("Debe seleccionar una categoría");
            }

            List<Recurso> recursos =
                    Service.instance()
                            .buscarRecursosPorCategoria(categoria);

            List<Reserva> reservas =
                    Service.instance().listarReservas();

            Celda[][] matriz =
                    construirMatriz(fecha, recursos, reservas);

            model.setMatriz(recursos, matriz);

        } catch (Exception ex) {

            view.mostrarError(ex.getMessage());
        }
    }

    // =========================================================
    // IMPRIMIR (pendiente: se implementará el reporte PDF
    // en una fase posterior del proyecto)
    // =========================================================

    public void imprimir() {

        view.mostrarMensaje(
                "La generación del reporte PDF se implementará "
                        + "en una fase posterior."
        );
    }

    // =========================================================
    // CONSTRUCCIÓN DE LA MATRIZ HORA x RECURSO
    // =========================================================

    private Celda[][] construirMatriz(
            LocalDate fecha,
            List<Recurso> recursos,
            List<Reserva> reservas
    ) {

        Celda[][] matriz = new Celda[HORAS_DIA][recursos.size()];

        for (int hora = 0; hora < HORAS_DIA; hora++) {
            for (int columna = 0; columna < recursos.size(); columna++) {
                matriz[hora][columna] = new Celda();
            }
        }

        for (Reserva reserva : reservas) {

            if (!reserva.estaActiva()) {
                continue;
            }

            if (!fecha.equals(reserva.getFecha())) {
                continue;
            }

            String texto =
                    reserva.getActividad()
                            + " - "
                            + reserva.getFuncionario().getNombre();

            for (Recurso recurso : reserva.getRecursos()) {

                int columna = recursos.indexOf(recurso);

                if (columna < 0) {
                    continue;
                }

                marcarHorasOcupadas(
                        matriz,
                        columna,
                        reserva.getHoraInicio(),
                        reserva.getHoraFin(),
                        texto
                );
            }
        }

        return matriz;
    }

    private void marcarHorasOcupadas(
            Celda[][] matriz,
            int columna,
            LocalTime horaInicio,
            LocalTime horaFin,
            String texto
    ) {

        for (int hora = 0; hora < HORAS_DIA; hora++) {

            LocalTime inicioSlot = LocalTime.of(hora, 0);

            LocalTime finSlot =
                    hora == HORAS_DIA - 1
                            ? LocalTime.MAX
                            : LocalTime.of(hora + 1, 0);

            boolean ocupaEsteSlot =
                    horaInicio.isBefore(finSlot)
                            && horaFin.isAfter(inicioSlot);

            if (ocupaEsteSlot) {
                matriz[hora][columna].agregar(texto);
            }
        }
    }

    private void cargarCategorias() {

        model.setCategorias(
                Service.instance().listarCategorias()
        );
    }
}
