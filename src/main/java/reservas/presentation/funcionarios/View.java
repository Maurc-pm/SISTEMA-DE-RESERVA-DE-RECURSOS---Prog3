package reservas.presentation.funcionarios;

import javax.swing.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class View implements PropertyChangeListener {

    private JPanel panel;

    private JPanel busquedaPanel;
    private JTextField idBusquedaFld;
    private JTextField nombreBusquedaFld;
    private JButton buscarBtn;

    private JPanel funcionarioPanel;
    private JTextField idFld;
    private JTextField nombreFld;
    private JTextField telefonoFld;
    private JButton guardarBtn;
    private JButton borrarBtn;
    private JButton limpiarBtn;

    private JPanel listadoPanel;
    private JTable funcionariosTable;

    private Model model;

    public View() {

        funcionariosTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );
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

            actualizarFuncionario();
            actualizarTabla();
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {

        switch (evt.getPropertyName()) {

            case Model.CURRENT:
                actualizarFuncionario();
                break;

            case Model.FUNCIONARIOS:
                actualizarTabla();
                break;
        }
    }

    private void actualizarFuncionario() {

        if (model == null || model.getCurrent() == null) {
            return;
        }

        idFld.setText(
                model.getCurrent().getId()
        );

        nombreFld.setText(
                model.getCurrent().getNombre()
        );

        telefonoFld.setText(
                model.getCurrent().getTelefono()
        );
    }

    private void actualizarTabla() {

        if (model == null) {
            return;
        }

        int[] columns = {
                TableModel.ID,
                TableModel.NOMBRE,
                TableModel.TELEFONO
        };

        funcionariosTable.setModel(
                new TableModel(
                        columns,
                        model.getFuncionarios()
                )
        );
    }
}