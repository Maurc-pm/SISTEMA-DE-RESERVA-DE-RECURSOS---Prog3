package reservas.logic;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import reservas.support.ServiceTestSupport;
import reservas.support.FixedTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class RecursoServiceTest extends ServiceTestSupport {
    @Test void crearRecursoNormalizaYUsaCategoriaCanonica() throws Exception {
        Categoria c = categoria("Sala");
        Recurso r = new Recurso(" R1 ", new Categoria(c.getId(), "copia"), " Aula 1 ");
        service.crearRecurso(r);
        assertSame(r, service.buscarRecursoPorId(" R1 "));
        assertSame(c, r.getCategoria());
        assertEquals("R1", r.getId()); assertEquals("Aula 1", r.getDescripcion());
    }
    @Test void crearRecursoConIdDuplicadoFalla() throws Exception {
        Categoria c = categoria("Sala"); Recurso r = recurso("R1", c);
        assertThrows(Exception.class, () -> recurso(" R1 ", c));
        assertEquals(List.of(r), service.listarRecursos());
    }
    @ParameterizedTest @NullAndEmptySource @ValueSource(strings = {" "})
    void idObligatorio(String id) throws Exception {
        Categoria c = categoria("Sala");
        assertEquals("El ID del recurso es requerido", assertThrows(Exception.class,
                () -> recurso(id, c)).getMessage());
        assertTrue(service.listarRecursos().isEmpty());
    }
    @ParameterizedTest @NullAndEmptySource @ValueSource(strings = {" "})
    void descripcionObligatoria(String descripcion) throws Exception {
        Categoria c = categoria("Sala");
        assertEquals("La descripción es requerida", assertThrows(Exception.class,
                () -> service.crearRecurso(new Recurso("R1", c, descripcion))).getMessage());
    }
    @Test void categoriaEsObligatoria() {
        assertEquals("La categoría es requerida", assertThrows(Exception.class,
                () -> recurso("R1", null)).getMessage());
    }
    @Test void categoriaDebeExistirRealmente() {
        assertEquals("Categoría no encontrada", assertThrows(Exception.class,
                () -> recurso("R1", new Categoria("falsa", "Sala"))).getMessage());
        assertTrue(service.listarRecursos().isEmpty());
    }
    @Test void recursoNuloFalla() {
        assertEquals("Recurso requerido", assertThrows(Exception.class,
                () -> service.crearRecurso(null)).getMessage());
    }
    @Test void filtrarCategoriaUsaIdYNoMezclaRecursos() throws Exception {
        Categoria a = categoria("Sala"), b = categoria("Equipo");
        Recurso r = recurso("R1", a); recurso("R2", b);
        assertEquals(List.of(r), service.buscarRecursosPorCategoria(new Categoria(a.getId(), "copia")));
        assertTrue(service.buscarRecursosPorCategoria(null).isEmpty());
    }
    @Test void modificarRecursoActualizaCategoriaYDescripcion() throws Exception {
        Categoria a = categoria("Sala"), b = categoria("Equipo");
        Recurso r = recurso("R1", a);
        service.modificarRecurso(new Recurso("R1", b, " Proyector "));
        assertSame(r, service.buscarRecursoPorId("R1"));
        assertSame(b, r.getCategoria()); assertEquals("Proyector", r.getDescripcion());
        assertTrue(service.buscarRecursosPorCategoria(a).isEmpty());
        assertEquals(List.of(r), service.buscarRecursosPorCategoria(b));
    }
    @Test void modificarConCategoriaInexistenteNoAlteraRecurso() throws Exception {
        Categoria c = categoria("Sala"); Recurso r = recurso("R1", c);
        assertThrows(Exception.class, () -> service.modificarRecurso(
                new Recurso("R1", new Categoria("falsa", "X"), "Cambiado")));
        assertSame(c, r.getCategoria()); assertEquals("Recurso R1", r.getDescripcion());
    }
    @Test void eliminarRecursoExistente() throws Exception {
        Categoria c = categoria("Sala"); recurso("R1", c);
        service.eliminarRecurso("R1");
        assertTrue(service.listarRecursos().isEmpty());
        assertDoesNotThrow(() -> service.eliminarCategoria(c.getId()));
    }
    @Test void eliminarYBuscarRecursoInexistenteFallan() {
        assertEquals("Recurso no encontrado", assertThrows(Exception.class,
                () -> service.eliminarRecurso("ausente")).getMessage());
        assertThrows(Exception.class, () -> service.buscarRecursoPorId("ausente"));
    }
    @Test void eliminacionActualPermiteRecursoConReservaActiva() throws Exception {
        Funcionario f = funcionario("F1", "Ana"); Categoria c = categoria("Sala");
        Recurso r = recurso("R1", c);
        Reserva reserva = reserva(f, FixedTime.TODAY.plusDays(1), "08:00", "09:00");
        service.crearReserva(reserva, List.of(c)); service.eliminarRecurso("R1");
        assertTrue(service.listarRecursos().isEmpty());
        assertEquals(List.of(r), reserva.getRecursos()); // Limitación actual: referencia histórica en memoria.
    }
}
