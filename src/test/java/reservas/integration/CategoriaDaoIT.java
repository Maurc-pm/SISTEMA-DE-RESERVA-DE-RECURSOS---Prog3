package reservas.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reservas.data.CategoriaDao;
import reservas.data.JdbcCategoriaDao;
import reservas.data.JdbcRecursoDao;
import reservas.data.RecursoDao;
import reservas.logic.Categoria;
import reservas.logic.Recurso;
import reservas.support.TestDatabase;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CategoriaDaoIT {
    private CategoriaDao categorias;

    @BeforeEach
    void prepararBase() throws Exception {
        TestDatabase.resetSchema();
        categorias = new JdbcCategoriaDao(TestDatabase.database());
    }

    @Test
    void insertaBuscaListaActualizaYEliminaCategorias() throws SQLException {
        Categoria categoria = new Categoria("C1", "Salas");
        categorias.insertar(categoria);
        assertEquals("Salas", categorias.buscarPorId("C1").getDescripcion());
        assertEquals(List.of("C1"), categorias.listar().stream().map(Categoria::getId).toList());

        categoria.setDescripcion("Salas de reuniones");
        categorias.actualizar(categoria);
        assertEquals("Salas de reuniones", categorias.buscarPorId("C1").getDescripcion());

        assertFalse(categorias.tieneRecursos("C1"));
        categorias.eliminar("C1");
        assertNull(categorias.buscarPorId("C1"));
    }

    @Test
    void reconoceRecursosYLaLlaveForaneaImpideEliminarCategoriaEnUso() throws SQLException {
        Categoria categoria = new Categoria("C1", "Salas");
        categorias.insertar(categoria);
        RecursoDao recursos = new JdbcRecursoDao(TestDatabase.database());
        recursos.insertar(new Recurso("R1", categoria, "Sala 1"));

        assertTrue(categorias.tieneRecursos("C1"));
        assertThrows(SQLException.class, () -> categorias.eliminar("C1"));
        assertNotNull(categorias.buscarPorId("C1"));
    }
}
