package reservas.presentation.reservas;

import reservas.logic.Categoria;
import reservas.logic.Funcionario;
import reservas.logic.Reserva;
import reservas.logic.Service;
import reservas.logic.Sesion;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

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

import java.awt.Desktop;
import java.io.File;

public class Controller {

    private final View view;
    private final Model model;

    private final DateTimeFormatter formatoFecha =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public Controller(View view, Model model) {

        this.view = view;
        this.model = model;

        view.setModel(model);
        view.setController(this);

        cargarCategorias();
        cargarReservas();
        limpiar();
    }

    // =========================================================
    // AGREGAR CATEGORÍA
    // =========================================================

    public void agregarCategoria() {

        Categoria categoria = view.getCategoriaSeleccionada();

        if (categoria == null) {
            return;
        }

        List<Categoria> seleccionadas =
                new ArrayList<>(model.getCategoriasSeleccionadas());

        if (!seleccionadas.contains(categoria)) {
            seleccionadas.add(categoria);
        }

        model.setCategoriasSeleccionadas(seleccionadas);
    }

    // =========================================================
    // RESERVAR
    // =========================================================

    public void interpretarConIA() {
        try {

            var resultado =
                    Service.instance().extraerReservaConIA(
                            view.getTextoIA()
                    );

            LocalDate fecha = null;

            if (resultado.getFecha() != null && !resultado.getFecha().isBlank()) {
                fecha = LocalDate.parse(resultado.getFecha());
            }

            view.llenarFormularioIA(
                    resultado.getActividad(),
                    fecha,
                    resultado.getHoraInicio(),
                    resultado.getHoraFinal()
            );
            List<Categoria> categoriasDetectadas = new ArrayList<>();

            if (resultado.getCategoriasRecurso() != null) {

                for (String nombre : resultado.getCategoriasRecurso()) {

                    for (Categoria categoria : model.getCategorias()) {

                        if (categoria.getDescripcion()
                                .equalsIgnoreCase(nombre.trim())) {

                            categoriasDetectadas.add(categoria);
                            break;
                        }
                    }
                }
            }

            model.setCategoriasSeleccionadas(categoriasDetectadas);
        } catch (Exception ex) {
            view.mostrarError(ex.getMessage());
        }
    }


    public void reservar() {
        try {

            Funcionario funcionario = (Funcionario) Sesion.getUsuario();

            Reserva reserva = new Reserva();

            reserva.setFuncionario(funcionario);

            reserva.setActividad(view.getActividad());

            reserva.setHoraInicio(LocalTime.parse(view.getHoraInicio()));

            reserva.setHoraFin(LocalTime.parse(view.getHoraFinal()));

            LocalDate fecha = view.getFecha();

            if (fecha == null) {
                throw new Exception(
                        "Debe seleccionar una fecha"
                );
            }

            reserva.setFecha(fecha);

            Service.instance().crearReserva(
                    reserva,
                    model.getCategoriasSeleccionadas()
            );

            view.mostrarMensaje("Reserva realizada correctamente");

            cargarReservas();
            limpiar();

        } catch (Exception ex) {
            view.mostrarError(ex.getMessage());
        }
    }

    // =========================================================
    // CANCELAR RESERVA
    // =========================================================

    public void cancelarReserva() {
        try {
            Reserva reserva = model.getCurrent();

            if (reserva == null || reserva.getId() == null || reserva.getId().trim().isEmpty()) {

                throw new Exception("Debe seleccionar una reserva");
            }

            Service.instance().cancelarReserva(reserva);

            view.mostrarMensaje("Reserva cancelada correctamente");

            cargarReservas();
            limpiar();

        } catch (Exception ex) {
            view.mostrarError(ex.getMessage());
        }
    }

    // =========================================================
    // SELECCIONAR RESERVA
    // =========================================================

    public void seleccionar(int fila) {
        if (fila < 0 || fila >= model.getReservas().size()) {
            return;
        }

        Reserva reserva = model.getReservas().get(fila);

        model.setCurrent(reserva);
    }

    // =========================================================
    // LIMPIAR
    // =========================================================

    public void limpiar() {
        model.setCurrent(new Reserva());

        model.setCategoriasSeleccionadas(new ArrayList<>());

        view.limpiarFormulario();
        view.limpiarSeleccionTabla();
    }

    // =========================================================
    // CARGAR CATEGORÍAS
    // =========================================================

    private void cargarCategorias() {
        model.setCategorias(Service.instance().listarCategorias());
    }

    // =========================================================
    // CARGAR RESERVAS DEL FUNCIONARIO
    // =========================================================

    private void cargarReservas() {
        if (!(Sesion.getUsuario() instanceof Funcionario)) {
            model.setReservas(new ArrayList<>());
            return;
        }

        Funcionario funcionario = (Funcionario) Sesion.getUsuario();
        model.setReservas(Service.instance().listarReservasFuncionario(funcionario));
    }

