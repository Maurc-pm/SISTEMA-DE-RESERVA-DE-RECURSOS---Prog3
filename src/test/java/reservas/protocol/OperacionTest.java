package reservas.protocol;

import org.junit.jupiter.api.Test;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import static org.junit.jupiter.api.Assertions.assertEquals;

class OperacionTest {
    @Test
    void cadaMetodoDeIServiceTieneUnaOperacionCorrespondienteYViceversa() {
        Set<String> metodos = Arrays.stream(IService.class.getDeclaredMethods())
                .map(Method::getName).map(OperacionTest::aConstante).collect(Collectors.toSet());
        Set<String> operaciones = Arrays.stream(Operacion.values()).map(Enum::name).collect(Collectors.toSet());
        assertEquals(metodos.size(), IService.class.getDeclaredMethods().length);
        assertEquals(operaciones.size(), Operacion.values().length);
        assertEquals(metodos, operaciones);
    }

    private static String aConstante(String nombre) {
        return nombre.replaceAll("([a-z0-9])([A-Z])", "$1_$2").toUpperCase();
    }
}
