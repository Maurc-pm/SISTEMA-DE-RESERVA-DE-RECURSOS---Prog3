package reservas.presentation.recursos;

import reservas.logic.Categoria;

import javax.swing.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class View implements PropertyChangeListener {

    private JPanel panel;

    // Filtro
    private JPanel filtroPanel;
    private JComboBox<Categoria> categoriaFiltroCbx;
    private JButton buscarBtn;

    // Recurso
    private JPanel recursoPanel;
    private JTextField idFld;
    private JComboBox<Categoria> categoriaCbx;
    private JTextField descripcionFld;
    private JButton guardarBtn;
    private JButton borrarBtn;
    private JButton limpiarBtn;

    // Listado
    private JPanel listadoPanel;
    private JTable recursosTable;

    private Model model;

    public View() {

        recursosTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );
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

            actualizarCategorias();
            actualizarRecurso();
            actualizarTabla();
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {

        switch (evt.getPropertyName()) {

            case Model.CURRENT:
                actualizarRecurso();
                break;

            case Model.RECURSOS:
                actualizarTabla();
                break;

            case Model.CATEGORIAS:
                actualizarCategorias();
                actualizarRecurso();
                break;
        }
    }

    // =========================================================
    // ACTUALIZAR RECURSO ACTUAL
    // =========================================================

    private void actualizarRecurso() {

        if (model == null || model.getCurrent() == null) {
            return;
        }

        idFld.setText(
                model.getCurrent().getId()
        );

        descripcionFld.setText(
                model.getCurrent().getDescripcion()
        );

        Categoria categoria =
                model.getCurrent().getCategoria();

        if (categoria != null
                && categoria.getId() != null
                && !categoria.getId().trim().isEmpty()) {

            categoriaCbx.setSelectedItem(categoria);

        } else {

            categoriaCbx.setSelectedIndex(-1);
        }
    }

    // =========================================================
    // ACTUALIZAR CATEGORÍAS DE LOS COMBOBOX
    // =========================================================

    private void actualizarCategorias() {

        if (model == null) {
            return;
        }

        categoriaFiltroCbx.removeAllItems();
        categoriaCbx.removeAllItems();

        /*
         * Opción especial para mostrar todos los recursos.
         * No es una categoría real del sistema.
         */
        categoriaFiltroCbx.addItem(
                new Categoria("", "Todas")
        );

        for (Categoria categoria : model.getCategorias()) {

            categoriaFiltroCbx.addItem(categoria);
            categoriaCbx.addItem(categoria);
        }

        categoriaFiltroCbx.setSelectedIndex(0);

        if (categoriaCbx.getItemCount() > 0) {
            categoriaCbx.setSelectedIndex(-1);
        }
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
                TableModel.CATEGORIA,
                TableModel.DESCRIPCION
        };

        recursosTable.setModel(
                new TableModel(
                        columns,
                        model.getRecursos()
                )
        );
    }
    // =========================================================
// CONTROLLER Y LISTENERS
// =========================================================

    public void setController(Controller controller) {

        // Botón Buscar
        buscarBtn.addActionListener(
                e -> controller.buscar()
        );

        // Botón Guardar
        guardarBtn.addActionListener(
                e -> controller.guardar()
        );

        // Botón Borrar
        borrarBtn.addActionListener(
                e -> controller.borrar()
        );

        // Botón Limpiar
        limpiarBtn.addActionListener(
                e -> controller.limpiar()
        );

        // Selección de una fila de la tabla
        recursosTable
                .getSelectionModel()
                .addListSelectionListener(e -> {

                    if (!e.getValueIsAdjusting()) {

                        int fila =
                                recursosTable.getSelectedRow();

                        controller.seleccionar(fila);
                    }
                });
    }


// =========================================================
// FILTRO
// =========================================================

    public Categoria getCategoriaFiltro() {

        return (Categoria)
                categoriaFiltroCbx.getSelectedItem();
    }


// =========================================================
// DATOS DEL RECURSO
// =========================================================

    public String getIdRecurso() {
        return idFld.getText().trim();
    }

    public Categoria getCategoriaSeleccionada() {

        return (Categoria)
                categoriaCbx.getSelectedItem();
    }

    public String getDescripcionRecurso() {
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
        recursosTable.clearSelection();
    }
}