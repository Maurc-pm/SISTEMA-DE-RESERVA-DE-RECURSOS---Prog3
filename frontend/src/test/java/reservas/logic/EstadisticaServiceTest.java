package reservas.logic;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class EstadisticaServiceTest {

    @Test
    public void recursosPorCategoriaAgrupaCorrectamente()
            throws Exception {

        EstadisticaService service =
                new EstadisticaService();

        Categoria sala =
                new Categoria("1", "Sala");

        Categoria computadoras =
                new Categoria("2", "Computadoras");

        Recurso recurso1 =
                new Recurso("R1", sala, "Sala 1");

        Recurso recurso2 =
                new Recurso("R2", sala, "Sala 2");

        Recurso recurso3 =
                new Recurso(
                        "R3",
                        computadoras,
                        "Laptop"
                );

        Reserva reserva1 =
                new Reserva(
                        "RES1",
                        null,
                        "Reunión",
                        LocalDate.of(2026, 9, 9),
                        LocalTime.of(8, 0),
                        LocalTime.of(9, 0),
                        List.of(recurso1, recurso2),
                        Reserva.ACTIVA
                );

        Reserva reserva2 =
                new Reserva(
                        "RES2",
                        null,
                        "Clase",
                        LocalDate.of(2026, 9, 10),
                        LocalTime.of(10, 0),
                        LocalTime.of(11, 0),
                        List.of(recurso3),
                        Reserva.ACTIVA
                );

        Map<String, Integer> resultado =
                service.recursosPorCategoria(
                        LocalDate.of(2026, 9, 9),
                        LocalDate.of(2026, 9, 10),
                        List.of(reserva1, reserva2)
                );

        assertEquals(
                2,
                resultado.get("Sala")
        );

        assertEquals(
                1,
                resultado.get("Computadoras")
        );
    }
    @Test
    public void actividadesPorSemanaAgrupaCorrectamente()
            throws Exception {

        EstadisticaService service =
                new EstadisticaService();

        Reserva reserva1 =
                new Reserva(
                        "RES1",
                        null,
                        "Actividad 1",
                        LocalDate.of(2026, 9, 9),
                        LocalTime.of(8, 0),
                        LocalTime.of(9, 0),
                        List.of(),
                        Reserva.ACTIVA
                );

        Reserva reserva2 =
                new Reserva(
                        "RES2",
                        null,
                        "Actividad 2",
                        LocalDate.of(2026, 9, 10),
                        LocalTime.of(10, 0),
                        LocalTime.of(11, 0),
                        List.of(),
                        Reserva.ACTIVA
                );

        Map<String, Integer> resultado =
                service.actividadesPorSemana(
                        LocalDate.of(2026, 9, 7),
                        LocalDate.of(2026, 9, 13),
                        List.of(reserva1, reserva2)
                );

        assertEquals(
                2,
                resultado.get("07/09/2026 - 13/09/2026")
        );
    }
    @Test
    public void recursosRespetaRangoDeFechas()
            throws Exception {

        EstadisticaService service =
                new EstadisticaService();

        Categoria sala =
                new Categoria("1", "Sala");

        Recurso recurso =
                new Recurso("R1", sala, "Sala 1");

        Reserva dentroDelRango =
                new Reserva(
                        "RES1",
                        null,
                        "Actividad dentro",
                        LocalDate.of(2026, 9, 10),
                        LocalTime.of(8, 0),
                        LocalTime.of(9, 0),
                        List.of(recurso),
                        Reserva.ACTIVA
                );

        Reserva fueraDelRango =
                new Reserva(
                        "RES2",
                        null,
                        "Actividad fuera",
                        LocalDate.of(2026, 9, 20),
                        LocalTime.of(8, 0),
                        LocalTime.of(9, 0),
                        List.of(recurso),
                        Reserva.ACTIVA
                );

        Map<String, Integer> resultado =
                service.recursosPorCategoria(
                        LocalDate.of(2026, 9, 9),
                        LocalDate.of(2026, 9, 15),
                        List.of(
                                dentroDelRango,
                                fueraDelRango
                        )
                );

        assertEquals(
                1,
                resultado.get("Sala")
        );
    }

    private final LocalDate lunes = LocalDate.of(2026, 9, 7);
    private final EstadisticaService estadisticas = new EstadisticaService();

    private Reserva dato(LocalDate fecha, String estado) {
        Recurso recurso = new Recurso("R1", new Categoria("C1", "Sala"), "Sala 1");
        return new Reserva("RES", null, "Clase", fecha, LocalTime.of(8, 0),
                LocalTime.of(9, 0), List.of(recurso), estado);
    }

    @Test void rangoInclusivoCuentaAmbosExtremosEIgnoraExteriorEnAmbosReportes() throws Exception {
        LocalDate desde = lunes.plusDays(2), hasta = lunes.plusDays(4);
        List<Reserva> datos = List.of(dato(desde, Reserva.ACTIVA), dato(hasta, Reserva.ACTIVA),
                dato(desde.minusDays(1), Reserva.ACTIVA), dato(hasta.plusDays(1), Reserva.ACTIVA));
        assertEquals(Map.of("Sala", 2), estadisticas.recursosPorCategoria(desde, hasta, datos));
        assertEquals(Map.of("07/09/2026 - 13/09/2026", 2), estadisticas.actividadesPorSemana(desde, hasta, datos));
    }
    @Test void estadisticasIgnoranCanceladasYFechasNulas() throws Exception {
        List<Reserva> datos = List.of(dato(lunes, Reserva.ACTIVA), dato(lunes, Reserva.CANCELADA), dato(null, Reserva.ACTIVA));
        assertEquals(Map.of("Sala", 1), estadisticas.recursosPorCategoria(lunes, lunes, datos));
        assertEquals(Map.of("07/09/2026 - 13/09/2026", 1), estadisticas.actividadesPorSemana(lunes, lunes, datos));
    }
    @Test void semanasVaciasSeIncluyenConCeroYEnOrdenCronologico() throws Exception {
        Map<String,Integer> resultado = estadisticas.actividadesPorSemana(lunes, lunes.plusWeeks(2), List.of());
        assertEquals(List.of("07/09/2026 - 13/09/2026", "14/09/2026 - 20/09/2026", "21/09/2026 - 27/09/2026"),
                new java.util.ArrayList<>(resultado.keySet()));
        assertEquals(List.of(0, 0, 0), new java.util.ArrayList<>(resultado.values()));
        assertTrue(estadisticas.recursosPorCategoria(lunes, lunes.plusWeeks(2), List.of()).isEmpty());
    }
    @Test void domingoYLunesSiguientePertenecenASemanasDiferentes() throws Exception {
        List<Reserva> datos = List.of(dato(lunes.plusDays(6), Reserva.ACTIVA), dato(lunes.plusDays(7), Reserva.ACTIVA));
        assertEquals(Map.of("07/09/2026 - 13/09/2026", 1, "14/09/2026 - 20/09/2026", 1),
                estadisticas.actividadesPorSemana(lunes, lunes.plusDays(7), datos));
    }
    @Test void semanaQueCruzaAnioConservaLunesYDomingoCorrectos() throws Exception {
        LocalDate fecha = LocalDate.of(2027, 1, 1);
        assertEquals(Map.of("28/12/2026 - 03/01/2027", 1),
                estadisticas.actividadesPorSemana(fecha, fecha, List.of(dato(fecha, Reserva.ACTIVA))));
    }
    @Test void desdePosteriorAHastaEsRechazadoEnAmbosReportes() {
        String mensaje = "La fecha desde no puede ser posterior a la fecha hasta";
        assertEquals(mensaje, assertThrows(Exception.class,
                () -> estadisticas.recursosPorCategoria(lunes.plusDays(1), lunes, List.of())).getMessage());
        assertEquals(mensaje, assertThrows(Exception.class,
                () -> estadisticas.actividadesPorSemana(lunes.plusDays(1), lunes, List.of())).getMessage());
    }
    @org.junit.jupiter.params.ParameterizedTest
    @org.junit.jupiter.params.provider.ValueSource(booleans = {true, false})
    void fechasObligatoriasEnAmbosReportes(boolean faltaDesde) {
        LocalDate desde = faltaDesde ? null : lunes, hasta = faltaDesde ? lunes : null;
        String mensaje = "Debe indicar las fechas desde y hasta";
        assertEquals(mensaje, assertThrows(Exception.class,
                () -> estadisticas.recursosPorCategoria(desde, hasta, List.of())).getMessage());
        assertEquals(mensaje, assertThrows(Exception.class,
                () -> estadisticas.actividadesPorSemana(desde, hasta, List.of())).getMessage());
    }
    @Test void recursosInvalidosNoSeCuentanYReservaCuentaUnaSolaActividad() throws Exception {
        Reserva r = dato(lunes, Reserva.ACTIVA);
        r.setRecursos(java.util.Arrays.asList(null, new Recurso("X", null, "Sin categoría"),
                new Recurso("1", new Categoria("C1", "Sala"), "Uno"),
                new Recurso("2", new Categoria("C1", "Sala"), "Dos")));
        assertEquals(Map.of("Sala", 2), estadisticas.recursosPorCategoria(lunes, lunes, List.of(r)));
        assertEquals(Map.of("07/09/2026 - 13/09/2026", 1), estadisticas.actividadesPorSemana(lunes, lunes, List.of(r)));
    }
}
