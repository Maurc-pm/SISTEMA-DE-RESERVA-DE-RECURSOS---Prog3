package reservas.data;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class Database {
    private static final String ARCHIVO_NO_ENCONTRADO =
            "No se encontró db.properties. Copie db.properties.example como db.properties y escriba la clave.";

    private final String url;
    private final String user;
    private final String password;

    public Database(String urlKey) {
        Properties properties = cargarPropiedades();
        url = properties.getProperty(urlKey);
        user = properties.getProperty("db.user");
        password = properties.getProperty("db.password");
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    public static Database desarrollo() {
        return new Database("db.url");
    }

    private static Properties cargarPropiedades() {
        Path ruta = buscarArchivo();
        if (ruta == null) {
            throw new IllegalStateException(ARCHIVO_NO_ENCONTRADO);
        }

        Properties properties = new Properties();
        try (InputStream inputStream = Files.newInputStream(ruta)) {
            properties.load(inputStream);
        } catch (IOException exception) {
            throw new IllegalStateException("No se pudo leer db.properties.", exception);
        }
        return properties;
    }

    /**
     * Busca db.properties en este orden: la ruta de la propiedad del sistema
     * "db.config", la carpeta actual y la carpeta superior. La carpeta superior
     * permite ejecutar el backend desde su módulo (backend/) con el archivo en la raíz.
     */
    private static Path buscarArchivo() {
        String rutaConfiguracion = System.getProperty("db.config");
        Path[] candidatos = {
                rutaConfiguracion == null ? null : Path.of(rutaConfiguracion),
                Path.of("db.properties"),
                Path.of("..", "db.properties")
        };
        for (Path candidato : candidatos) {
            if (candidato != null && Files.isRegularFile(candidato)) {
                return candidato;
            }
        }
        return null;
    }
}
