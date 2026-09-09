package reservas.presentation.actividades;

import reservas.logic.Reserva;
import reservas.logic.Service;
import reservas.logic.Celda;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

public class Controller {

    private static final int HORAS_DIA = 24;
    private static final int DIAS_SEMANA = 7;

    private final View view;
    private final Model model;

    public Controller(View view, Model model) {

        this.view = view;
        this.model = model;

        view.setModel(model);
        view.setController(this);
    }

    // =========================================================
    // CARGAR MATRIZ DE LA SEMANA
    // =========================================================

    public void cargar() {

        try {

            LocalDate referencia = view.getFechaReferencia();

            if (referencia == null) {
                throw new Exception(
                        "Debe seleccionar una fecha de referencia"
                );
            }

            LocalDate lunes =
                    referencia.with(
                            TemporalAdjusters.previousOrSame(
                                    DayOfWeek.MONDAY
                            )
                    );

            List<LocalDate> dias = new ArrayList<>();

            for (int i = 0; i < DIAS_SEMANA; i++) {
                dias.add(lunes.plusDays(i));
            }

            List<Reserva> reservas =
                    Service.instance().listarReservas();

            Celda[][] matriz =
                    construirMatriz(dias, reservas);

            model.setMatriz(dias, matriz);

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
    // CONSTRUCCIÓN DE LA MATRIZ HORA x DÍA
    // =========================================================

    private Celda[][] construirMatriz(
            List<LocalDate> dias,
            List<Reserva> reservas
    ) {

        Celda[][] matriz = new Celda[HORAS_DIA][DIAS_SEMANA];

        for (int hora = 0; hora < HORAS_DIA; hora++) {
            for (int dia = 0; dia < DIAS_SEMANA; dia++) {
                matriz[hora][dia] = new Celda();
            }
        }

        for (Reserva reserva : reservas) {

            if (!reserva.estaActiva()
                    || reserva.getFecha() == null) {
                continue;
            }

            int columna = dias.indexOf(reserva.getFecha());

            if (columna < 0) {
                continue;
            }

            String texto =
                    reserva.getActividad()
                            + " ("
                            + reserva.getFuncionario().getNombre()
                            + ")";

            marcarHorasOcupadas(
                    matriz,
                    columna,
                    reserva.getHoraInicio(),
                    reserva.getHoraFin(),
                    texto
            );
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
}
