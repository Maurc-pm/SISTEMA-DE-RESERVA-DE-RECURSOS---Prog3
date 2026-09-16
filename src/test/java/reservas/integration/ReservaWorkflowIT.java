package reservas.integration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import reservas.support.IsolatedJvm;
import reservas.support.WorkflowScenario;
import java.nio.file.Path;

class ReservaWorkflowIT {
    @TempDir Path directory;
    @Test void crearSolaparAgotarCancelarReutilizarYRecargarEnOtraJVM() throws Exception {
        IsolatedJvm.run(directory, WorkflowScenario.class, "reservas");
        IsolatedJvm.run(directory, WorkflowScenario.class, "reservas-reload");
    }
}
