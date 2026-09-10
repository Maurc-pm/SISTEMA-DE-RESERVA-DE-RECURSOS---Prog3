package reservas.presentation.funcionarios;

import reservas.logic.Funcionario;
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

        cargarFuncionarios();
        limpiar();
    }

    public void buscar() {

        String id = view.getIdBusqueda();
        String nombre = view.getNombreBusqueda();

        List<Funcionario> resultado =
                Service.instance()
                        .buscarFuncionarios(id, nombre);

        model.setFuncionarios(resultado);
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

            Funcionario funcionario =
                    new Funcionario();

            if (editando) {

                funcionario.setId(
                        model.getCurrent().getId()
                );

            } else {

                funcionario.setId(
                        view.getIdFuncionario()
                );
            }

            funcionario.setNombre(
                    view.getNombreFuncionario()
            );

            funcionario.setTelefono(
                    view.getTelefonoFuncionario()
            );

            if (editando) {

                Service.instance()
                        .modificarFuncionario(funcionario);

                view.mostrarMensaje(
                        "Funcionario modificado correctamente"
                );

            } else {

                Service.instance()
                        .crearFuncionario(funcionario);

                view.mostrarMensaje(
                        "Funcionario creado correctamente"
                );
            }

            cargarFuncionarios();
            limpiar();

        } catch (Exception ex) {

            view.mostrarError(
                    ex.getMessage()
            );
        }
    }

    public void borrar() {

        try {

            Funcionario actual =
                    model.getCurrent();

            if (actual == null
                    || actual.getId() == null
                    || actual.getId()
                    .trim()
                    .isEmpty()) {

                throw new Exception(
                        "Debe seleccionar un funcionario"
                );
            }

            Service.instance()
                    .eliminarFuncionario(
                            actual.getId()
                    );

            view.mostrarMensaje(
                    "Funcionario eliminado correctamente"
            );

            cargarFuncionarios();
            limpiar();

        } catch (Exception ex) {

            view.mostrarError(
                    ex.getMessage()
            );
        }
    }

    public void limpiar() {

        model.setCurrent(
                new Funcionario()
        );

        view.limpiarSeleccionTabla();
    }

    public void seleccionar(int fila) {

        if (fila < 0
                || fila >= model
                .getFuncionarios()
                .size()) {

            return;
        }

        Funcionario funcionario =
                model.getFuncionarios()
                        .get(fila);

        model.setCurrent(funcionario);
    }

    private void cargarFuncionarios() {

        model.setFuncionarios(
                Service.instance()
                        .listarFuncionarios()
        );
    }

    public void imprimir() {

        try {

            List<Funcionario> funcionarios =
                    model.getFuncionarios();

            String path = "funcionarios.pdf";

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
                    new Paragraph("Reporte de Funcionarios")
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
                            new float[]{2, 4, 3}
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
                            new Paragraph("Nombre")
                                    .setFont(bold),
                            TextAlignment.CENTER,
                            true
                    )
            );

            tabla.addHeaderCell(
                    getCell(
                            new Paragraph("Teléfono")
                                    .setFont(bold),
                            TextAlignment.CENTER,
                            true
                    )
            );

            for (Funcionario funcionario : funcionarios) {

                tabla.addCell(
                        new Paragraph(
                                funcionario.getId()
                        ).setFont(font)
                );

                tabla.addCell(
                        new Paragraph(
                                funcionario.getNombre()
                        ).setFont(font)
                );

                tabla.addCell(
                        new Paragraph(
                                funcionario.getTelefono()
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