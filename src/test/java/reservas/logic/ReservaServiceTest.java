package reservas.logic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import reservas.data.XmlStorage;
import reservas.support.FixedTime;
import reservas.support.ServiceTestSupport;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReservaServiceTest extends ServiceTestSupport {
    private Funcionario ana;
    private Categoria sala;
    private Recurso r1;
    private final LocalDate fecha = FixedTime.TODAY.plusDays(1);

    @BeforeEach void prepararRecursos() throws Exception {
        ana = funcionario("F1", "Ana"); sala = categoria("Sala"); r1 = recurso("R1", sala);
        storage.clearInvocations();
    }
    private Reserva nueva(String inicio, String fin) { return reserva(ana, fecha, inicio, fin); }
    private Reserva crear(String inicio, String fin) throws Exception {
        Reserva r = nueva(inicio, fin); service.crearReserva(r, List.of(sala)); return r;
    }
    @Test void reservaValidaAsignaRecursoIdYEstadoYSePersiste() throws Exception {
        Reserva r = crear("08:00", "10:00");
        assertEquals("RES-000001", r.getId()); assertTrue(r.estaActiva());
        assertEquals(List.of(r1), r.getRecursos());
        assertEquals(List.of(r), service.listarReservasFuncionario(ana));
        storage.verify(() -> XmlStorage.guardar(data), times(1));
    }
    @ParameterizedTest(name = "ocupada 08:00-10:00, solicitud {0}-{1}")
    @CsvSource({"08:00,10:00", "08:30,09:30", "07:00,11:00", "07:30,08:30", "09:30,10:30"})
    void reservasSolapadasNoUsanMismoRecurso(String inicio, String fin) throws Exception {
        Reserva existente = crear("08:00", "10:00");
        Reserva intento = nueva(inicio, fin); storage.clearInvocations();
        assertTrue(assertThrows(Exception.class, () -> service.crearReserva(intento, List.of(sala)))
                .getMessage().contains("No hay recursos disponibles"));
        assertEquals(List.of(existente), service.listarReservas());
        assertTrue(intento.getRecursos().isEmpty()); assertEquals("", intento.getId());
        storage.verifyNoInteractions();
    }
    @ParameterizedTest @CsvSource({"10:00,11:00", "07:00,08:00"})
    void reservasConBordeExactoNoSeSolapan(String inicio, String fin) throws Exception {
        crear("08:00", "10:00");
        Reserva siguiente = crear(inicio, fin);
        assertEquals(List.of(r1), siguiente.getRecursos());
        assertEquals(2, service.listarReservas().size());
        assertEquals("RES-000002", siguiente.getId());
    }
    @Test void segundoRecursoSeAsignaCuandoPrimeroEstaOcupado() throws Exception {
        Recurso r2 = recurso("R2", sala);
        Reserva primera = crear("08:00", "10:00"), segunda = crear("09:00", "11:00");
        assertEquals(List.of(r1), primera.getRecursos());
        assertEquals(List.of(r2), segunda.getRecursos());
        assertThrows(Exception.class, () -> crear("09:30", "10:30"));
        assertEquals(2, service.listarReservas().size());
    }
    @Test void otraFechaNoBloqueaMismoHorario() throws Exception {
        crear("08:00", "10:00");
        Reserva otra = reserva(ana, fecha.plusDays(1), "08:00", "10:00");
        service.crearReserva(otra, List.of(sala));
        assertEquals(List.of(r1), otra.getRecursos());
    }
    @Test void categoriaSinRecursosInformaCategoriaYNoGuarda() throws Exception {
        Categoria vacia = categoria("Proyectores"); storage.clearInvocations();
        assertEquals("No hay recursos disponibles para:\n- Proyectores\n", assertThrows(Exception.class,
                () -> service.crearReserva(nueva("08:00", "09:00"), List.of(vacia))).getMessage());
        assertTrue(service.listarReservas().isEmpty()); storage.verifyNoInteractions();
    }
    @Test void multiplesCategoriasAsignanExactamenteUnRecursoDeCadaUna() throws Exception {
        Categoria equipo = categoria("Equipo"); Recurso proyector = recurso("P1", equipo);
        recurso("R2", sala); recurso("P2", equipo);
        Reserva r = nueva("08:00", "09:00"); service.crearReserva(r, List.of(sala, equipo));
        assertEquals(List.of(r1, proyector), r.getRecursos());
        assertEquals(1, service.listarReservas().size());
    }
    @ParameterizedTest @ValueSource(booleans = {true, false})
    void reservaConVariasCategoriasEsAtomicaIndependientementeDelOrden(boolean disponiblePrimero) throws Exception {
        Categoria sinRecursos = categoria("Equipo"); Reserva intento = nueva("08:00", "09:00");
        List<Categoria> categorias = disponiblePrimero ? List.of(sala, sinRecursos) : List.of(sinRecursos, sala);
        storage.clearInvocations();
        assertThrows(Exception.class, () -> service.crearReserva(intento, categorias));
        assertTrue(service.listarReservas().isEmpty());
        assertTrue(intento.getRecursos().isEmpty()); assertEquals("", intento.getId());
        storage.verifyNoInteractions();
        Reserva posterior = crear("08:00", "09:00");
        assertEquals(List.of(r1), posterior.getRecursos()); assertEquals("RES-000001", posterior.getId());
    }
    @ParameterizedTest @CsvSource({"10:00,08:00", "08:00,08:00"})
    void intervaloInvalidoEsRechazado(String inicio, String fin) {
        assertEquals("La hora de inicio debe ser anterior a la hora final", assertThrows(Exception.class,
                () -> crear(inicio, fin)).getMessage());
        assertTrue(service.listarReservas().isEmpty()); storage.verifyNoInteractions();
    }
    @ParameterizedTest @NullAndEmptySource @ValueSource(strings = {" "})
    void actividadVaciaEsRechazada(String actividad) {
        Reserva r = nueva("08:00", "09:00"); r.setActividad(actividad);
        assertEquals("La actividad es requerida", assertThrows(Exception.class,
                () -> service.crearReserva(r, List.of(sala))).getMessage());
        assertTrue(service.listarReservas().isEmpty());
    }
    @Test void fechaNulaEsRechazada() {
        Reserva r = nueva("08:00", "09:00"); r.setFecha(null);
        assertEquals("La fecha es requerida", assertThrows(Exception.class,
                () -> service.crearReserva(r, List.of(sala))).getMessage());
    }
    @Test void fechaPasadaEsAceptadaPorLaImplementacionActual() throws Exception {
        Reserva r = reserva(ana, FixedTime.TODAY.minusDays(1), "08:00", "09:00");
        service.crearReserva(r, List.of(sala));
        assertEquals(List.of(r), service.listarReservas()); assertEquals(List.of(r1), r.getRecursos());
    }
    @ParameterizedTest @ValueSource(booleans = {true, false})
    void horasNulasSonRechazadas(boolean inicio) {
        Reserva r = nueva("08:00", "09:00");
        if (inicio) r.setHoraInicio(null); else r.setHoraFin(null);
        assertEquals("Las horas son requeridas", assertThrows(Exception.class,
                () -> service.crearReserva(r, List.of(sala))).getMessage());
    }
    @Test void reservaYFuncionarioSonRequeridos() {
        assertEquals("La reserva es requerida", assertThrows(Exception.class,
                () -> service.crearReserva(null, List.of(sala))).getMessage());
        Reserva r = nueva("08:00", "09:00"); r.setFuncionario(null);
        assertEquals("El funcionario es requerido", assertThrows(Exception.class,
                () -> service.crearReserva(r, List.of(sala))).getMessage());
    }
    @ParameterizedTest @NullAndEmptySource
    void categoriasSonRequeridas(List<Categoria> categorias) {
        assertEquals("Debe seleccionar al menos una categoría", assertThrows(Exception.class,
                () -> service.crearReserva(nueva("08:00", "09:00"), categorias)).getMessage());
    }
    @Test void listarReservasSoloRetornaLasDelFuncionarioPorIdentidadDeUsuario() throws Exception {
        Reserva propia = crear("08:00", "09:00");
        Funcionario otro = funcionario("F2", "Luis");
        Reserva ajena = reserva(otro, fecha, "10:00", "11:00"); service.crearReserva(ajena, List.of(sala));
        assertEquals(List.of(propia), service.listarReservasFuncionario(new Funcionario("F1", "", "copia", "")));
        assertEquals(List.of(ajena), service.listarReservasFuncionario(otro));
    }
    @Test void cancelarReservaFuturaLiberaRecursosYPersisteEstado() throws Exception {
        Reserva r = crear("08:00", "10:00"); storage.clearInvocations();
        service.cancelarReserva(r);
        assertFalse(r.estaActiva()); assertEquals(Reserva.CANCELADA, r.getEstado());
        storage.verify(() -> XmlStorage.guardar(data), times(1));
        Reserva reemplazo = crear("08:00", "10:00");
        assertEquals(List.of(r1), reemplazo.getRecursos());
        assertEquals(2, service.listarReservasFuncionario(ana).size());
    }
    @ParameterizedTest @CsvSource({"0,11:00,13:00", "0,12:00,13:00", "-1,13:00,14:00"})
    void noSeCancelaReservaIniciadaExactaOPasada(int dias, String inicio, String fin) throws Exception {
        Reserva r = reserva(ana, FixedTime.TODAY.plusDays(dias), inicio, fin);
        service.crearReserva(r, List.of(sala)); storage.clearInvocations();
        assertEquals("Solo se pueden cancelar reservas futuras", assertThrows(Exception.class,
                () -> service.cancelarReserva(r)).getMessage());
        assertTrue(r.estaActiva()); storage.verifyNoInteractions();
    }
    @Test void sePuedeCancelarHoySiAunNoEmpieza() throws Exception {
        Reserva r = reserva(ana, FixedTime.TODAY, "12:01", "13:00"); service.crearReserva(r, List.of(sala));
        assertDoesNotThrow(() -> service.cancelarReserva(r)); assertFalse(r.estaActiva());
    }
    @Test void noSeCancelaDosVecesNiReservaNula() throws Exception {
        Reserva r = crear("08:00", "09:00"); service.cancelarReserva(r); storage.clearInvocations();
        assertEquals("La reserva ya está cancelada", assertThrows(Exception.class,
                () -> service.cancelarReserva(r)).getMessage());
        assertEquals("Reserva requerida", assertThrows(Exception.class,
                () -> service.cancelarReserva(null)).getMessage());
        storage.verifyNoInteractions();
    }
}
