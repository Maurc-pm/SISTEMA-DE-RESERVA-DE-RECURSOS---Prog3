package reservas.protocol;

import org.junit.jupiter.api.Test;
import reservas.logic.*;
import java.util.List;
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
}
