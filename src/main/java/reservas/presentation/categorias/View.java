package reservas.presentation.categorias;

import javax.swing.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class View implements PropertyChangeListener {

    private JPanel panel;

    private JPanel busquedaPanel;
    private JTextField descripcionBusquedaFld;
    private JButton buscarBtn;

    private JPanel categoriaPanel;
    private JTextField idFld;
    private JTextField descripcionFld;
    private JButton guardarBtn;
    private JButton borrarBtn;
    private JButton limpiarBtn;
    private JButton imprimirBtn;

    private JPanel listadoPanel;
    private JTable categoriasTable;

    private Model model;

    public View() {

        categoriasTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        idFld.setEditable(false);
    }

    public JPanel getPanel() {
        return panel;
    }

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

        imprimirBtn.addActionListener(
                e -> controller.imprimir()
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

    public String getDescripcionBusqueda() {
        return descripcionBusquedaFld.getText().trim();
    }

    public String getDescripcionCategoria() {
        return descripcionFld.getText().trim();
    }

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

    public void limpiarSeleccionTabla() {
        categoriasTable.clearSelection();
    }
}