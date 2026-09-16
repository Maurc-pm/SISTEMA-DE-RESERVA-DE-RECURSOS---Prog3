package reservas.support;

import reservas.data.*;
import reservas.logic.*;
import java.nio.file.*;
import java.time.*;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public final class XmlScenario {
    public static void main(String[] args) throws Exception {
        IsolatedJvm.assertDirectory(args[0]);
        switch (args[1]) {
            case "roundtrip" -> roundtrip();
            case "missing" -> assertNull(XmlStorage.cargar());
            case "invalid" -> {
                Files.createDirectories(Path.of("data"));
                Files.writeString(Path.of("data/reservas.xml"), "<sistemaReservas><roto>");
                assertThrows(Exception.class, XmlStorage::cargar);
            }
            case "bootstrap" -> {
                Service service = Service.instance();
                assertTrue(service.login("admin", "admin").esAdministrador());
                assertTrue(Files.size(Path.of("data/reservas.xml")) > 0);
                assertEquals(1, XmlStorage.cargar().getUsuarios().size());
            }
            case "orphans" -> orphanBehavior();
            default -> throw new IllegalArgumentException(args[1]);
        }
    }
    private static void roundtrip() throws Exception {
        Data original = new Data();
        Usuario admin = new Usuario("admin", "clave-admin", Usuario.ADMINISTRADOR);
        Funcionario ana = new Funcionario("F1", "secreta", "Ana Pérez & Núñez", "8888-1111");
        Categoria sala = new Categoria("CAT-000009", "Sala <grande> & equipo");
        Categoria equipo = new Categoria("CAT-000010", "Computación");
        Recurso r1 = new Recurso("R1", sala, "Sala Norte"), r2 = new Recurso("R2", equipo, "Portátil");
        LocalDate fecha = LocalDate.of(2026,9,17);
        Reserva activa = new Reserva("RES-000020", ana, "Clase <A> & B", fecha,
                LocalTime.of(8,30), LocalTime.of(10,15), List.of(r1,r2), Reserva.ACTIVA);
        Reserva cancelada = new Reserva("RES-000021", ana, "Cancelada", fecha.plusDays(1),
                LocalTime.of(14,0), LocalTime.of(15,0), List.of(r1), Reserva.CANCELADA);
        original.getUsuarios().addAll(List.of(admin,ana)); original.getCategorias().addAll(List.of(sala,equipo));
        original.getRecursos().addAll(List.of(r1,r2)); original.getReservas().addAll(List.of(activa,cancelada));
        XmlStorage.guardar(original);
        assertTrue(Files.size(Path.of("data/reservas.xml")) > 0);
        Data loaded = XmlStorage.cargar();
        assertNotSame(original, loaded);
        assertEquals(2, loaded.getUsuarios().size()); assertEquals(2, loaded.getCategorias().size());
        assertEquals(2, loaded.getRecursos().size()); assertEquals(2, loaded.getReservas().size());
        Usuario loadedAdmin = loaded.getUsuarios().stream().filter(u -> u.getId().equals("admin")).findFirst().orElseThrow();
        Funcionario loadedAna = (Funcionario) loaded.getUsuarios().stream().filter(u -> u.getId().equals("F1")).findFirst().orElseThrow();
        assertEquals("clave-admin", loadedAdmin.getClave()); assertTrue(loadedAdmin.esAdministrador());
        assertEquals("secreta", loadedAna.getClave()); assertTrue(loadedAna.esFuncionario());
        assertEquals(ana.getNombre(), loadedAna.getNombre()); assertEquals(ana.getTelefono(), loadedAna.getTelefono());
        for (int i=0;i<2;i++) {
            Categoria c = loaded.getCategorias().get(i); Recurso r = loaded.getRecursos().get(i);
            assertEquals(original.getCategorias().get(i).getId(), c.getId());
            assertEquals(original.getCategorias().get(i).getDescripcion(), c.getDescripcion());
            assertEquals(original.getRecursos().get(i).getId(), r.getId());
            assertEquals(original.getRecursos().get(i).getDescripcion(), r.getDescripcion());
            assertSame(c, r.getCategoria());
            Reserva expected = original.getReservas().get(i), actual = loaded.getReservas().get(i);
            assertEquals(expected.getId(), actual.getId()); assertEquals(expected.getActividad(), actual.getActividad());
            assertEquals(expected.getFecha(), actual.getFecha()); assertEquals(expected.getHoraInicio(), actual.getHoraInicio());
            assertEquals(expected.getHoraFin(), actual.getHoraFin()); assertEquals(expected.getEstado(), actual.getEstado());
            assertSame(loadedAna, actual.getFuncionario());
            assertEquals(expected.getRecursos().size(), actual.getRecursos().size());
            for (Recurso assigned : actual.getRecursos()) {
                assertSame(loaded.getRecursos().stream().filter(r0 -> r0.getId().equals(assigned.getId())).findFirst().orElseThrow(), assigned);
            }
        }
        assertTrue(loaded.getReservas().get(0).estaActiva()); assertFalse(loaded.getReservas().get(1).estaActiva());
        // Segunda escritura/lectura asegura que no se agregan duplicados ni recursos anidados espurios.
        XmlStorage.guardar(loaded); Data twice = XmlStorage.cargar();
        assertEquals(2, twice.getRecursos().size()); assertEquals(2, twice.getReservas().size());
        assertEquals(2, twice.getReservas().get(0).getRecursos().size());
    }
    private static void orphanBehavior() throws Exception {
        // Caracterización explícita de una limitación; NO afirma integridad referencial.
        IntegrationFixture fixture = new IntegrationFixture();
        fixture.reservar("Clase", "08:00", "09:00");
        fixture.service.eliminarRecurso("R1");
        assertEquals(1, fixture.service.listarReservas().get(0).getRecursos().size());
        assertEquals(0, XmlStorage.cargar().getReservas().get(0).getRecursos().size());
        fixture.service.eliminarFuncionario("F1");
        assertEquals(1, fixture.service.listarReservas().size());
        assertEquals(0, XmlStorage.cargar().getReservas().size());
        System.out.println("LIMITACION CONFIRMADA: eliminar referencias pierde recursos/reservas al recargar XML.");
    }
}
