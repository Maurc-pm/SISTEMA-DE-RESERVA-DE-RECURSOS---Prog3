package reservas.presentation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import reservas.ai.ReservaExtraccion;
import reservas.ai.ReservaExtractorService;
import reservas.logic.*;
import reservas.presentation.reservas.Controller;
import reservas.presentation.reservas.Model;
import reservas.presentation.reservas.View;
import reservas.support.ServiceTestSupport;
import java.time.LocalDate;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReservaIAPresentationTest extends ServiceTestSupport {
    private View view;
    private Model model;
    private Controller controller;
    private ReservaExtractorService extractor;
    private Categoria sala;
    @BeforeEach void preparar() throws Exception {
        sala = categoria("Sala"); extractor = mock(ReservaExtractorService.class); extractor(extractor);
        view = mock(View.class); when(view.getTextoIA()).thenReturn("Reservar una sala");
        model = new Model(); controller = new Controller(view, model);
    }
    private void respuesta(ReservaExtraccion respuesta) {
        when(extractor.extraer(anyString(), anyString(), anyString())).thenReturn(respuesta);
    }
    @Test void datosValidosInterpretanFechaISOYResuelvenCategoriaSinDistinguirMayusculas() {
        respuesta(new ReservaExtraccion("Clase", "2026-09-17", "08:30", "09:45", List.of(" sALA ")));
        controller.interpretarConIA();
        verify(view).llenarFormularioIA("Clase", LocalDate.of(2026,9,17), "08:30", "09:45");
        assertEquals(List.of(sala), model.getCategoriasSeleccionadas());
        assertTrue(service.listarReservas().isEmpty()); verify(view, never()).mostrarError(any());
    }
    @Test void respuestaIncompletaNoInventaDatosNiCreaReserva() {
        respuesta(new ReservaExtraccion(null, null, null, null, null)); controller.interpretarConIA();
        verify(view).llenarFormularioIA(null, null, null, null);
        assertTrue(model.getCategoriasSeleccionadas().isEmpty()); assertTrue(service.listarReservas().isEmpty());
        verify(view, never()).mostrarError(any());
    }
    @Test void categoriaDesconocidaNoSeAgregaPeroConservaConocida() {
        respuesta(new ReservaExtraccion("Clase", null, null, null, List.of("No existe", "Sala")));
        controller.interpretarConIA(); assertEquals(List.of(sala), model.getCategoriasSeleccionadas());
        verify(view, never()).mostrarError(any());
    }
    @Test void fechaInvalidaInformaErrorSinLlenarFormulario() {
        respuesta(new ReservaExtraccion("Clase", "17/09/2026", "08:00", "09:00", List.of("Sala")));
        controller.interpretarConIA();
        verify(view).mostrarError(contains("could not be parsed"));
        verify(view, never()).llenarFormularioIA(any(), any(), any(), any());
        assertTrue(model.getCategoriasSeleccionadas().isEmpty());
    }
    @ParameterizedTest @ValueSource(strings = {"25:00", "hora inválida"})
    void horaInvalidaSeRechazaAlReservarSinCrearDatos(String hora) throws Exception {
        Funcionario f = funcionario("F1", "Ana"); Sesion.setUsuario(f); recurso("R1", sala);
        model.setCategoriasSeleccionadas(List.of(sala));
        when(view.getActividad()).thenReturn("Clase"); when(view.getHoraInicio()).thenReturn(hora);
        when(view.getHoraFinal()).thenReturn("10:00"); when(view.getFecha()).thenReturn(LocalDate.of(2026,9,17));
        controller.reservar(); verify(view).mostrarError(anyString());
        assertTrue(service.listarReservas().isEmpty());
    }
    @Test void agregarCategoriaNoDuplicaSeleccion() {
        when(view.getCategoriaSeleccionada()).thenReturn(sala);
        controller.agregarCategoria(); controller.agregarCategoria();
        assertEquals(List.of(sala), model.getCategoriasSeleccionadas());
    }
}
