package reservas.logic;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
}