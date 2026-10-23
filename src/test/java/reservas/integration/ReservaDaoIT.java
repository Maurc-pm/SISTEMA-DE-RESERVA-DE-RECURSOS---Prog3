package reservas.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reservas.data.*;
import reservas.logic.*;
import reservas.support.TestDatabase;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReservaDaoIT {
    private Funcionario funcionario;
    private Categoria categoria;
    private Recurso recurso1;
    private Recurso recurso2;
    private ReservaDao reservas;

    @BeforeEach
    void prepararBase() throws Exception {
        TestDatabase.resetSchema();
        UsuarioDao usuarios = new JdbcUsuarioDao(TestDatabase.database());
        CategoriaDao categorias = new JdbcCategoriaDao(TestDatabase.database());
        RecursoDao recursos = new JdbcRecursoDao(TestDatabase.database());
        funcionario = new Funcionario("F1", "clave", "Ana", "8888");
        categoria = new Categoria("C1", "Salas");
        recurso1 = new Recurso("R1", categoria, "Sala 1");
        recurso2 = new Recurso("R2", categoria, "Sala 2");
        usuarios.insertar(funcionario);
        categorias.insertar(categoria);
        recursos.insertar(recurso1);
        recursos.insertar(recurso2);
        reservas = new JdbcReservaDao(TestDatabase.database());
    }

    @Test
    void insertaBuscaYListaReservasConFuncionarioRecursosYCategorias() throws SQLException {
        Reserva reserva = nueva("RES1", List.of(recurso1, recurso2));
        reservas.insertar(reserva);

        Reserva encontrada = reservas.buscarPorId("RES1");
        assertEquals("Ana", encontrada.getFuncionario().getNombre());
        assertEquals(List.of("R1", "R2"), encontrada.getRecursos().stream().map(Recurso::getId).toList());
        assertEquals("Salas", encontrada.getRecursos().get(0).getCategoria().getDescripcion());
        assertEquals(List.of("RES1"), reservas.listar().stream().map(Reserva::getId).toList());
        assertEquals(List.of("RES1"), reservas.listarPorFuncionario("F1").stream().map(Reserva::getId).toList());

        reservas.actualizarEstado("RES1", Reserva.CANCELADA);
        assertEquals(Reserva.CANCELADA, reservas.buscarPorId("RES1").getEstado());
    }

    @Test
    void revierteLaReservaSiFallaUnRecursoInexistente() throws SQLException {
        Recurso inexistente = new Recurso("NO_EXISTE", categoria, "Inexistente");
        Reserva reserva = nueva("RES1", List.of(recurso1, inexistente));

        assertThrows(SQLException.class, () -> reservas.insertar(reserva));
        assertNull(reservas.buscarPorId("RES1"));
    }

    @Test
    void revierteLaReservaSiUnRecursoEsNull() throws SQLException {
        Reserva reserva = nueva("RES1", new ArrayList<>(Arrays.asList(recurso1, null)));

        assertThrows(Exception.class, () -> reservas.insertar(reserva));
        assertNull(reservas.buscarPorId("RES1"));
    }

    private Reserva nueva(String id, List<Recurso> recursos) {
        return new Reserva(id, funcionario, "Clase", LocalDate.of(2026, 1, 2),
                LocalTime.of(8, 0), LocalTime.of(9, 0), recursos, Reserva.ACTIVA);
    }
}
