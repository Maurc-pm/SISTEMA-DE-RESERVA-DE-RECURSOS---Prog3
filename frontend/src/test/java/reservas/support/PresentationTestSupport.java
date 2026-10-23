package reservas.support;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import reservas.presentation.Servicio;

public abstract class PresentationTestSupport extends ServiceTestSupport {
    @BeforeEach
    final void inicializarServicioFrontend() {
        Servicio.set(service);
    }

    @AfterEach
    final void limpiarServicioFrontend() {
        Servicio.set(null);
    }
}
