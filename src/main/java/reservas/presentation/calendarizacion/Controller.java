package reservas.presentation.calendarizacion;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.borders.Border;
import reservas.logic.Categoria;
import reservas.logic.Recurso;
import reservas.logic.Reserva;
import reservas.logic.Service;
import reservas.logic.Celda;

import java.awt.Desktop;
import java.io.File;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class Controller {

    private static final int HORAS_DIA = 24;

    private final View view;
    private final Model model;

    public Controller(View view, Model model) {

        this.view = view;
        this.model = model;

        view.setModel(model);
        view.setController(this);

        cargarCategorias();

        /**
         * mientras esta pestaña permanece oculta, se refresca
         * el combo al volver a mostrarla.
         */
        view.getPanel().addHierarchyListener(e -> {

            if ((e.getChangeFlags()
                    & java.awt.event.HierarchyEvent.SHOWING_CHANGED) != 0
                    && view.getPanel().isShowing()) {

                cargarCategorias();
            }
        });
    }

    // =========================================================
    // CARGAR MATRIZ
    // =========================================================

    public void cargar() {

        try {

            LocalDate fecha = view.getFecha();
            Categoria categoria = view.getCategoriaSeleccionada();

            if (fecha == null) {
                throw new Exception("Debe seleccionar una fecha");
            }

            if (categoria == null
                    || categoria.getId() == null
                    || categoria.getId().trim().isEmpty()) {

                throw new Exception("Debe seleccionar una categoría");
            }

            List<Recurso> recursos =
                    Service.instance()
                            .buscarRecursosPorCategoria(categoria);

            List<Reserva> reservas =
                    Service.instance().listarReservas();

            Celda[][] matriz =
                    construirMatriz(fecha, recursos, reservas);

            model.setMatriz(recursos, matriz);

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
            LocalDate fecha = view.getFecha();
            Categoria categoria = view.getCategoriaSeleccionada();

            if (fecha == null) {
                throw new Exception("Debe seleccionar una fecha");
            }

            if (categoria == null || categoria.getId() == null || categoria.getId().trim().isEmpty()) {
                throw new Exception("Debe seleccionar una categoría");
            }

            List<Recurso> recursos = Service.instance().buscarRecursosPorCategoria(categoria);

            List<Reserva> reservas = Service.instance().listarReservas();

            Celda[][] matriz = construirMatriz(fecha, recursos, reservas);

            String dest = "calendarizacion.pdf";

            PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA);

            PdfWriter writer = new PdfWriter(dest);
            PdfDocument pdf = new PdfDocument(writer);

            Document document = new Document(pdf);
            document.setMargins(20, 20, 20, 20);

            // =========================
            // TÍTULO
            // =========================

            Paragraph titulo = new Paragraph("Calendarización de Recursos").setFont(font).setBold().setFontSize(16).setTextAlignment(TextAlignment.CENTER);
            document.add(titulo);

            // =========================
            // DATOS DEL REPORTE
            // =========================

            document.add(new Paragraph("Fecha: " + fecha).setFont(font));
            document.add(new Paragraph("Categoría: " + categoria.getDescripcion()).setFont(font));
            document.add(new Paragraph("\n"));

            // =========================
            // TABLA
            // =========================

            Table tabla = new Table(recursos.size() + 1);
            tabla.useAllAvailableWidth();

            // Encabezado de hora
            tabla.addHeaderCell(getCell(new Paragraph("Hora").setFont(font).setBold(), TextAlignment.CENTER, true));

            // Encabezados de recursos
            for (Recurso recurso : recursos) {
                tabla.addHeaderCell(getCell(new Paragraph(recurso.getDescripcion()).setFont(font).setBold(), TextAlignment.CENTER, true));
            }

            // =========================
            // FILAS DE HORAS
            // =========================

            for (int hora = 0; hora < HORAS_DIA; hora++) {

                String horaTexto = String.format("%02d:00", hora);

                tabla.addCell(getCell(new Paragraph(horaTexto).setFont(font), TextAlignment.CENTER, true));

                for (int columna = 0; columna < recursos.size(); columna++) {
                    Celda celda = matriz[hora][columna];

                    String texto = "";

                    if (celda != null && celda.isOcupada()) {

                        texto = String.join("\n", celda.getTextos());
                    }

                    tabla.addCell(getCell(new Paragraph(texto).setFont(font), TextAlignment.CENTER, true));
                }
            }

            document.add(tabla);
            document.close();
            openPdf(dest);

        } catch (Exception ex) {
            view.mostrarError("No se pudo generar el PDF: " + ex.getMessage());
        }
    }

    private Cell getCell(
            Paragraph paragraph,
            TextAlignment alignment,
            boolean hasBorder) {

        Cell cell = new Cell().add(paragraph);

        cell.setPadding(0);
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
                    System.out.println(
                            "AWT Desktop no es soportado."
                    );
                }

            } else {
                System.out.println(
                        "El archivo PDF no existe."
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =========================================================
    // CONSTRUCCIÓN DE LA MATRIZ HORA x RECURSO
    // =========================================================

    private Celda[][] construirMatriz(
            LocalDate fecha,
            List<Recurso> recursos,
            List<Reserva> reservas
    ) {

        Celda[][] matriz = new Celda[HORAS_DIA][recursos.size()];

        for (int hora = 0; hora < HORAS_DIA; hora++) {
            for (int columna = 0; columna < recursos.size(); columna++) {
                matriz[hora][columna] = new Celda();
            }
        }

        for (Reserva reserva : reservas) {

            if (!reserva.estaActiva()) {
                continue;
            }

            if (!fecha.equals(reserva.getFecha())) {
                continue;
            }

            String texto =
                    reserva.getActividad()
                            + " - "
                            + reserva.getFuncionario().getNombre();

            for (Recurso recurso : reserva.getRecursos()) {

                int columna = recursos.indexOf(recurso);

                if (columna < 0) {
                    continue;
                }

                marcarHorasOcupadas(
                        matriz,
                        columna,
                        reserva.getHoraInicio(),
                        reserva.getHoraFin(),
                        texto
                );
            }
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

    private void cargarCategorias() {

        model.setCategorias(
                Service.instance().listarCategorias()
        );
    }
}
