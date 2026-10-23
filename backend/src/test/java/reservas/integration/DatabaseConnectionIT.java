package reservas.integration;

import org.junit.jupiter.api.Test;
import reservas.data.Database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DatabaseConnectionIT {

    @Test
    void conectaSoloALaBaseDePruebasYEjecutaSelectUno() throws Exception {
        try (Connection connection = new Database("db.test.url").getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT 1")) {

            assertEquals("reservas_test", connection.getCatalog());
            resultSet.next();
            assertEquals(1, resultSet.getInt(1));
        }
    }
}
