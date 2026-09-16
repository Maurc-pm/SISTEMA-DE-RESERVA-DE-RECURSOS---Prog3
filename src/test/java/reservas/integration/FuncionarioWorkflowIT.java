package reservas.integration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import reservas.support.IsolatedJvm;
import reservas.support.WorkflowScenario;
import java.nio.file.Path;

class FuncionarioWorkflowIT {
    @TempDir Path directory;
    @Test void crearAutenticarModificarRecargarEliminarYComprobarConsistencia() throws Exception {
        IsolatedJvm.run(directory, WorkflowScenario.class, "funcionario");
        IsolatedJvm.run(directory, WorkflowScenario.class, "funcionario-reload");
        IsolatedJvm.run(directory, WorkflowScenario.class, "funcionario-delete");
        IsolatedJvm.run(directory, WorkflowScenario.class, "funcionario-deleted-reload");
    }
}
