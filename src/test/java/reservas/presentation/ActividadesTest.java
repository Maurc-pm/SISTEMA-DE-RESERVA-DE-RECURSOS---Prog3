package reservas.presentation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import reservas.logic.*;
import reservas.presentation.actividades.Controller;
import reservas.presentation.actividades.Model;
import reservas.presentation.actividades.View;
import reservas.support.ServiceTestSupport;
import java.time.LocalDate;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ActividadesTest extends ServiceTestSupport {
    private View view;
    private Model model;
    private Controller controller;
    private Funcionario ana;
    private Categoria sala;
    private final LocalDate lunes = LocalDate.of(2026, 9, 14);
    @BeforeEach void preparar() throws Exception {
        ana = funcionario("F1", "Ana"); sala = categoria("Sala"); recurso("R1", sala); recurso("R2", sala);
        view = mock(View.class); when(view.getFechaReferencia()).thenReturn(lunes.plusDays(2));
        model = new Model(); controller = new Controller(view, model);
    }
    @ParameterizedTest @ValueSource(ints = {0, 2, 6})
    void calculaLunesYDomingoDesdeCualquierDiaDeSemana(int dia) {
        when(view.getFechaReferencia()).thenReturn(lunes.plusDays(dia)); controller.cargar();
        assertEquals(7, model.getDias().size());
        for (int i=0; i<7; i++) assertEquals(lunes.plusDays(i), model.getDias().get(i));
        assertEquals(24, model.getMatriz().length);
        for (Celda[] fila : model.getMatriz()) {
            assertEquals(7, fila.length);
            for (Celda celda : fila) { assertFalse(celda.isOcupada()); assertEquals("", celda.getTexto()); }
        }
        verify(view, never()).mostrarError(any());
    }
    @Test void actividadesSeColocanEnDiasYHorasCorrectos() throws Exception {
        Reserva primera = reserva(ana, lunes, "08:30", "10:00"); primera.setActividad("Clase");
        Reserva ultima = reserva(ana, lunes.plusDays(6), "23:00", "23:59"); ultima.setActividad("Cierre");
        service.crearReserva(primera, List.of(sala)); service.crearReserva(ultima, List.of(sala));
        controller.cargar();
        for (int h=0; h<24; h++) for (int d=0; d<7; d++) {
            String esperado = d==0 && (h==8 || h==9) ? "Clase (Ana)" : d==6 && h==23 ? "Cierre (Ana)" : "";
            assertEquals(esperado, model.getMatriz()[h][d].getTexto(), "Hora/día " + h + "/" + d);
            assertEquals(!esperado.isEmpty(), model.getMatriz()[h][d].isOcupada());
        }
        verify(view, never()).mostrarError(any());
    }
    @Test void variasActividadesEnMismaCeldaSeConservan() throws Exception {
        Reserva r = reserva(ana, lunes, "08:00", "09:00"); r.setActividad("Clase"); service.crearReserva(r, List.of(sala));
        Funcionario luis = funcionario("F2", "Luis");
        Reserva otra = reserva(luis, lunes, "08:00", "09:00"); otra.setActividad("Reunión"); service.crearReserva(otra, List.of(sala));
        controller.cargar();
        assertEquals(List.of("Clase (Ana)", "Reunión (Luis)"), model.getMatriz()[8][0].getTextos());
        verify(view, never()).mostrarError(any());
    }
    @Test void ignoraSemanasExternasYReservasCanceladas() throws Exception {
        service.crearReserva(reserva(ana, lunes.minusDays(1), "08:00", "09:00"), List.of(sala));
        service.crearReserva(reserva(ana, lunes.plusDays(7), "08:00", "09:00"), List.of(sala));
        Reserva cancelada = reserva(ana, lunes.plusDays(4), "08:00", "09:00");
        service.crearReserva(cancelada, List.of(sala)); service.cancelarReserva(cancelada);
        controller.cargar();
        for (Celda[] fila : model.getMatriz()) for (Celda celda : fila) assertFalse(celda.isOcupada());
        verify(view, never()).mostrarError(any());
    }
    @Test void semanaPuedeCruzarAnio() {
        when(view.getFechaReferencia()).thenReturn(LocalDate.of(2027,1,1)); controller.cargar();
        assertEquals(LocalDate.of(2026,12,28), model.getDias().get(0));
        assertEquals(LocalDate.of(2027,1,3), model.getDias().get(6));
    }
    @Test void fechaDeReferenciaObligatoria() {
        when(view.getFechaReferencia()).thenReturn(null); controller.cargar();
        verify(view).mostrarError("Debe seleccionar una fecha de referencia"); assertTrue(model.getDias().isEmpty());
    }
}
