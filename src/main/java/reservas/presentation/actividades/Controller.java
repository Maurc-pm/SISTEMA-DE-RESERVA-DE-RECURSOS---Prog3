package reservas.presentation.actividades;

import reservas.logic.Reserva;
import reservas.logic.Service;
import reservas.logic.Celda;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;

import java.awt.Desktop;
import java.io.File;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

public class Controller {

    private static final int HORAS_DIA = 24;
    private static final int DIAS_SEMANA = 7;

    private final View view;
    private final Model model;

    public Controller(View view, Model model) {

        this.view = view;
        this.model = model;

        view.setModel(model);
        view.setController(this);
    }

    // =========================================================
    // CARGAR MATRIZ DE LA SEMANA
    // =========================================================

    public void cargar() {

        try {

            LocalDate referencia = view.getFechaReferencia();

            if (referencia == null) {
                throw new Exception(
                        "Debe seleccionar una fecha de referencia"
                );
            }

            LocalDate lunes =
                    referencia.with(
                            TemporalAdjusters.previousOrSame(
                                    DayOfWeek.MONDAY
                            )
                    );

            List<LocalDate> dias = new ArrayList<>();

            for (int i = 0; i < DIAS_SEMANA; i++) {
                dias.add(lunes.plusDays(i));
            }

            List<Reserva> reservas =
                    Service.instance().listarReservas();

            Celda[][] matriz =
                    construirMatriz(dias, reservas);

            model.setMatriz(dias, matriz);

        } catch (Exception ex) {

            view.mostrarError(ex.getMessage());
        }
    }

    // =========================================================
    // IMPRIMIR (pendiente: se implementará el reporte PDF
    // en una fase posterior del proyecto)
    // =========================================================

    public void imprimir() {

        try {
            LocalDate referencia = view.getFechaReferencia();

            if (referencia == null) {
                throw new Exception("Debe seleccionar una fecha de referencia");
            }

            LocalDate lunes = referencia.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

            List<LocalDate> dias = new ArrayList<>();

            for (int i = 0; i < DIAS_SEMANA; i++) {
                dias.add(lunes.plusDays(i));
            }

            List<Reserva> reservas = Service.instance().listarReservas();

            Celda[][] matriz = construirMatriz(dias, reservas);

            String dest = "actividades.pdf";

            PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA);

            PdfWriter writer = new PdfWriter(dest);
            PdfDocument pdf = new PdfDocument(writer);

            // Horizontal porque tenemos 8 columnas
            Document document = new Document(pdf, PageSize.A4.rotate());

            document.setMargins(20, 20, 20, 20);

            // =========================
            // TÍTULO
            // =========================

            Paragraph titulo = new Paragraph("Programación Semanal de Actividades").setFont(font).setBold().setFontSize(16).setTextAlignment(TextAlignment.CENTER);

            document.add(titulo);
            document.add(new Paragraph("Semana: " + dias.get(0) + " al " + dias.get(6)).setFont(font));

            document.add(new Paragraph("\n"));

            // =========================
            // TABLA
            // =========================

            float[] anchos = {
                    1,
                    2, 2, 2, 2, 2, 2, 2
            };

            Table tabla = new Table(anchos);
            tabla.useAllAvailableWidth();

            // Hora
            tabla.addHeaderCell(getCell(new Paragraph("Hora").setFont(font).setBold(), TextAlignment.CENTER, true));

            // Días
            String[] nombresDias = {
                    "Lunes",
                    "Martes",
                    "Miércoles",
                    "Jueves",
                    "Viernes",
                    "Sábado",
                    "Domingo"
            };

            for (int i = 0; i < DIAS_SEMANA; i++) {
                String encabezado = nombresDias[i] + "\n" + dias.get(i);

                tabla.addHeaderCell(getCell(new Paragraph(encabezado).setFont(font).setBold(), TextAlignment.CENTER, true));
            }

            // =========================
            // HORAS
            // =========================

            for (int hora = 0; hora < HORAS_DIA; hora++) {
                tabla.addCell(getCell(new Paragraph(String.format("%02d:00", hora)).setFont(font), TextAlignment.CENTER, true));

                for (int dia = 0; dia < DIAS_SEMANA; dia++) {
                    Celda celda = matriz[hora][dia];

                    String texto = "";

                    if (celda != null && celda.isOcupada()) {
                        texto = celda.getTexto();
                    }

                    tabla.addCell(getCell(new Paragraph(texto).setFont(font).setFontSize(8), TextAlignment.CENTER, true));
                }
            }

            document.add(tabla);
            document.close();
            openPdf(dest);

        } catch (Exception ex) {
            view.mostrarError("No se pudo generar el PDF: " + ex.getMessage());
        }
    }

    private Cell getCell(Paragraph paragraph, TextAlignment alignment, boolean hasBorder) {

        Cell cell = new Cell().add(paragraph);

        cell.setPadding(2);
        cell.setTextAlignment(alignment);

        if (!hasBorder) {
            cell.setBorder(Border.NO_BORDER);
        }

        return cell;
    }

    private void openPdf(String path) {

        try {
            File pdfFile = new File(path);

            if (pdfFile.exists()) {
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(pdfFile);

                } else {
                    System.out.println("AWT Desktop no es soportado.");
                }

            } else {
                System.out.println("El archivo PDF no existe.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =========================================================
    // CONSTRUCCIÓN DE LA MATRIZ HORA x DÍA
    // =========================================================

    private Celda[][] construirMatriz(
            List<LocalDate> dias,
            List<Reserva> reservas
    ) {

        Celda[][] matriz = new Celda[HORAS_DIA][DIAS_SEMANA];

        for (int hora = 0; hora < HORAS_DIA; hora++) {
            for (int dia = 0; dia < DIAS_SEMANA; dia++) {
                matriz[hora][dia] = new Celda();
            }
        }

        for (Reserva reserva : reservas) {

            if (!reserva.estaActiva()
                    || reserva.getFecha() == null) {
                continue;
            }

            int columna = dias.indexOf(reserva.getFecha());

            if (columna < 0) {
                continue;
            }

            String texto =
                    reserva.getActividad()
                            + " ("
                            + reserva.getFuncionario().getNombre()
                            + ")";

            marcarHorasOcupadas(
                    matriz,
                    columna,
                    reserva.getHoraInicio(),
                    reserva.getHoraFin(),
                    texto
            );
        }

        return matriz;
    }

    private void marcarHorasOcupadas(
            Celda[][] matriz,
            int columna,
            LocalTime horaInicio,
            LocalTime horaFin,
            String texto
    ) {

        for (int hora = 0; hora < HORAS_DIA; hora++) {

            LocalTime inicioSlot = LocalTime.of(hora, 0);

            LocalTime finSlot =
                    hora == HORAS_DIA - 1
                            ? LocalTime.MAX
                            : LocalTime.of(hora + 1, 0);

            boolean ocupaEsteSlot =
                    horaInicio.isBefore(finSlot)
                            && horaFin.isAfter(inicioSlot);

            if (ocupaEsteSlot) {
                matriz[hora][columna].agregar(texto);
            }
        }
    }
}
