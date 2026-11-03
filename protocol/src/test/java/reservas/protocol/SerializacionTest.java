package reservas.protocol;

import org.junit.jupiter.api.Test;
import reservas.logic.*;
import java.io.*;
import java.time.*;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class SerializacionTest {
    private Reserva reservaCompleta() {
        Categoria cat1 = new Categoria("CAT1", "Sala");
        Categoria cat2 = new Categoria("CAT2", "Equipo");
        return new Reserva("RES1", new Funcionario("F1", "clave", "Ana", "123"), "Actividad",
                LocalDate.of(2026, 5, 10), LocalTime.of(9, 0), LocalTime.of(10, 0),
                List.of(new Recurso("R1", cat1, "Aula"), new Recurso("R2", cat2, "Proyector")), Reserva.ACTIVA);
    }

    private Object roundTrip(Object value) throws Exception {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ObjectOutputStream out = new ObjectOutputStream(bytes)) { out.writeObject(value); }
        try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()))) { return in.readObject(); }
    }

    private void assertReserva(Reserva actual) {
        assertEquals("RES1", actual.getId());
        assertEquals("F1", actual.getFuncionario().getId());
        assertEquals("Ana", actual.getFuncionario().getNombre());
        assertEquals("Actividad", actual.getActividad());
        assertEquals(LocalDate.of(2026, 5, 10), actual.getFecha());
        assertEquals(LocalTime.of(9, 0), actual.getHoraInicio());
        assertEquals(LocalTime.of(10, 0), actual.getHoraFin());
        assertEquals(2, actual.getRecursos().size());
        assertEquals("CAT1", actual.getRecursos().get(0).getCategoria().getId());
        assertEquals("CAT2", actual.getRecursos().get(1).getCategoria().getId());
    }

    @Test void requestConservaReservaCompleta() throws Exception {
        Request actual = (Request) roundTrip(new Request(Operacion.CREAR_RESERVA, reservaCompleta()));
        assertEquals(Operacion.CREAR_RESERVA, actual.getOperacion());
        assertReserva((Reserva) actual.getArgumentos()[0]);
    }

    @Test void responseConservaReservaCompleta() throws Exception {
        Response actual = (Response) roundTrip(Response.ok(reservaCompleta()));
        assertTrue(actual.isOk());
        assertReserva((Reserva) actual.getValor());
    }

    @Test void notificationConservaReservaCompleta() throws Exception {
        Notification actual = (Notification) roundTrip(new Notification(reservaCompleta()));
        assertReserva(actual.getReserva());
    }
}
