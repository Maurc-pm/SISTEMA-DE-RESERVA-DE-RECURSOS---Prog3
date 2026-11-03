package reservas.presentation;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.fail;

class ArquitecturaFrontendTest {
    @Test
    void frontendNoDependeDelServiceConcretoNiDePersistenciaJDBC() throws Exception {
        Path raiz = Path.of("src/main/java");
        Path presentation = raiz.resolve("reservas/presentation");
        Path application = raiz.resolve("reservas/Application.java");
        List<String> prohibidas = List.of("reservas.logic.Service", "reservas.data.", "java.sql");

        try (Stream<Path> archivos = Files.walk(presentation)) {
            for (Path archivo : archivos.filter(path -> path.toString().endsWith(".java")).toList()) {
                verificar(archivo, prohibidas);
            }
        }
        verificar(application, prohibidas);
    }

    private void verificar(Path archivo, List<String> prohibidas) throws Exception {
        String contenido = Files.readString(archivo);
        for (String referencia : prohibidas) {
            if (contenido.contains(referencia)) {
                fail(archivo + " contiene una referencia prohibida: " + referencia);
            }
        }
    }
}
