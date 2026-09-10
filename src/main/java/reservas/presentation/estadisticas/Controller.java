package reservas.presentation.estadisticas;

import reservas.logic.EstadisticaService;
import reservas.logic.Service;

import javax.swing.*;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.layout.element.Image;
import org.jfree.chart.JFreeChart;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

import java.awt.Desktop;
import java.io.File;
import java.time.LocalDate;
import java.util.Map;

public class Controller {

    private final View view;
    private final Model model;
    private final EstadisticaService estadisticaService;

    public Controller(View view, Model model) {
        this.view = view;
        this.model = model;
        this.estadisticaService = new EstadisticaService();

        view.setController(this);
    }

    public void consultarRecursos() {

        try {

            model.setRecursos(
                    estadisticaService.recursosPorCategoria(
                            view.getDesdeRecursos(),
                            view.getHastaRecursos(),
                            Service.instance().listarReservas()
                    )
            );

            view.mostrarRecursos(
                    model.getRecursos()
            );

            view.mostrarGraficoRecursos(
                    model.getRecursos()
            );

        } catch (Exception ex) {

            JOptionPane.showMessageDialog(
                    view.getPanel(),
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
    public void consultarActividades() {

        try {

            model.setActividades(
                    estadisticaService.actividadesPorSemana(
                            view.getDesdeRecursos(),
                            view.getHastaRecursos(),
                            Service.instance().listarReservas()
                    )
            );

            view.mostrarActividades(
                    model.getActividades()
            );

            view.mostrarGraficoActividades(
                    model.getActividades()
            );

        } catch (Exception ex) {

            JOptionPane.showMessageDialog(
                    view.getPanel(),
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    public void imprimirRecursos() {

        try {

            LocalDate desde = view.getDesdeRecursos();
            LocalDate hasta = view.getHastaRecursos();

            if (desde == null || hasta == null) {
                throw new Exception("Debe seleccionar el rango de fechas");
            }

            var datos =
                    estadisticaService.recursosPorCategoria(
                            desde,
                            hasta,
                            Service.instance().listarReservas()
                    );

            String dest = "estadisticas_recursos.pdf";

            PdfFont font =
                    PdfFontFactory.createFont(
                            StandardFonts.HELVETICA
                    );

            PdfWriter writer = new PdfWriter(dest);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            document.setMargins(20, 20, 20, 20);

            Paragraph titulo =
                    new Paragraph(
                            "Recursos reservados por categoría"
                    )
                            .setFont(font)
                            .setBold()
                            .setFontSize(16)
                            .setTextAlignment(
                                    TextAlignment.CENTER
                            );

            document.add(titulo);

            document.add(
                    new Paragraph(
                            "Desde: " + desde
                                    + "   Hasta: " + hasta
                    ).setFont(font)
            );

            document.add(new Paragraph("\n"));

            Table tabla = new Table(
                    new float[]{3, 1}
            );

            tabla.useAllAvailableWidth();

            tabla.addHeaderCell(
                    getCell(
                            new Paragraph("Categoría")
                                    .setFont(font)
                                    .setBold(),
                            TextAlignment.CENTER,
                            true
                    )
            );

            tabla.addHeaderCell(
                    getCell(
                            new Paragraph("Cantidad")
                                    .setFont(font)
                                    .setBold(),
                            TextAlignment.CENTER,
                            true
                    )
            );

            for (var entrada : datos.entrySet()) {

                tabla.addCell(
                        getCell(
                                new Paragraph(
                                        entrada.getKey()
                                ).setFont(font),
                                TextAlignment.LEFT,
                                true
                        )
                );

                tabla.addCell(
                        getCell(
                                new Paragraph(
                                        String.valueOf(
                                                entrada.getValue()
                                        )
                                ).setFont(font),
                                TextAlignment.CENTER,
                                true
                        )
                );
            }

            // =========================
// GRÁFICA
// =========================

            document.add(new Paragraph("\n"));

            JFreeChart grafico =
                    view.crearGraficoRecursos(datos);

            BufferedImage imagenGrafico =
                    grafico.createBufferedImage(700, 350);

            ByteArrayOutputStream baos =
                    new ByteArrayOutputStream();

            ImageIO.write(imagenGrafico, "png", baos);

            ImageData imageData =
                    ImageDataFactory.create(
                            baos.toByteArray()
                    );

            Image image =
                    new Image(imageData);

            image.setAutoScale(true);

            document.add(image);

            document.add(tabla);

            document.close();

            openPdf(dest);

        } catch (Exception ex) {

            JOptionPane.showMessageDialog(
                    view.getPanel(),
                    "No se pudo generar el PDF: "
                            + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    public void imprimirActividades() {

        try {

            LocalDate desde = view.getDesdeRecursos();
            LocalDate hasta = view.getHastaRecursos();

            if (desde == null || hasta == null) {
                throw new Exception("Debe seleccionar el rango de fechas");
            }

            var datos =
                    estadisticaService.actividadesPorSemana(
                            desde,
                            hasta,
                            Service.instance().listarReservas()
                    );

            String dest = "estadisticas_actividades.pdf";

            PdfFont font =
                    PdfFontFactory.createFont(
                            StandardFonts.HELVETICA
                    );

            PdfWriter writer = new PdfWriter(dest);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            document.setMargins(20, 20, 20, 20);

            Paragraph titulo =
                    new Paragraph(
                            "Actividades programadas por semana"
                    )
                            .setFont(font)
                            .setBold()
                            .setFontSize(16)
                            .setTextAlignment(
                                    TextAlignment.CENTER
                            );

            document.add(titulo);

            document.add(
                    new Paragraph(
                            "Desde: " + desde
                                    + "   Hasta: " + hasta
                    ).setFont(font)
            );

            document.add(new Paragraph("\n"));

            Table tabla = new Table(
                    new float[]{3, 1}
            );

            tabla.useAllAvailableWidth();

            tabla.addHeaderCell(
                    getCell(
                            new Paragraph("Semana")
                                    .setFont(font)
                                    .setBold(),
                            TextAlignment.CENTER,
                            true
                    )
            );

            tabla.addHeaderCell(
                    getCell(
                            new Paragraph("Cantidad")
                                    .setFont(font)
                                    .setBold(),
                            TextAlignment.CENTER,
                            true
                    )
            );

            for (var entrada : datos.entrySet()) {

                tabla.addCell(
                        getCell(
                                new Paragraph(
                                        entrada.getKey()
                                ).setFont(font),
                                TextAlignment.LEFT,
                                true
                        )
                );

                tabla.addCell(
                        getCell(
                                new Paragraph(
                                        String.valueOf(
                                                entrada.getValue()
                                        )
                                ).setFont(font),
                                TextAlignment.CENTER,
                                true
                        )
                );
            }

            // =========================
// GRÁFICA
// =========================

            document.add(new Paragraph("\n"));

            JFreeChart grafico =
                    view.crearGraficoActividades(datos);

            BufferedImage imagenGrafico =
                    grafico.createBufferedImage(700, 350);

            ByteArrayOutputStream baos =
                    new ByteArrayOutputStream();

            ImageIO.write(imagenGrafico, "png", baos);

            ImageData imageData =
                    ImageDataFactory.create(
                            baos.toByteArray()
                    );

            Image image =
                    new Image(imageData);

            image.setAutoScale(true);

            document.add(image);

            document.add(tabla);

            document.close();

            openPdf(dest);

        } catch (Exception ex) {

            JOptionPane.showMessageDialog(
                    view.getPanel(),
                    "No se pudo generar el PDF: "
                            + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private Cell getCell(
            Paragraph paragraph,
            TextAlignment alignment,
            boolean hasBorder) {

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

            if (pdfFile.exists()
                    && Desktop.isDesktopSupported()) {

                Desktop.getDesktop().open(pdfFile);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}