package reservas.logic;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import reservas.ai.ReservaExtraccion;
import reservas.ai.ReservaExtractorService;
import reservas.support.ServiceTestSupport;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReservaIATest extends ServiceTestSupport {
    @ParameterizedTest @NullAndEmptySource @ValueSource(strings = {"  "})
    void entradaVaciaNoInvocaExtractor(String frase) throws Exception {
        ReservaExtractorService extractor = mock(ReservaExtractorService.class); extractor(extractor);
        assertEquals("Debe escribir una descripción de la reserva", assertThrows(Exception.class,
                () -> service.extraerReservaConIA(frase)).getMessage());
        verifyNoInteractions(extractor);
    }
    @Test void enviaFraseNormalizadaCategoriasYFechaFijaSinLlamadaExterna() throws Exception {
        categoria("Sala"); categoria("Equipo");
        ReservaExtractorService extractor = mock(ReservaExtractorService.class); extractor(extractor);
        ReservaExtraccion esperado = new ReservaExtraccion("Clase", "2026-09-17", "08:00", "09:00", List.of("Sala"));
        when(extractor.extraer("mañana una sala", "[Sala, Equipo]", "2026-09-16")).thenReturn(esperado);
        assertSame(esperado, service.extraerReservaConIA(" mañana una sala "));
        verify(extractor, times(1)).extraer("mañana una sala", "[Sala, Equipo]", "2026-09-16");
        assertTrue(service.listarReservas().isEmpty());
    }
    @Test void errorDelExtractorSePropagaSinCrearReserva() throws Exception {
        ReservaExtractorService extractor = mock(ReservaExtractorService.class); extractor(extractor);
        when(extractor.extraer(anyString(), anyString(), anyString())).thenThrow(new IllegalStateException("Sin servicio"));
        assertEquals("Sin servicio", assertThrows(IllegalStateException.class,
                () -> service.extraerReservaConIA("Reservar sala")).getMessage());
        assertTrue(service.listarReservas().isEmpty());
        storage.verifyNoInteractions();
    }
}
