package reservas.presentation.reservas;

import javax.swing.*;
import reservas.logic.Categoria;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class View implements PropertyChangeListener {
    private JLabel titulo;
    private JLabel actividad;
    private JTextField horaInicioFld;
    private JTextField fechaFld;
    private JTextField actividadFld;
    private JTextField horaFinalFld;
    private JComboBox categoriaComboBox;
    private JButton agregarButton;
    private JLabel categoria;
    private JLabel fecha;
    private JLabel horaInicio;
    private JLabel horaFinal;
    private JLabel listasCategorias;
    private JList categoriasList;
    private JButton reservarButton;
    private JButton limpiarButton;
    private JButton cancelarButton;
    private JLabel misReservas;
    private JTable reservasTable;
    private JPanel panel;
    private Model model;

    public View() {
        reservasTable.setSelectionMode(
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

            actualizarTabla();
            actualizarCategorias();
            actualizarCategoriasSeleccionadas();
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {

        switch (evt.getPropertyName()) {

            case Model.RESERVAS:
                actualizarTabla();
                break;

            case Model.CATEGORIAS:
                actualizarCategorias();
                break;

            case Model.CATEGORIAS_SELECCIONADAS:
                actualizarCategoriasSeleccionadas();
                break;

            case Model.CURRENT:
                break;
        }
    }

    private void actualizarCategorias() {

        categoriaComboBox.removeAllItems();

        for (Categoria categoria : model.getCategorias()) {
            categoriaComboBox.addItem(categoria);
        }

        if (categoriaComboBox.getItemCount() > 0) {
            categoriaComboBox.setSelectedIndex(0);
        }
    }

    private void actualizarCategoriasSeleccionadas() {

        DefaultListModel<Categoria> listModel =
                new DefaultListModel<>();

        for (Categoria categoria :
                model.getCategoriasSeleccionadas()) {

            listModel.addElement(categoria);
        }

        categoriasList.setModel(listModel);
    }

    public Categoria getCategoriaSeleccionada() {

        return (Categoria) categoriaComboBox.getSelectedItem();
    }

    public void limpiarFormulario() {

        actividadFld.setText("");
        fechaFld.setText("");
        horaInicioFld.setText("");
        horaFinalFld.setText("");

        if (categoriaComboBox.getItemCount() > 0) {
            categoriaComboBox.setSelectedIndex(0);
        }
    }

    public void limpiarSeleccionTabla() {
        reservasTable.clearSelection();
    }

    private void actualizarTabla() {

        if (model == null) {
            return;
        }

        int[] columns = {
                TableModel.ID,
                TableModel.ACTIVIDAD,
                TableModel.FECHA,
                TableModel.INICIO,
                TableModel.FIN,
                TableModel.ESTADO
        };

        reservasTable.setModel(new TableModel(columns, model.getReservas()));
    }

    public void setController(Controller controller) {
        agregarButton.addActionListener(e -> controller.agregarCategoria());
        reservarButton.addActionListener(e -> controller.reservar());
        limpiarButton.addActionListener(e -> controller.limpiar());
        cancelarButton.addActionListener(e -> controller.cancelarReserva());
        reservasTable.getSelectionModel().addListSelectionListener(e -> {
                    if (!e.getValueIsAdjusting()) {
                        int fila = reservasTable.getSelectedRow();
                        controller.seleccionar(fila);
                    }
                });
    }

    public String getActividad() {
        return actividadFld.getText().trim();
    }

    public String getFecha() {
        return fechaFld.getText().trim();
    }

    public String getHoraInicio() {
        return horaInicioFld.getText().trim();
    }

    public String getHoraFinal() {
        return horaFinalFld.getText().trim();
    }

    public void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(panel, mensaje, "Sistema de Reservas", JOptionPane.INFORMATION_MESSAGE);
    }

    public void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(panel, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
