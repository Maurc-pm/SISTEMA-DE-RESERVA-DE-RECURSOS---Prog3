package reservas.protocol;

import org.junit.jupiter.api.Test;
import reservas.logic.*;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DespachadorTest {
    @Test void loginDevuelveElUsuario() throws Exception {
        IService service = mock(IService.class);
        Usuario usuario = new Usuario("admin", "clave", Usuario.ADMINISTRADOR);
        when(service.login("admin", "clave")).thenReturn(usuario);
        Response response = Despachador.atender(service, new Request(Operacion.LOGIN, "admin", "clave"));
        assertTrue(response.isOk());
        assertSame(usuario, response.getValor());
    }

    @Test void crearReservaDevuelveElObjetoRecibido() throws Exception {
        IService service = mock(IService.class);
        Reserva reserva = new Reserva();
        Request request = new Request(Operacion.CREAR_RESERVA, reserva, List.of());
        Response response = Despachador.atender(service, request);
        assertTrue(response.isOk());
        assertSame(reserva, response.getValor());
    }

    @Test void excepcionSeConvierteEnError() throws Exception {
        IService service = mock(IService.class);
        doThrow(new Exception("La actividad es requerida")).when(service).crearReserva(any(), anyList());
        Response response = Despachador.atender(service,
                new Request(Operacion.CREAR_RESERVA, new Reserva(), List.of()));
        assertFalse(response.isOk());
        assertEquals("La actividad es requerida", response.getMensajeError());
    }

    @Test void estadisticasSeDelegan() throws Exception {
        IService service = mock(IService.class);
        LocalDate desde = LocalDate.of(2026, 8, 1), hasta = LocalDate.of(2026, 9, 1);
        Map<String, Integer> recursos = new LinkedHashMap<>(Map.of("Sala", 5));
        Map<String, Integer> semanas = new LinkedHashMap<>(Map.of("03/08/2026 - 09/08/2026", 2));
        when(service.estadisticaRecursosPorCategoria(desde, hasta)).thenReturn(recursos);
        when(service.estadisticaActividadesPorSemana(desde, hasta)).thenReturn(semanas);
        assertSame(recursos, Despachador.atender(service,
                new Request(Operacion.ESTADISTICA_RECURSOS_POR_CATEGORIA, desde, hasta)).getValor());
        assertSame(semanas, Despachador.atender(service,
                new Request(Operacion.ESTADISTICA_ACTIVIDADES_POR_SEMANA, desde, hasta)).getValor());
    }

    @Test void errorDeValidacionDeEstadisticasLlegaAlCliente() throws Exception {
        IService service = mock(IService.class);
        when(service.estadisticaRecursosPorCategoria(null, null))
                .thenThrow(new Exception("Debe indicar las fechas desde y hasta"));
        Response response = Despachador.atender(service,
                new Request(Operacion.ESTADISTICA_RECURSOS_POR_CATEGORIA, null, null));
        assertFalse(response.isOk());
        assertEquals("Debe indicar las fechas desde y hasta", response.getMensajeError());
    }
}
