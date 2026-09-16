package reservas.support;

import reservas.logic.*;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;
import java.nio.file.*;
import javax.swing.JPanel;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/** Se ejecutan los generadores productivos reales, nunca PdfWriter/PdfDocument simulados. */
public final class PdfScenario {
    public static void main(String[] args) throws Exception {
        IsolatedJvm.assertDirectory(args[0]);
        try (FixedTime time = new FixedTime()) {
            IntegrationFixture f = new IntegrationFixture();
            f.reservar("Clase de integración", "08:30", "10:00");
            Reserva cancelada = f.reservar("ACTIVIDAD_CANCELADA", "14:00", "15:00");
            f.service.cancelarReserva(cancelada);
            Sesion.setUsuario(f.ana);
            String report = args[1];
            switch (report) {
                case "reservas" -> {
                    var view = mock(reservas.presentation.reservas.View.class);
                    doAnswer(inv -> { throw new AssertionError(inv.getArgument(0, String.class)); }).when(view).mostrarError(any());
                    var controller = new reservas.presentation.reservas.Controller(view, new reservas.presentation.reservas.Model());
                    assertDoesNotThrow(controller::imprimir);
                }
                case "funcionarios" -> {
                    var view = mock(reservas.presentation.funcionarios.View.class);
                    doAnswer(inv -> { throw new AssertionError(inv.getArgument(0, String.class)); }).when(view).mostrarError(any());
                    var controller = new reservas.presentation.funcionarios.Controller(view, new reservas.presentation.funcionarios.Model());
                    assertDoesNotThrow(controller::imprimir);
                }
                case "categorias" -> {
                    var view = mock(reservas.presentation.categorias.View.class);
                    doAnswer(inv -> { throw new AssertionError(inv.getArgument(0, String.class)); }).when(view).mostrarError(any());
                    var controller = new reservas.presentation.categorias.Controller(view, new reservas.presentation.categorias.Model());
                    assertDoesNotThrow(controller::imprimir);
                }
                case "recursos" -> {
                    var view = mock(reservas.presentation.recursos.View.class);
                    when(view.getPanel()).thenReturn(mock(JPanel.class));
                    doAnswer(inv -> { throw new AssertionError(inv.getArgument(0, String.class)); }).when(view).mostrarError(any());
                    var controller = new reservas.presentation.recursos.Controller(view, new reservas.presentation.recursos.Model());
                    assertDoesNotThrow(controller::imprimir);
                }
                case "calendarizacion" -> {
                    var view = mock(reservas.presentation.calendarizacion.View.class);
                    when(view.getPanel()).thenReturn(mock(JPanel.class));
                    when(view.getFecha()).thenReturn(IntegrationFixture.DATE);
                    when(view.getCategoriaSeleccionada()).thenReturn(f.sala);
                    doAnswer(inv -> { throw new AssertionError(inv.getArgument(0, String.class)); }).when(view).mostrarError(any());
                    var controller = new reservas.presentation.calendarizacion.Controller(view, new reservas.presentation.calendarizacion.Model());
                    assertDoesNotThrow(controller::imprimir);
                }
                case "actividades" -> {
                    var view = mock(reservas.presentation.actividades.View.class);
                    when(view.getFechaReferencia()).thenReturn(IntegrationFixture.DATE);
                    doAnswer(inv -> { throw new AssertionError(inv.getArgument(0, String.class)); }).when(view).mostrarError(any());
                    var controller = new reservas.presentation.actividades.Controller(view, new reservas.presentation.actividades.Model());
                    assertDoesNotThrow(controller::imprimir);
                }
                case "estadisticas_recursos", "estadisticas_actividades" -> {
                    var view = mock(reservas.presentation.estadisticas.View.class);
                    when(view.getDesdeRecursos()).thenReturn(IntegrationFixture.DATE);
                    when(view.getHastaRecursos()).thenReturn(IntegrationFixture.DATE);
                    // Métodos productivos puros: se construye y dibuja un gráfico real.
                    when(view.crearGraficoRecursos(anyMap())).thenCallRealMethod();
                    when(view.crearGraficoActividades(anyMap())).thenCallRealMethod();
                    var controller = new reservas.presentation.estadisticas.Controller(view, new reservas.presentation.estadisticas.Model());
                    // Si la generación falla, JOptionPane lanza HeadlessException y falla la prueba.
                    if (report.endsWith("recursos")) assertDoesNotThrow(controller::imprimirRecursos);
                    else assertDoesNotThrow(controller::imprimirActividades);
                }
                default -> throw new IllegalArgumentException(report);
            }
            Path output = Path.of(report + ".pdf");
            assertTrue(Files.isRegularFile(output)); assertTrue(Files.size(output) > 500);
            byte[] bytes = Files.readAllBytes(output);
            assertEquals("%PDF-", new String(bytes, 0, 5, java.nio.charset.StandardCharsets.US_ASCII));
            try (PdfDocument pdf = new PdfDocument(new PdfReader(output.toString()))) {
                assertTrue(pdf.getNumberOfPages() > 0);
                StringBuilder all = new StringBuilder(); boolean hasImage = false;
                for (int i=1; i<=pdf.getNumberOfPages(); i++) {
                    all.append(PdfTextExtractor.getTextFromPage(pdf.getPage(i))).append('\n');
                    PdfDictionary xobjects = pdf.getPage(i).getResources().getResource(PdfName.XObject);
                    if (xobjects != null) for (PdfName key : xobjects.keySet()) {
                        PdfStream object = xobjects.getAsStream(key);
                        if (object != null && PdfName.Image.equals(object.getAsName(PdfName.Subtype))) {
                            assertEquals(700, object.getAsNumber(PdfName.Width).intValue());
                            assertEquals(350, object.getAsNumber(PdfName.Height).intValue());
                            assertTrue(object.getBytes().length > 0);
                            hasImage = true;
                        }
                    }
                }
                String text = all.toString();
                assertTrue(text.contains(args[2]), () -> "No aparece contenido esperado: " + args[2] + "\n" + text);
                switch (report) {
                    case "reservas" -> {
                        assertTrue(text.contains("Ana Pérez")); assertTrue(text.contains("Clase de integración"));
                        assertTrue(text.contains("Sala Norte")); assertTrue(text.contains("CANCELADA"));
                    }
                    case "funcionarios" -> { assertTrue(text.contains("F1")); assertTrue(text.contains("8888-0000")); }
                    case "categorias" -> assertTrue(text.contains(f.sala.getId()));
                    case "recursos" -> { assertTrue(text.contains("Sala Norte")); assertTrue(text.contains("Sala Sur")); }
                    case "calendarizacion", "actividades" -> {
                        assertTrue(text.contains("Clase de integración")); assertTrue(text.contains("Ana Pérez"));
                        assertFalse(text.contains("ACTIVIDAD_CANCELADA"));
                        assertTrue(text.contains("00:00")); assertTrue(text.contains("23:00"));
                    }
                    case "estadisticas_recursos", "estadisticas_actividades" -> {
                        assertTrue(hasImage, "Debe incluir el gráfico real como imagen");
                        assertTrue(text.contains("Cantidad"));
                    }
                }
            }
            Sesion.logout();
        }
    }
}
