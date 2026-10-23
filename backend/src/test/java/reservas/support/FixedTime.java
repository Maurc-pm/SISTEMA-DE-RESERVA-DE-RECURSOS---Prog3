package reservas.support;

import org.mockito.MockedStatic;
import java.time.*;
import static org.mockito.Mockito.*;

/** Reloj fijo por hilo; no cambia el reloj del sistema ni la zona horaria global. */
public final class FixedTime implements AutoCloseable {
    public static final LocalDate TODAY = LocalDate.of(2026, 9, 16);
    public static final LocalTime NOW = LocalTime.of(12, 0);
    private final MockedStatic<Clock> clock;

    public FixedTime() {
        Clock fixed = Clock.fixed(Instant.parse("2026-09-16T12:00:00Z"), ZoneOffset.UTC);
        clock = mockStatic(Clock.class, CALLS_REAL_METHODS);
        clock.when(Clock::systemDefaultZone).thenReturn(fixed);
    }

    @Override public void close() { clock.close(); }
}
