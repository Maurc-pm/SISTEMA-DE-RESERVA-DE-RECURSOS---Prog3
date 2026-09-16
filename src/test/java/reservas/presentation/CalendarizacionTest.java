package reservas.presentation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import reservas.logic.*;
import reservas.presentation.calendarizacion.Controller;
import reservas.presentation.calendarizacion.Model;
import reservas.presentation.calendarizacion.View;
import reservas.support.FixedTime;
import reservas.support.ServiceTestSupport;
import javax.swing.JPanel;
import java.time.LocalDate;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CalendarizacionTest extends ServiceTestSupport {
    private View view;
    private Model model;
    private Controller controller;
    private Funcionario ana;
    private Categoria sala;
    private Recurso r1, r2;
    private final LocalDate fecha = FixedTime.TODAY;

    @BeforeEach void preparar() throws Exception {
        ana = funcionario("F1", "Ana Pérez"); sala = categoria("Sala");
        r1 = recurso("R1", sala); r2 = recurso("R2", sala);
        recurso("P1", categoria("Equipo"));
        view = mock(View.class); when(view.getPanel()).thenReturn(mock(JPanel.class));
        when(view.getFecha()).thenReturn(fecha); when(view.getCategoriaSeleccionada()).thenReturn(sala);
        model = new Model(); controller = new Controller(view, model);
    }
    @Test void fechaSinReservasGenera24HorasYRecursosDeLaCategoria() {
        controller.cargar();
        assertEquals(List.of(r1, r2), model.getRecursos());
        assertEquals(24, model.getMatriz().length);
        for (Celda[] fila : model.getMatriz()) {
            assertEquals(2, fila.length);
            for (Celda celda : fila) { assertFalse(celda.isOcupada()); assertEquals("", celda.getTexto()); }
        }
        verify(view, never()).mostrarError(any());
    }
    @ParameterizedTest @CsvSource({"08:00,10:00,8,9", "08:30,10:15,8,10", "00:00,01:00,0,0", "23:00,23:59,23,23"})
    void reservaOcupaSoloHorasCorrespondientesConActividadYResponsable(String inicio, String fin, int primera, int ultima) throws Exception {
        Reserva r = reserva(ana, fecha, inicio, fin); service.crearReserva(r, List.of(sala));
        controller.cargar();
        for (int hora = 0; hora < 24; hora++) {
            boolean ocupada = hora >= primera && hora <= ultima;
            assertEquals(ocupada, model.getMatriz()[hora][0].isOcupada(), "Hora " + hora);
            assertEquals(ocupada ? "Reunión - Ana Pérez" : "", model.getMatriz()[hora][0].getTexto());
            assertFalse(model.getMatriz()[hora][1].isOcupada());
        }
        verify(view, never()).mostrarError(any());
    }
    @Test void otraFechaOtroRecursoYCanceladaNoOcupanLaMatriz() throws Exception {
        Reserva ayer = reserva(ana, fecha.minusDays(1), "08:00", "09:00"); service.crearReserva(ayer, List.of(sala));
        Reserva cancelada = reserva(ana, fecha, "13:00", "14:00");
        service.crearReserva(cancelada, List.of(sala)); service.cancelarReserva(cancelada);
        Categoria equipo = service.buscarCategorias("Equipo").get(0);
        service.crearReserva(reserva(ana, fecha, "08:00", "09:00"), List.of(equipo));
        controller.cargar();
        for (Celda[] fila : model.getMatriz()) for (Celda celda : fila) assertFalse(celda.isOcupada());
        verify(view, never()).mostrarError(any());
    }
    @Test void categoriaSinRecursosProduce24FilasSinColumnas() throws Exception {
        Categoria vacia = categoria("Vacía");
        when(view.getCategoriaSeleccionada()).thenReturn(vacia);
        controller.cargar(); assertEquals(24, model.getMatriz().length);
        for (Celda[] fila : model.getMatriz()) assertEquals(0, fila.length);
        verify(view, never()).mostrarError(any());
    }
    @Test void fechaObligatoriaSeInformaSinConstruirMatriz() {
        when(view.getFecha()).thenReturn(null); controller.cargar();
        verify(view).mostrarError("Debe seleccionar una fecha");
        assertTrue(model.getRecursos().isEmpty());
    }
    @Test void categoriaObligatoriaSeInformaSinConstruirMatriz() {
        when(view.getCategoriaSeleccionada()).thenReturn(null); controller.cargar();
        verify(view).mostrarError("Debe seleccionar una categoría");
        assertTrue(model.getRecursos().isEmpty());
    }
}
