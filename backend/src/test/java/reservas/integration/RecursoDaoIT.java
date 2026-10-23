package reservas.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reservas.data.*;
import reservas.logic.*;
import reservas.support.TestDatabase;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RecursoDaoIT {
    private Categoria categoria;
    private RecursoDao recursos;

    @BeforeEach
    void prepararBase() throws Exception {
        TestDatabase.resetSchema();
        categoria = new Categoria("C1", "Salas");
        new JdbcCategoriaDao(TestDatabase.database()).insertar(categoria);
        recursos = new JdbcRecursoDao(TestDatabase.database());
    }

    @Test
    void insertaBuscaListaFiltraActualizaYEliminaRecursos() throws SQLException {
        Recurso recurso = new Recurso("R1", categoria, "Sala 1");
        recursos.insertar(recurso);

        Recurso encontrado = recursos.buscarPorId("R1");
        assertEquals("Sala 1", encontrado.getDescripcion());
        assertEquals("C1", encontrado.getCategoria().getId());
        assertEquals("Salas", encontrado.getCategoria().getDescripcion());
        assertEquals(List.of("R1"), recursos.listar().stream().map(Recurso::getId).toList());
        assertEquals(List.of("R1"), recursos.listarPorCategoria("C1").stream().map(Recurso::getId).toList());

        Categoria otra = new Categoria("C2", "Equipo");
        new JdbcCategoriaDao(TestDatabase.database()).insertar(otra);
        recurso.setCategoria(otra);
        recurso.setDescripcion("Proyector");
        recursos.actualizar(recurso);
        assertEquals("C2", recursos.buscarPorId("R1").getCategoria().getId());
        assertEquals("Proyector", recursos.buscarPorId("R1").getDescripcion());

        assertFalse(recursos.tieneReservas("R1"));
        recursos.eliminar("R1");
        assertNull(recursos.buscarPorId("R1"));
    }

    @Test
    void reconoceReservasYLaLlaveForaneaImpideEliminarRecursoEnUso() throws SQLException {
        Recurso recurso = new Recurso("R1", categoria, "Sala 1");
        recursos.insertar(recurso);
        UsuarioDao usuarios = new JdbcUsuarioDao(TestDatabase.database());
        Funcionario funcionario = new Funcionario("F1", "clave", "Ana", "8888");
        usuarios.insertar(funcionario);
        new JdbcReservaDao(TestDatabase.database()).insertar(new Reserva("RES1", funcionario, "Clase",
                LocalDate.of(2026, 1, 2), LocalTime.of(8, 0), LocalTime.of(9, 0),
                List.of(recurso), Reserva.ACTIVA));

        assertTrue(recursos.tieneReservas("R1"));
        assertThrows(SQLException.class, () -> recursos.eliminar("R1"));
        assertNotNull(recursos.buscarPorId("R1"));
    }
}
