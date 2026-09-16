package reservas.support;

import java.io.File;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import static org.junit.jupiter.api.Assertions.*;

/** Una JVM nueva con cwd temporal: las rutas relativas productivas jamás llegan al proyecto. */
public final class IsolatedJvm {
    private IsolatedJvm() { }

    public static void run(Path directory, Class<?> scenario, String... args) throws Exception {
        Path cwd = directory.toRealPath();
        String rawClasspath = System.getProperty("surefire.test.class.path", System.getProperty("java.class.path"));
        String classpath = Arrays.stream(rawClasspath.split(java.util.regex.Pattern.quote(File.pathSeparator)))
                .map(p -> Path.of(p).toAbsolutePath().normalize().toString()).collect(Collectors.joining(File.pathSeparator));
        String executable = System.getProperty("os.name").startsWith("Windows") ? "java.exe" : "java";
        List<String> command = new ArrayList<>(List.of(
                Path.of(System.getProperty("java.home"), "bin", executable).toString(),
                "-Djava.awt.headless=true", "-Dfile.encoding=UTF-8",
                "-Dstdout.encoding=UTF-8", "-Dstderr.encoding=UTF-8", "-cp", classpath,
                scenario.getName(), cwd.toString()));
        command.addAll(List.of(args));
        Path log = Files.createTempFile(cwd, "scenario-", ".log");
        Process process = new ProcessBuilder(command).directory(cwd.toFile())
                .redirectErrorStream(true).redirectOutput(log.toFile()).start();
        try {
            assertTrue(process.waitFor(45, TimeUnit.SECONDS), "La JVM aislada excedió 45 segundos: " + command);
            assertEquals(0, process.exitValue(), () -> {
                try { return new String(Files.readAllBytes(log), java.nio.charset.StandardCharsets.UTF_8); } catch (Exception e) { return e.toString(); }
            });
        } finally {
            if (process.isAlive()) { process.destroyForcibly(); process.waitFor(5, TimeUnit.SECONDS); }
        }
    }

    /** Se llama antes de inicializar Service/XmlStorage en el proceso hijo. */
    public static void assertDirectory(String expected) throws Exception {
        assertEquals(Path.of(expected).toRealPath(), Path.of("").toRealPath());
        assertTrue(java.awt.GraphicsEnvironment.isHeadless(), "Nunca abrir visores o ventanas en integración");
    }
}
