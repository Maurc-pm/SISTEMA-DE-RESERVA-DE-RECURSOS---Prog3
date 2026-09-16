package reservas.logic;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import reservas.support.ServiceTestSupport;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class CategoriaServiceTest extends ServiceTestSupport {
    @Test void crearCategoriaGeneraIdsDiferentesYNormalizaDescripcion() throws Exception {
        Categoria c1 = categoria(" Sala ");
        Categoria c2 = categoria("Equipo");
        assertEquals("CAT-000001", c1.getId());
        assertEquals("CAT-000002", c2.getId());
        assertNotEquals(c1.getId(), c2.getId());
        assertEquals("Sala", c1.getDescripcion());
        assertSame(c1, service.buscarCategoriaPorId(" CAT-000001 "));
    }
    @Test void idSeGeneraDesdeMayorExistenteIgnorandoIdsAjenosAlFormato() throws Exception {
        data.getCategorias().add(new Categoria("CAT-000042", "Existente"));
        data.getCategorias().add(new Categoria("CAT-mal", "Legado"));
        data.getCategorias().add(new Categoria("otra", "Otra"));
        assertEquals("CAT-000043", categoria("Nueva").getId());
    }
    @ParameterizedTest @NullAndEmptySource @ValueSource(strings = {" "})
    void descripcionObligatoria(String descripcion) {
        assertEquals("La descripción es requerida", assertThrows(Exception.class,
                () -> categoria(descripcion)).getMessage());
        assertTrue(service.listarCategorias().isEmpty());
    }
    @Test void categoriaNulaFalla() {
        assertEquals("Categoría requerida", assertThrows(Exception.class,
                () -> service.crearCategoria(null)).getMessage());
    }
    @Test void busquedaParcialIgnoraMayusculasYEspacios() throws Exception {
        Categoria c = categoria("Salas grandes"); categoria("Equipo");
        assertEquals(List.of(c), service.buscarCategorias(" SAL "));
        assertEquals(2, service.buscarCategorias(null).size());
        assertTrue(service.buscarCategorias("inexistente").isEmpty());
    }
    @Test void modificarDescripcionConservaIdentidadYRelacionDelRecurso() throws Exception {
        Categoria c = categoria("Sala"); Recurso r = recurso("R1", c);
        service.modificarCategoria(new Categoria(c.getId(), " Aula "));
        assertSame(c, service.buscarCategoriaPorId(c.getId()));
        assertEquals("Aula", r.getCategoria().getDescripcion());
        assertEquals(1, service.listarCategorias().size());
    }
    @Test void eliminarCategoriaSinRecursosEsValido() throws Exception {
        Categoria c = categoria("Sala"); service.eliminarCategoria(c.getId());
        assertTrue(service.listarCategorias().isEmpty());
        assertThrows(Exception.class, () -> service.buscarCategoriaPorId(c.getId()));
    }
    @Test void eliminarCategoriaConRecursosFallaYConservaDatos() throws Exception {
        Categoria c = categoria("Sala"); Recurso r = recurso("R1", c);
        assertEquals("No se puede eliminar la categoría porque tiene recursos asociados",
                assertThrows(Exception.class, () -> service.eliminarCategoria(c.getId())).getMessage());
        assertEquals(List.of(c), service.listarCategorias());
        assertEquals(List.of(r), service.listarRecursos());
    }
    @Test void eliminarYModificarInexistentesFallan() {
        assertThrows(Exception.class, () -> service.eliminarCategoria("ausente"));
        assertThrows(Exception.class, () -> service.modificarCategoria(new Categoria("ausente", "Sala")));
    }
    @ParameterizedTest @NullAndEmptySource @ValueSource(strings = {" "})
    void buscarCategoriaExigeId(String id) {
        assertEquals("El ID de categoría es requerido", assertThrows(Exception.class,
                () -> service.buscarCategoriaPorId(id)).getMessage());
    }
}
