package reservas.presentation.recursos;

import reservas.logic.Categoria;
import reservas.logic.Recurso;
import reservas.logic.Service;

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

    public Controller(View view, Model model) {

        this.view = view;
        this.model = model;

        view.setModel(model);
        view.setController(this);

        cargarCategorias();
        cargarRecursos();
        limpiar();

        view.getPanel().addHierarchyListener(e -> {

            if ((e.getChangeFlags()
                    & java.awt.event.HierarchyEvent.SHOWING_CHANGED) != 0
                    && view.getPanel().isShowing()) {

                cargarCategorias();
            }
        });
    }

    public void buscar() {

        Categoria categoria =
                view.getCategoriaFiltro();

        if (categoria == null
                || categoria.getId() == null
                || categoria.getId().trim().isEmpty()) {

            cargarRecursos();
            return;
        }

        List<Recurso> resultado =
                Service.instance()
                        .buscarRecursosPorCategoria(categoria);

        model.setRecursos(resultado);
    }

    public void guardar() {

        try {

            boolean editando =
                    model.getCurrent() != null
                            && model.getCurrent().getId() != null
                            && !model.getCurrent()
                            .getId()
                            .trim()
                            .isEmpty();

            Categoria categoria =
                    view.getCategoriaSeleccionada();

            if (categoria == null) {
                throw new Exception(
                        "Debe seleccionar una categoría"
                );
            }

            Recurso recurso = new Recurso();

            if (editando) {

                recurso.setId(
                        model.getCurrent().getId()
                );

            } else {

                recurso.setId(
                        view.getIdRecurso()
                );
            }

            recurso.setCategoria(categoria);

            recurso.setDescripcion(
                    view.getDescripcionRecurso()
            );

            if (editando) {

                Service.instance()
                        .modificarRecurso(recurso);

                view.mostrarMensaje(
                        "Recurso modificado correctamente"
                );

            } else {

                Service.instance()
                        .crearRecurso(recurso);

                view.mostrarMensaje(
                        "Recurso creado correctamente"
                );
            }

            cargarRecursos();
            limpiar();

        } catch (Exception ex) {

            view.mostrarError(
                    ex.getMessage()
            );
        }
    }

    public void borrar() {

        try {

            Recurso actual =
                    model.getCurrent();

            if (actual == null
                    || actual.getId() == null
                    || actual.getId()
                    .trim()
                    .isEmpty()) {

                throw new Exception(
                        "Debe seleccionar un recurso"
                );
            }

            Service.instance()
                    .eliminarRecurso(
                            actual.getId()
                    );

            view.mostrarMensaje(
                    "Recurso eliminado correctamente"
            );

            cargarRecursos();
            limpiar();

        } catch (Exception ex) {

            view.mostrarError(
                    ex.getMessage()
            );
        }
    }

    public void limpiar() {

        model.setCurrent(
                new Recurso()
        );

        view.limpiarSeleccionTabla();
    }

    public void seleccionar(int fila) {

        if (fila < 0
                || fila >= model
                .getRecursos()
                .size()) {

            return;
        }

        Recurso recurso =
                model.getRecursos()
                        .get(fila);

        model.setCurrent(recurso);
    }

    private void cargarCategorias() {

        model.setCategorias(
                Service.instance()
                        .listarCategorias()
        );
    }

    private void cargarRecursos() {

        model.setRecursos(
                Service.instance()
                        .listarRecursos()
        );
    }

    public void imprimir() {

        try {

            List<Recurso> recursos =
                    model.getRecursos();

            String path = "recursos.pdf";

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
                    new Paragraph("Reporte de Recursos")
                            .setFont(bold)
                            .setFontSize(18)
                            .setTextAlignment(
                                    TextAlignment.CENTER
                            );

            document.add(titulo);

            document.add(
                    new Paragraph("\n")
            );

            Table tabla =
                    new Table(
                            new float[]{2, 4, 4}
                    );

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
                            new Paragraph("Descripción")
                                    .setFont(bold),
                            TextAlignment.CENTER,
                            true
                    )
            );

            tabla.addHeaderCell(
                    getCell(
                            new Paragraph("Categoría")
                                    .setFont(bold),
                            TextAlignment.CENTER,
                            true
                    )
            );

            for (Recurso recurso : recursos) {

                tabla.addCell(
                        new Paragraph(
                                recurso.getId()
                        ).setFont(font)
                );

                tabla.addCell(
                        new Paragraph(
                                recurso.getDescripcion()
                        ).setFont(font)
                );

                String categoria = "";

                if (recurso.getCategoria() != null) {
                    categoria =
                            recurso.getCategoria()
                                    .getDescripcion();
                }

                tabla.addCell(
                        new Paragraph(
                                categoria
                        ).setFont(font)
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

        cell.setTextAlignment(
                alignment
        );

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