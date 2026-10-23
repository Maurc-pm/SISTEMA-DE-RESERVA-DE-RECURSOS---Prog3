package reservas.logic;

import java.time.DayOfWeek;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.TreeMap;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class EstadisticaService {

    public EstadisticaService() {
    }

    public Map<String, Integer> recursosPorCategoria(
            LocalDate desde,
            LocalDate hasta,
            List<Reserva> reservas
    ) throws Exception {

        if (desde == null || hasta == null) {
            throw new Exception("Debe indicar las fechas desde y hasta");
        }

        if (desde.isAfter(hasta)) {
            throw new Exception(
                    "La fecha desde no puede ser posterior a la fecha hasta"
            );
        }

        Map<String, Integer> resultado =
                new LinkedHashMap<>();

        for (Reserva reserva : reservas) {

            if (!reserva.estaActiva()
                    || reserva.getFecha() == null) {
                continue;
            }

            boolean dentroDelPeriodo =
                    !reserva.getFecha().isBefore(desde)
                            && !reserva.getFecha().isAfter(hasta);

            if (!dentroDelPeriodo) {
                continue;
            }

            for (Recurso recurso : reserva.getRecursos()) {

                if (recurso == null
                        || recurso.getCategoria() == null) {
                    continue;
                }

                String categoria =
                        recurso.getCategoria()
                                .getDescripcion();

                resultado.put(
                        categoria,
                        resultado.getOrDefault(
                                categoria,
                                0
                        ) + 1
                );
            }
        }

        return resultado;
    }
    public Map<String, Integer> actividadesPorSemana(
            LocalDate desde,
            LocalDate hasta,
            List<Reserva> reservas
    ) throws Exception {

        if (desde == null || hasta == null) {
            throw new Exception("Debe indicar las fechas desde y hasta");
        }

        if (desde.isAfter(hasta)) {
            throw new Exception(
                    "La fecha desde no puede ser posterior a la fecha hasta"
            );
        }

        Map<LocalDate, Integer> semanas =
                new TreeMap<>();

        LocalDate lunes =
                desde.with(
                        TemporalAdjusters.previousOrSame(
                                DayOfWeek.MONDAY
                        )
                );

        LocalDate ultimoLunes =
                hasta.with(
                        TemporalAdjusters.previousOrSame(
                                DayOfWeek.MONDAY
                        )
                );

        while (!lunes.isAfter(ultimoLunes)) {
            semanas.put(lunes, 0);
            lunes = lunes.plusWeeks(1);
        }

        for (Reserva reserva : reservas) {

            if (!reserva.estaActiva()
                    || reserva.getFecha() == null) {
                continue;
            }

            boolean dentroDelPeriodo =
                    !reserva.getFecha().isBefore(desde)
                            && !reserva.getFecha().isAfter(hasta);

            if (!dentroDelPeriodo) {
                continue;
            }

            LocalDate lunesReserva =
                    reserva.getFecha().with(
                            TemporalAdjusters.previousOrSame(
                                    DayOfWeek.MONDAY
                            )
                    );

            semanas.put(
                    lunesReserva,
                    semanas.getOrDefault(
                            lunesReserva,
                            0
                    ) + 1
            );
        }

        Map<String, Integer> resultado =
                new LinkedHashMap<>();

        DateTimeFormatter formato =
                DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (Map.Entry<LocalDate, Integer> entry
                : semanas.entrySet()) {

            LocalDate inicioSemana = entry.getKey();
            LocalDate finSemana =
                    inicioSemana.plusDays(6);

            String nombreSemana =
                    inicioSemana.format(formato)
                            + " - "
                            + finSemana.format(formato);

            resultado.put(
                    nombreSemana,
                    entry.getValue()
            );
        }

        return resultado;
    }
}