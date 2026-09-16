package reservas.logic;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import reservas.support.ServiceTestSupport;
import reservas.support.FixedTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class FuncionarioServiceTest extends ServiceTestSupport {
    @Test void crearFuncionarioNormalizaDatosAsignaClaveIdYRol() throws Exception {
        Funcionario f = new Funcionario(" F1 ", "otra", " Ana Pérez ", " 8888 ");
        f.setRol(Usuario.ADMINISTRADOR);
        service.crearFuncionario(f);
        assertSame(f, service.buscarFuncionarioPorId(" F1 "));
        assertEquals("F1", f.getId());
        assertEquals("F1", f.getClave());
        assertEquals("Ana Pérez", f.getNombre());
        assertEquals("8888", f.getTelefono());
        assertTrue(f.esFuncionario());
        assertFalse(f.esAdministrador());
        assertSame(f, service.login("F1", "F1"));
    }
    @Test void buscarPorNombreParcialIgnoraMayusculas() throws Exception {
        Funcionario ana = funcionario("ABC1", "Ana Pérez");
        funcionario("XYZ2", "Luis Mora");
        assertEquals(List.of(ana), service.buscarFuncionarios(null, " PÉR "));
    }
    @Test void buscarPorIdParcialYCombinarFiltros() throws Exception {
        Funcionario ana = funcionario("ABC1", "Ana Pérez");
        funcionario("ABC2", "Luis Mora");
        assertEquals(2, service.buscarFuncionarios(" ab ", null).size());
        assertEquals(List.of(ana), service.buscarFuncionarios("bc", "ana"));
        assertTrue(service.buscarFuncionarios("XYZ", "ana").isEmpty());
    }
    @Test void listarFuncionariosExcluyeAdministradores() throws Exception {
        Funcionario ana = funcionario("F1", "Ana");
        data.getUsuarios().add(new Usuario("admin", "admin", Usuario.ADMINISTRADOR));
        assertEquals(List.of(ana), service.listarFuncionarios());
        assertThrows(Exception.class, () -> service.buscarFuncionarioPorId("admin"));
    }
    @Test void modificarConservaClaveYRol() throws Exception {
        Funcionario original = funcionario("F1", "Ana");
        service.cambiarClave(original, "F1", "privada", "privada");
        service.modificarFuncionario(new Funcionario("F1", "no usar", " Ana María ", " 9999 "));
        assertEquals("Ana María", service.buscarFuncionarioPorId("F1").getNombre());
        assertEquals("9999", original.getTelefono());
        assertSame(original, service.login("F1", "privada"));
        assertTrue(original.esFuncionario());
        assertEquals(1, service.listarFuncionarios().size());
    }
    @Test void crearFuncionarioConIdDuplicadoFallaSinReemplazar() throws Exception {
        Funcionario original = funcionario("F1", "Ana");
        assertThrows(Exception.class, () -> funcionario(" F1 ", "Otra"));
        assertEquals(List.of(original), service.listarFuncionarios());
    }
    @Test void idDeAdministradorTambienImpideCrearFuncionario() {
        data.getUsuarios().add(new Usuario("admin", "x", Usuario.ADMINISTRADOR));
        assertThrows(Exception.class, () -> funcionario("admin", "Ana"));
        assertTrue(service.listarFuncionarios().isEmpty());
    }
    @ParameterizedTest @NullAndEmptySource @ValueSource(strings = {" "})
    void nombreObligatorio(String nombre) {
        assertEquals("El nombre es requerido", assertThrows(Exception.class,
                () -> funcionario("F1", nombre)).getMessage());
        assertTrue(data.getUsuarios().isEmpty());
    }
    @ParameterizedTest @NullAndEmptySource @ValueSource(strings = {" "})
    void telefonoObligatorio(String telefono) {
        assertEquals("El teléfono es requerido", assertThrows(Exception.class,
                () -> service.crearFuncionario(new Funcionario("F1", "", "Ana", telefono))).getMessage());
    }
    @ParameterizedTest @NullAndEmptySource @ValueSource(strings = {" "})
    void idObligatorio(String id) {
        assertEquals("El ID es requerido", assertThrows(Exception.class,
                () -> funcionario(id, "Ana")).getMessage());
    }
    @Test void funcionarioNuloEsRechazado() {
        assertEquals("Funcionario requerido", assertThrows(Exception.class,
                () -> service.crearFuncionario(null)).getMessage());
    }
    @Test void eliminarInexistenteNoAfectaExistentes() throws Exception {
        Funcionario f = funcionario("F1", "Ana");
        assertEquals("Funcionario no encontrado", assertThrows(Exception.class,
                () -> service.eliminarFuncionario("otro")).getMessage());
        assertEquals(List.of(f), service.listarFuncionarios());
    }
    @Test void modificarInexistenteFalla() {
        assertThrows(Exception.class, () -> service.modificarFuncionario(
                new Funcionario("otro", "", "Ana", "8888")));
        assertTrue(service.listarFuncionarios().isEmpty());
    }
    @Test void eliminarFuncionarioRetiraTambienSuUsuario() throws Exception {
        funcionario("F1", "Ana");
        service.eliminarFuncionario("F1");
        assertTrue(service.listarFuncionarios().isEmpty());
        assertThrows(Exception.class, () -> service.buscarUsuarioPorId("F1"));
        assertThrows(Exception.class, () -> service.login("F1", "F1"));
    }
    @Test void eliminacionActualNoRestringeReservasAsociadas() throws Exception {
        Funcionario f = funcionario("F1", "Ana");
        Categoria c = categoria("Sala"); recurso("R1", c);
        Reserva r = reserva(f, FixedTime.TODAY.plusDays(1), "08:00", "09:00");
        service.crearReserva(r, List.of(c));
        service.eliminarFuncionario("F1");
        assertTrue(service.listarFuncionarios().isEmpty());
        assertEquals(List.of(r), service.listarReservas()); // Caracteriza la regla actual, no integridad XML.
    }
}
