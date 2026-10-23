package reservas.support;

import reservas.logic.*;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public final class WorkflowScenario {
    public static void main(String[] args) throws Exception {
        IsolatedJvm.assertDirectory(args[0]);
        boolean resetSchema = args[1].equals("reservas") || args[1].equals("funcionario");
        try (MySqlScenario environment = MySqlScenario.start(resetSchema);
             FixedTime time = new FixedTime()) {
            Service service = environment.service;
            switch (args[1]) {
                case "reservas" -> reservas(service);
                case "reservas-reload" -> reservasReload(service);
                case "funcionario" -> funcionario(service);
                case "funcionario-reload" -> funcionarioReload(service);
                case "funcionario-delete" -> funcionarioDelete(service);
                case "funcionario-deleted-reload" -> {
                    assertTrue(service.listarFuncionarios().isEmpty());
                    assertThrows(Exception.class, () -> service.login("F1", "nueva"));
                    assertTrue(service.login("admin", "admin").esAdministrador());
                }
                default -> throw new IllegalArgumentException(args[1]);
            }
        }
    }
    private static void reservas(Service service) throws Exception {
        IntegrationFixture f = new IntegrationFixture(service);
        Reserva primera = f.reservar("Primera", "08:00", "10:00");
        Reserva segunda = f.reservar("Segunda", "09:00", "11:00");
        assertEquals(List.of(f.r1), primera.getRecursos()); assertEquals(List.of(f.r2), segunda.getRecursos());
        assertThrows(Exception.class, () -> f.reservar("Sin cupo", "09:30", "10:30"));
        assertEquals(2, f.service.listarReservas().size());
        f.service.cancelarReserva(primera);
        Reserva tercera = f.reservar("Reutilizada", "09:00", "10:00");
        assertEquals(List.of(f.r1), tercera.getRecursos());
        assertEquals("RES-000003", tercera.getId());
        assertEquals(Reserva.CANCELADA, primera.getEstado());
    }
    private static void reservasReload(Service service) throws Exception {
        Funcionario ana = service.buscarFuncionarioPorId("F1");
        List<Reserva> reservas = service.listarReservasFuncionario(ana);
        assertEquals(3, reservas.size());
        assertEquals(Reserva.CANCELADA, reservas.get(0).getEstado());
        assertEquals(List.of(service.buscarRecursoPorId("R2")), reservas.get(1).getRecursos());
        assertEquals(List.of(service.buscarRecursoPorId("R1")), reservas.get(2).getRecursos());
        for (Reserva r : reservas) assertSame(ana, r.getFuncionario());
        assertTrue(reservas.get(1).estaActiva()); assertTrue(reservas.get(2).estaActiva());
    }
    private static void funcionario(Service service) throws Exception {
        Funcionario ana = new Funcionario("F1", "no-usar", "Ana", "8888"); service.crearFuncionario(ana);
        assertSame(ana, service.login("F1", "F1")); assertTrue(ana.esFuncionario());
        service.cambiarClave(ana, "F1", "nueva", "nueva");
        service.modificarFuncionario(new Funcionario("F1", "ignorar", "Ana María", "9999"));
        assertEquals(List.of(ana), service.buscarFuncionarios("F", "MARÍA"));
        assertSame(ana, service.buscarUsuarioPorId("F1")); assertSame(ana, service.login("F1", "nueva"));
    }
    private static void funcionarioReload(Service s) throws Exception {
        Funcionario ana = s.buscarFuncionarioPorId("F1");
        assertSame(ana, s.buscarUsuarioPorId("F1")); assertSame(ana, s.login("F1", "nueva"));
        assertEquals("Ana María", ana.getNombre()); assertEquals("9999", ana.getTelefono());
        assertThrows(Exception.class, () -> s.login("F1", "F1"));
    }
    private static void funcionarioDelete(Service service) throws Exception {
        service.eliminarFuncionario("F1");
        assertTrue(service.listarFuncionarios().isEmpty());
        assertThrows(Exception.class, () -> service.buscarUsuarioPorId("F1"));
        assertThrows(Exception.class, () -> service.login("F1", "nueva"));
    }
}
