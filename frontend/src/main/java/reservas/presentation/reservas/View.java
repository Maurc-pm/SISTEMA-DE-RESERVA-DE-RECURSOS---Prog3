package reservas.presentation.reservas;

import reservas.presentation.Icons;

import javax.swing.*;

import reservas.logic.Categoria;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import com.github.lgooddatepicker.components.DatePicker;
import java.time.LocalDate;

public class View implements PropertyChangeListener {
    private JLabel titulo;
    private JLabel actividad;
    private JTextField horaInicioFld;
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
    private JTextField iaFld;
    private JButton interpretarIAButton;
    private DatePicker fechaPicker;
    private JButton imprimirButton;
    private Model model;

    public View() {

        // Fuente del título en código (no en el .form) para que el proyecto
        // compile igual con cualquier opción del GUI Designer de IntelliJ
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 18f));

        reservasTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        fechaPicker.setDate(LocalDate.now());

        Icons.set(interpretarIAButton, "ai.png");
        Icons.set(agregarButton, "ok.png");
        Icons.set(reservarButton, "reservas.png");
        Icons.set(limpiarButton, "clear.png");
        Icons.set(cancelarButton, "cancel.png");
        Icons.set(imprimirButton, "pdf.png");
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
        fechaPicker.clear();
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
        interpretarIAButton.addActionListener(e -> controller.interpretarConIA());
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
        imprimirButton.addActionListener(e -> controller.imprimir());
    }

    public String getActividad() {
        return actividadFld.getText().trim();
    }

    public LocalDate getFecha() {
        return fechaPicker.getDate();
    }

    public String getHoraInicio() {
        return horaInicioFld.getText().trim();
    }

    public String getHoraFinal() {
        return horaFinalFld.getText().trim();
    }

    public String getTextoIA() {
        return iaFld.getText().trim();
    }

    public void llenarFormularioIA(
            String actividad,
            LocalDate fecha,
            String horaInicio,
            String horaFinal
    ) {
        actividadFld.setText(actividad != null ? actividad : "");

        if (fecha != null) {
            fechaPicker.setDate(fecha);
        } else {
            fechaPicker.clear();
        }

        horaInicioFld.setText(horaInicio != null ? horaInicio : "");
        horaFinalFld.setText(horaFinal != null ? horaFinal : "");
    }

    public void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(panel, mensaje, "Sistema de Reservas", JOptionPane.INFORMATION_MESSAGE);
    }

    public void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(panel, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        panel = new JPanel();
        panel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(13, 5, new Insets(10, 10, 10, 10), -1, -1));
        titulo = new JLabel();
        titulo.setText("Mis reservas");
        panel.add(titulo, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 5, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        actividad = new JLabel();
        actividad.setText("Actividad:");
        panel.add(actividad, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        fecha = new JLabel();
        fecha.setText("Fecha: ");
        panel.add(fecha, new com.intellij.uiDesigner.core.GridConstraints(3, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        horaInicio = new JLabel();
        horaInicio.setText("Hora inicio (HH:mm):");
        panel.add(horaInicio, new com.intellij.uiDesigner.core.GridConstraints(4, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        categoria = new JLabel();
        categoria.setText("Categoría:");
        panel.add(categoria, new com.intellij.uiDesigner.core.GridConstraints(5, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        listasCategorias = new JLabel();
        listasCategorias.setText("Listas de categorías:");
        panel.add(listasCategorias, new com.intellij.uiDesigner.core.GridConstraints(6, 0, 1, 5, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        categoriasList = new JList();
        panel.add(categoriasList, new com.intellij.uiDesigner.core.GridConstraints(7, 0, 1, 5, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150, 50), null, 0, false));
        misReservas = new JLabel();
        misReservas.setText("Mis reservas:");
        panel.add(misReservas, new com.intellij.uiDesigner.core.GridConstraints(9, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        reservasTable = new JTable();
        panel.add(reservasTable, new com.intellij.uiDesigner.core.GridConstraints(10, 0, 1, 5, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150, 50), null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Reserva con IA:");
        panel.add(label1, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        iaFld = new JTextField();
        panel.add(iaFld, new com.intellij.uiDesigner.core.GridConstraints(1, 1, 1, 3, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        interpretarIAButton = new JButton();
        interpretarIAButton.setText("Interpretar con IA");
        panel.add(interpretarIAButton, new com.intellij.uiDesigner.core.GridConstraints(1, 4, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        actividadFld = new JTextField();
        panel.add(actividadFld, new com.intellij.uiDesigner.core.GridConstraints(2, 1, 1, 3, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        categoriaComboBox = new JComboBox();
        panel.add(categoriaComboBox, new com.intellij.uiDesigner.core.GridConstraints(5, 1, 1, 3, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        agregarButton = new JButton();
        agregarButton.setText("Agregar");
        panel.add(agregarButton, new com.intellij.uiDesigner.core.GridConstraints(5, 4, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        horaInicioFld = new JTextField();
        panel.add(horaInicioFld, new com.intellij.uiDesigner.core.GridConstraints(4, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        horaFinal = new JLabel();
        horaFinal.setText("Hora final: (HH:mm)");
        panel.add(horaFinal, new com.intellij.uiDesigner.core.GridConstraints(4, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        horaFinalFld = new JTextField();
        panel.add(horaFinalFld, new com.intellij.uiDesigner.core.GridConstraints(4, 3, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        fechaPicker = new DatePicker();
        panel.add(fechaPicker, new com.intellij.uiDesigner.core.GridConstraints(3, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        cancelarButton = new JButton();
        cancelarButton.setText("Cancelar");
        panel.add(cancelarButton, new com.intellij.uiDesigner.core.GridConstraints(11, 4, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        limpiarButton = new JButton();
        limpiarButton.setText("Limpiar");
        panel.add(limpiarButton, new com.intellij.uiDesigner.core.GridConstraints(8, 4, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        reservarButton = new JButton();
        reservarButton.setText("Reservar");
        panel.add(reservarButton, new com.intellij.uiDesigner.core.GridConstraints(8, 3, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        imprimirButton = new JButton();
        imprimirButton.setText("Imprimir");
        panel.add(imprimirButton, new com.intellij.uiDesigner.core.GridConstraints(12, 4, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel;
    }
}
