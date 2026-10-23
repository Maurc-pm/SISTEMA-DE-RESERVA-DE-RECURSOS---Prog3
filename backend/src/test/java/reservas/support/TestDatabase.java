package reservas.support;

import reservas.data.Database;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public final class TestDatabase {
    private TestDatabase() {
    }

    public static Database database() {
        return new Database("db.test.url");
    }

    public static void resetSchema() throws SQLException, IOException {
        try (Connection connection = database().getConnection()) {
            if (!"reservas_test".equals(connection.getCatalog())) {
                throw new IllegalStateException("La base de datos de pruebas debe ser reservas_test.");
            }

            ejecutar(connection, "DROP TABLE IF EXISTS reserva_recurso");
            ejecutar(connection, "DROP TABLE IF EXISTS reserva");
            ejecutar(connection, "DROP TABLE IF EXISTS recurso");
            ejecutar(connection, "DROP TABLE IF EXISTS categoria");
            ejecutar(connection, "DROP TABLE IF EXISTS usuario");

            String schemaPath = System.getProperty("reservas.schema");
            Path schema = schemaPath == null
                    ? Path.of("database", "reservas.sql")
                    : Path.of(schemaPath);
            String script = Files.readString(schema);
            script = script.replaceAll("(?m)^\\s*--.*(?:\\R|$)", "");
            for (String sentencia : script.split(";")) {
                String sql = sentencia.trim();
                if (!sql.isEmpty()
                        && !sql.startsWith("CREATE DATABASE")
                        && !sql.startsWith("USE ")) {
                    ejecutar(connection, sql);
                }
            }
        }
    }

    private static void ejecutar(Connection connection, String sql) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.execute();
        }
    }
}
