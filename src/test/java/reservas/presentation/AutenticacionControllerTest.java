package reservas.presentation;

import org.junit.jupiter.api.Test;
import reservas.logic.*;
import reservas.support.ServiceTestSupport;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AutenticacionControllerTest extends ServiceTestSupport {
    @Test void loginCorrectoIniciaSesionYCierraDialogo() throws Exception {
        Funcionario f = funcionario("F1", "Ana");
        var view = mock(reservas.presentation.login.View.class);
        var model = new reservas.presentation.login.Model();
        when(view.getId()).thenReturn("F1"); when(view.getClave()).thenReturn("F1");
        new reservas.presentation.login.Controller(view, model).login();
        assertSame(f, Sesion.getUsuario()); assertSame(f, model.getCurrent()); assertTrue(Sesion.isLogged());
        verify(view).dispose(); verify(view, never()).mostrarError(any());
    }
    @Test void loginIncorrectoNoIniciaSesionNiCierraDialogo() throws Exception {
        funcionario("F1", "Ana"); var view = mock(reservas.presentation.login.View.class);
        when(view.getId()).thenReturn("F1"); when(view.getClave()).thenReturn("incorrecta");
        new reservas.presentation.login.Controller(view, new reservas.presentation.login.Model()).login();
        assertFalse(Sesion.isLogged()); verify(view).mostrarError("ID o clave incorrectos"); verify(view, never()).dispose();
    }
    @Test void cancelarLoginCierraSesion() throws Exception {
        Sesion.setUsuario(funcionario("F1", "Ana")); var view = mock(reservas.presentation.login.View.class);
        new reservas.presentation.login.Controller(view, new reservas.presentation.login.Model()).cancelar();
        assertNull(Sesion.getUsuario()); verify(view).dispose();
    }
    @Test void cambioClaveDesdeControladorPermiteNuevoLogin() throws Exception {
        Funcionario f = funcionario("F1", "Ana"); var view = mock(reservas.presentation.cambiarclave.View.class);
        when(view.getId()).thenReturn("F1"); when(view.getClaveActual()).thenReturn("F1");
        when(view.getClaveNueva()).thenReturn("nueva"); when(view.getConfirmarClave()).thenReturn("nueva");
        new reservas.presentation.cambiarclave.Controller(view, new reservas.presentation.cambiarclave.Model()).cambiarClave();
        assertSame(f, service.login("F1", "nueva")); verify(view).cerrar(); verify(view, never()).mostrarError(any());
    }
}