    public void imprimir() {

        try {

            if (!(Sesion.getUsuario() instanceof Funcionario)) {
                throw new Exception(
                        "Solo un funcionario puede generar este reporte"
                );
            }

            Funcionario funcionario =
                    (Funcionario) Sesion.getUsuario();

            List<Reserva> reservas =
                    Service.instance()
                            .listarReservasFuncionario(funcionario);

            String path = "reservas.pdf";

            PdfWriter writer =
                    new PdfWriter(path);

            PdfDocument pdf =
                    new PdfDocument(writer);

            Document document =
                    new Document(pdf);

            PdfFont font =
                    PdfFontFactory.createFont(
                            StandardFonts.HELVETICA
                    );

            PdfFont bold =
                    PdfFontFactory.createFont(
                            StandardFonts.HELVETICA_BOLD
                    );

            Paragraph titulo =
                    new Paragraph("Reporte de Reservas")
                            .setFont(bold)
                            .setFontSize(18)
                            .setTextAlignment(
                                    TextAlignment.CENTER
                            );

            document.add(titulo);

            document.add(
                    new Paragraph(
                            "Funcionario: "
                                    + funcionario.getNombre()
                    ).setFont(font)
            );

            document.add(
                    new Paragraph("\n")
            );

            float[] anchos = {
                    2, 4, 2, 2, 2, 2, 5
            };

            Table tabla =
                    new Table(anchos);

            tabla.useAllAvailableWidth();

            tabla.addHeaderCell(
                    getCell(
                            new Paragraph("ID")
                                    .setFont(bold),
                            TextAlignment.CENTER,
                            true
                    )
            );

            tabla.addHeaderCell(
                    getCell(
                            new Paragraph("Actividad")
                                    .setFont(bold),
                            TextAlignment.CENTER,
                            true
                    )
            );

            tabla.addHeaderCell(
                    getCell(
                            new Paragraph("Fecha")
                                    .setFont(bold),
                            TextAlignment.CENTER,
                            true
                    )
            );

            tabla.addHeaderCell(
                    getCell(
                            new Paragraph("Inicio")
                                    .setFont(bold),
                            TextAlignment.CENTER,
                            true
                    )
            );

            tabla.addHeaderCell(
                    getCell(
                            new Paragraph("Fin")
                                    .setFont(bold),
                            TextAlignment.CENTER,
                            true
                    )
            );

            tabla.addHeaderCell(
                    getCell(
                            new Paragraph("Estado")
                                    .setFont(bold),
                            TextAlignment.CENTER,
                            true
                    )
            );

            tabla.addHeaderCell(
                    getCell(
                            new Paragraph("Recursos")
                                    .setFont(bold),
                            TextAlignment.CENTER,
                            true
                    )
            );

            for (Reserva reserva : reservas) {

                String recursos = "";

                if (reserva.getRecursos() != null) {

                    recursos =
                            reserva.getRecursos()
                                    .stream()
                                    .map(r -> r.getDescripcion())
                                    .reduce(
                                            "",
                                            (a, b) ->
                                                    a.isEmpty()
                                                            ? b
                                                            : a + ", " + b
                                    );
                }

                tabla.addCell(
                        new Paragraph(
                                reserva.getId()
                        ).setFont(font)
                );

                tabla.addCell(
                        new Paragraph(
                                reserva.getActividad()
                        ).setFont(font)
                );

                tabla.addCell(
                        new Paragraph(
                                reserva.getFecha()
                                        .format(formatoFecha)
                        ).setFont(font)
                );

                tabla.addCell(
                        new Paragraph(
                                reserva.getHoraInicio()
                                        .toString()
                        ).setFont(font)
                );

                tabla.addCell(
                        new Paragraph(
                                reserva.getHoraFin()
                                        .toString()
                        ).setFont(font)
                );

                tabla.addCell(
                        new Paragraph(
                                reserva.estaActiva()
                                        ? "ACTIVA"
                                        : "CANCELADA"
                        ).setFont(font)
                );

                tabla.addCell(
                        new Paragraph(recursos)
                                .setFont(font)
                );
            }

            document.add(tabla);

            document.close();

            openPdf(path);

        } catch (Exception ex) {

            view.mostrarError(
                    ex.getMessage()
            );
        }
    }

    private Cell getCell(
            Paragraph paragraph,
            TextAlignment alignment,
            boolean hasBorder) {

        Cell cell =
                new Cell().add(paragraph);

        cell.setPadding(2);
        cell.setTextAlignment(alignment);

        if (!hasBorder) {
            cell.setBorder(
                    Border.NO_BORDER
            );
        }

        return cell;
    }

    private void openPdf(String path) {

        try {

            File pdfFile =
                    new File(path);

            if (pdfFile.exists()) {

                if (Desktop.isDesktopSupported()) {

                    Desktop.getDesktop()
                            .open(pdfFile);

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
}