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
        String rutaConfiguracion = System.getProperty("db.config");
        Path ruta = rutaConfiguracion == null
                ? Path.of("db.properties")
                : Path.of(rutaConfiguracion);

        if (!Files.exists(ruta)) {
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
}
