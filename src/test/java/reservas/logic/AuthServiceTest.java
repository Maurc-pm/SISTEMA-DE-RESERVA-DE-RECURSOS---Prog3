package reservas.logic;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import reservas.support.ServiceTestSupport;
import reservas.data.XmlStorage;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest extends ServiceTestSupport {
    @Test void loginConClaveCorrectaRetornaUsuario() throws Exception {
        Funcionario f = funcionario("F1", "Ana");
        assertSame(f, service.login(" F1 ", "F1"));
    }
    @Test void loginAdministradorConservaRol() throws Exception {
        Usuario admin = new Usuario("admin", "secreto", Usuario.ADMINISTRADOR);
        data.getUsuarios().add(admin);
        assertTrue(service.login("admin", "secreto").esAdministrador());
        assertSame(admin, service.buscarUsuarioPorId(" admin "));
    }
    @Test void loginConClaveIncorrectaLanzaExcepcion() throws Exception {
        funcionario("F1", "Ana");
        assertEquals("ID o clave incorrectos", assertThrows(Exception.class,
                () -> service.login("F1", "otra")).getMessage());
    }
    @Test void loginConUsuarioInexistenteLanzaExcepcion() {
        assertEquals("ID o clave incorrectos", assertThrows(Exception.class,
                () -> service.login("nadie", "clave")).getMessage());
    }
    @ParameterizedTest @NullAndEmptySource @ValueSource(strings = {"   "})
    void loginRechazaIdVacio(String id) {
        assertEquals("El ID es requerido", assertThrows(Exception.class,
                () -> service.login(id, "clave")).getMessage());
    }
    @ParameterizedTest @NullAndEmptySource @ValueSource(strings = {"   "})
    void loginRechazaClaveVacia(String clave) {
        assertEquals("La clave es requerida", assertThrows(Exception.class,
                () -> service.login("F1", clave)).getMessage());
    }
    @Test void cambiarClaveInvalidaAnteriorYPermiteNuevoLogin() throws Exception {
        Funcionario f = funcionario("F1", "Ana");
        storage.clearInvocations();
        service.cambiarClave(f, "F1", "nueva", "nueva");
        assertSame(f, service.login("F1", "nueva"));
        assertThrows(Exception.class, () -> service.login("F1", "F1"));
        storage.verify(() -> XmlStorage.guardar(data), times(1));
    }
    @Test void cambiarClaveActualIncorrectaNoModificaNiPersiste() throws Exception {
        Funcionario f = funcionario("F1", "Ana");
        storage.clearInvocations();
        assertEquals("La clave actual es incorrecta", assertThrows(Exception.class,
                () -> service.cambiarClave(f, "mal", "nueva", "nueva")).getMessage());
        assertSame(f, service.login("F1", "F1"));
        storage.verifyNoInteractions();
    }
    @Test void cambiarClaveConConfirmacionDiferenteNoModifica() throws Exception {
        Funcionario f = funcionario("F1", "Ana");
        assertEquals("La clave nueva y la confirmación no coinciden", assertThrows(Exception.class,
                () -> service.cambiarClave(f, "F1", "nueva", "otra")).getMessage());
        assertSame(f, service.login("F1", "F1"));
    }
    @Test void cambiarClaveSinUsuarioFalla() {
        assertEquals("Usuario requerido", assertThrows(Exception.class,
                () -> service.cambiarClave(null, "x", "y", "y")).getMessage());
    }
    @ParameterizedTest @NullAndEmptySource
    void cambiarClaveActualVaciaFalla(String clave) throws Exception {
        Funcionario f = funcionario("F1", "Ana");
        assertEquals("La clave actual es requerida", assertThrows(Exception.class,
                () -> service.cambiarClave(f, clave, "nueva", "nueva")).getMessage());
    }
    @ParameterizedTest @NullAndEmptySource @ValueSource(strings = {" "})
    void cambiarClaveNuevaVaciaFalla(String clave) throws Exception {
        Funcionario f = funcionario("F1", "Ana");
        assertEquals("La nueva clave es requerida", assertThrows(Exception.class,
                () -> service.cambiarClave(f, "F1", clave, clave)).getMessage());
    }
    @Test void buscarUsuarioInexistenteFalla() {
        assertEquals("Usuario no encontrado", assertThrows(Exception.class,
                () -> service.buscarUsuarioPorId("nadie")).getMessage());
    }
}
