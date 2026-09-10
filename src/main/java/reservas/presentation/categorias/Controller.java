package reservas.presentation.categorias;

import reservas.logic.Categoria;
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
        limpiar();
    }

    public void buscar() {

        String descripcion =
                view.getDescripcionBusqueda();

        List<Categoria> resultado =
                Service.instance()
                        .buscarCategorias(descripcion);

        model.setCategorias(resultado);
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
                    new Categoria();

            categoria.setDescripcion(
                    view.getDescripcionCategoria()
            );

            if (editando) {

                categoria.setId(
                        model.getCurrent().getId()
                );

                Service.instance()
                        .modificarCategoria(categoria);

                view.mostrarMensaje(
                        "Categoría modificada correctamente"
                );

            } else {

                Service.instance()
                        .crearCategoria(categoria);

                view.mostrarMensaje(
                        "Categoría creada correctamente"
                );
            }

            cargarCategorias();
            limpiar();

        } catch (Exception ex) {

            view.mostrarError(
                    ex.getMessage()
            );
        }
    }

    public void borrar() {

        try {

            Categoria actual =
                    model.getCurrent();

            if (actual == null
                    || actual.getId() == null
                    || actual.getId()
                    .trim()
                    .isEmpty()) {

                throw new Exception(
                        "Debe seleccionar una categoría"
                );
            }

            Service.instance()
                    .eliminarCategoria(
                            actual.getId()
                    );

            view.mostrarMensaje(
                    "Categoría eliminada correctamente"
            );

            cargarCategorias();
            limpiar();

        } catch (Exception ex) {

            view.mostrarError(
                    ex.getMessage()
            );
        }
    }

    public void limpiar() {

        model.setCurrent(
                new Categoria()
        );

        view.limpiarSeleccionTabla();
    }

    public void seleccionar(int fila) {

        if (fila < 0
                || fila >= model
                .getCategorias()
                .size()) {

            return;
        }

        Categoria categoria =
                model.getCategorias()
                        .get(fila);

        model.setCurrent(categoria);
    }

    private void cargarCategorias() {

        model.setCategorias(
                Service.instance()
                        .listarCategorias()
        );
    }

    public void imprimir() {

        try {

            List<Categoria> categorias =
                    model.getCategorias();

            String path = "categorias.pdf";

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
                    new Paragraph("Reporte de Categorías")
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
                            new float[]{2, 5}
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

            for (Categoria categoria : categorias) {

                tabla.addCell(
                        new Paragraph(
                                categoria.getId()
                        ).setFont(font)
                );

                tabla.addCell(
                        new Paragraph(
                                categoria.getDescripcion()
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