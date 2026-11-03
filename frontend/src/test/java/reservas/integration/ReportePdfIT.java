package reservas.integration;

import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import reservas.support.IsolatedJvm;
import reservas.support.PdfScenario;
import java.nio.file.*;
import static org.junit.jupiter.api.Assertions.*;

class ReportePdfIT {
    @TempDir Path directory;
    @ParameterizedTest(name = "PDF real: {0}")
    @CsvSource({
            "reservas,Reporte de Reservas",
            "funcionarios,Reporte de Funcionarios",
            "categorias,Reporte de Categorías",
            "recursos,Reporte de Recursos",
            "calendarizacion,Calendarización de Recursos",
            "actividades,Programación Semanal de Actividades",
            "estadisticas_recursos,Recursos reservados por categoría",
            "estadisticas_actividades,Actividades programadas por semana"
    })
    void generaPdfLegibleConContenidoRealEnDirectorioTemporal(String reporte, String titulo) throws Exception {
        IsolatedJvm.run(directory, PdfScenario.class, reporte, titulo);
        assertTrue(Files.isRegularFile(directory.resolve(reporte + ".pdf")));
        assertTrue(Files.size(directory.resolve(reporte + ".pdf")) > 0);
    }
}
