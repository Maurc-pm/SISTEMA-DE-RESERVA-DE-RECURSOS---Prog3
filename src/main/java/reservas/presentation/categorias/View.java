package reservas.presentation.categorias;

import javax.swing.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class View implements PropertyChangeListener {

    private JPanel panel;

    // Búsqueda
    private JPanel busquedaPanel;
    private JTextField descripcionBusquedaFld;
    private JButton buscarBtn;

    // Categoría
    private JPanel categoriaPanel;
    private JTextField idFld;
    private JTextField descripcionFld;
    private JButton guardarBtn;
    private JButton borrarBtn;
    private JButton limpiarBtn;

    // Listado
    private JPanel listadoPanel;
    private JTable categoriasTable;

    private Model model;

    public View() {

        // Solo se puede seleccionar una categoría a la vez.
        categoriasTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        // El ID de categoría lo genera el sistema.
        idFld.setEditable(false);
    }

    public JPanel getPanel() {
        return panel;
    }

    // =========================================================
    // MODEL
    // =========================================================

    public void setModel(Model model) {

        if (this.model != null) {
            this.model.removePropertyChangeListener(this);
        }

        this.model = model;

        if (this.model != null) {

            this.model.addPropertyChangeListener(this);

            actualizarCategoria();
            actualizarTabla();
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {

        switch (evt.getPropertyName()) {

            case Model.CURRENT:
                actualizarCategoria();
                break;

            case Model.CATEGORIAS:
                actualizarTabla();
                break;
        }
    }

    // =========================================================
    // ACTUALIZAR FORMULARIO
    // =========================================================

    private void actualizarCategoria() {

        if (model == null || model.getCurrent() == null) {
            return;
        }

        idFld.setText(
                model.getCurrent().getId()
        );

        descripcionFld.setText(
                model.getCurrent().getDescripcion()
        );
    }

    // =========================================================
    // ACTUALIZAR TABLA
    // =========================================================

    private void actualizarTabla() {

        if (model == null) {
            return;
        }

        int[] columns = {
                TableModel.ID,
                TableModel.DESCRIPCION
        };

        categoriasTable.setModel(
                new TableModel(
                        columns,
                        model.getCategorias()
                )
        );
    }
    // =========================================================
// CONTROLLER Y LISTENERS
// =========================================================

    public void setController(Controller controller) {

        buscarBtn.addActionListener(
                e -> controller.buscar()
        );

        guardarBtn.addActionListener(
                e -> controller.guardar()
        );

        borrarBtn.addActionListener(
                e -> controller.borrar()
        );

        limpiarBtn.addActionListener(
                e -> controller.limpiar()
        );

        categoriasTable
                .getSelectionModel()
                .addListSelectionListener(e -> {

                    if (!e.getValueIsAdjusting()) {

                        int fila =
                                categoriasTable.getSelectedRow();

                        controller.seleccionar(fila);
                    }
                });
    }


// =========================================================
// CAMPOS DE BÚSQUEDA
// =========================================================

    public String getDescripcionBusqueda() {
        return descripcionBusquedaFld.getText().trim();
    }


// =========================================================
// CAMPOS DE CATEGORÍA
// =========================================================

    public String getDescripcionCategoria() {
        return descripcionFld.getText().trim();
    }


// =========================================================
// MENSAJES
// =========================================================

    public void mostrarMensaje(String mensaje) {

        JOptionPane.showMessageDialog(
                panel,
                mensaje,
                "Sistema de Reservas",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    public void mostrarError(String mensaje) {

        JOptionPane.showMessageDialog(
                panel,
                mensaje,
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }


// =========================================================
// TABLA
// =========================================================

    public void limpiarSeleccionTabla() {
        categoriasTable.clearSelection();
    }
}