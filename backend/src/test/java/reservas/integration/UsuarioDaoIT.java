package reservas.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reservas.data.CategoriaDao;
import reservas.data.JdbcCategoriaDao;
import reservas.data.JdbcRecursoDao;
import reservas.data.JdbcReservaDao;
import reservas.data.JdbcUsuarioDao;
import reservas.data.RecursoDao;
import reservas.data.ReservaDao;
import reservas.data.UsuarioDao;
import reservas.logic.Categoria;
import reservas.logic.Funcionario;
import reservas.logic.Recurso;
import reservas.logic.Reserva;
import reservas.logic.Usuario;
import reservas.support.TestDatabase;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioDaoIT {
    private UsuarioDao usuarios;

    @BeforeEach
    void prepararBase() throws Exception {
        TestDatabase.resetSchema();
        usuarios = new JdbcUsuarioDao(TestDatabase.database());
    }

    @Test
    void insertaBuscaListaActualizaYEliminaUsuarios() throws SQLException {
        Usuario admin = new Usuario("admin2", "clave-admin", Usuario.ADMINISTRADOR);
        Funcionario funcionario = new Funcionario("F1", "clave-f", "Ana", "8888");
        usuarios.insertar(admin);
        usuarios.insertar(funcionario);

        assertInstanceOf(Usuario.class, usuarios.buscarPorId("admin2"));
        assertFalse(usuarios.buscarPorId("admin2") instanceof Funcionario);
        Funcionario encontrado = assertInstanceOf(Funcionario.class, usuarios.buscarPorId("F1"));
        assertEquals("Ana", encontrado.getNombre());
        assertEquals(List.of("F1"), usuarios.listarFuncionarios().stream().map(Funcionario::getId).toList());

        encontrado.setClave("nueva");
        encontrado.setNombre("Ana María");
        encontrado.setTelefono("9999");
        usuarios.actualizar(encontrado);
        assertEquals("nueva", usuarios.buscarPorId("F1").getClave());
        assertEquals("Ana María", ((Funcionario) usuarios.buscarPorId("F1")).getNombre());

        usuarios.eliminar("admin2");
        assertNull(usuarios.buscarPorId("admin2"));
    }

    @Test
    void listarDevuelveAdminYFuncionarioOrdenadosPorId() throws SQLException {
        usuarios.insertar(new Funcionario("F1", "clave-f", "Ana", "8888"));

        List<Usuario> listados = usuarios.listar();

        assertEquals(List.of("admin", "F1"), listados.stream().map(Usuario::getId).toList());
        assertFalse(listados.get(0) instanceof Funcionario);
        assertInstanceOf(Funcionario.class, listados.get(1));
    }

    @Test
    void reconoceReservasYLaLlaveForaneaImpideEliminarFuncionarioEnUso() throws SQLException {
        Funcionario funcionario = new Funcionario("F1", "clave", "Ana", "8888");
        usuarios.insertar(funcionario);
        assertFalse(usuarios.tieneReservas("F1"));

        CategoriaDao categorias = new JdbcCategoriaDao(TestDatabase.database());
        RecursoDao recursos = new JdbcRecursoDao(TestDatabase.database());
        ReservaDao reservas = new JdbcReservaDao(TestDatabase.database());
        Categoria categoria = new Categoria("C1", "Salas");
        Recurso recurso = new Recurso("R1", categoria, "Sala 1");
        categorias.insertar(categoria);
        recursos.insertar(recurso);
        reservas.insertar(new Reserva("RES1", funcionario, "Clase", LocalDate.of(2026, 1, 2),
                LocalTime.of(8, 0), LocalTime.of(9, 0), List.of(recurso), Reserva.ACTIVA));

        assertTrue(usuarios.tieneReservas("F1"));
        assertThrows(SQLException.class, () -> usuarios.eliminar("F1"));
        assertNotNull(usuarios.buscarPorId("F1"));
    }
}
