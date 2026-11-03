package reservas.support;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import reservas.logic.Sesion;
import reservas.presentation.Servicio;

public abstract class PresentationTestSupport extends ServiceTestSupport {
    @BeforeEach
    final void inicializarServicioFrontend() {
        Sesion.logout();
        Servicio.set(service);
    }

    @AfterEach
    final void limpiarServicioFrontend() {
        Sesion.logout();
        Servicio.set(null);
    }
}