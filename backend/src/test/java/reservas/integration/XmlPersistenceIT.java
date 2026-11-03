package reservas.integration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import reservas.support.IsolatedJvm;
import reservas.support.XmlScenario;
import java.nio.file.Path;

class XmlPersistenceIT {
    @TempDir Path directory;
    @Test void guardarYRecargarPreservaEntidadesEstadosRelacionesYCaracteresXML() throws Exception {
        IsolatedJvm.run(directory, XmlScenario.class, "roundtrip");
    }
    @Test void archivoAusenteRetornaNullSinDatosInventados() throws Exception {
        IsolatedJvm.run(directory, XmlScenario.class, "missing");
    }
    @Test void xmlMalformadoProduceErrorEnLugarDeCargarDatosParciales() throws Exception {
        IsolatedJvm.run(directory, XmlScenario.class, "invalid");
    }
    @Test void primeraEjecucionCreaAdministradorYPersiste() throws Exception {
        IsolatedJvm.run(directory, XmlScenario.class, "bootstrap");
    }
    @Test void documentaPerdidaActualDeReferenciasAlEliminarEntidadesReservadas() throws Exception {
        IsolatedJvm.run(directory, XmlScenario.class, "orphans");
    }
}
