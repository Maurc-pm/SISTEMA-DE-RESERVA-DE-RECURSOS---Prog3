package reservas.presentation.calendarizacion;

import com.github.lgooddatepicker.components.DatePicker;
import reservas.logic.Categoria;
import reservas.logic.Celda;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.time.LocalDate;

public class View implements PropertyChangeListener {

    private static final int HORAS_DIA = 24;
    private static final int ALTURA_FILA_MINIMA = 45;
    private static final int ANCHO_CELDA_HTML = 220;

    private JPanel panel;

    // Filtros
    private JPanel filtrosPanel;
    private DatePicker fechaPicker;
    private JComboBox<Categoria> categoriaCbx;
    private JButton cargarBtn;
    private JButton imprimirBtn;

    // Listado
    private JPanel listadoPanel;
    private JTable calendarioTable;

    private Model model;

    public View() {
        fechaPicker.setDate(LocalDate.now());
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
            actualizarTabla();
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {

        switch (evt.getPropertyName()) {

            case Model.CATEGORIAS:
                actualizarCategorias();
                break;

            case Model.MATRIZ:
                actualizarTabla();
                break;
        }
    }

    // =========================================================
    // ACTUALIZAR COMBO DE CATEGORÍAS
    // =========================================================

    private void actualizarCategorias() {

        if (model == null) {
            return;
        }

        Categoria seleccionActual =
                (Categoria) categoriaCbx.getSelectedItem();

        categoriaCbx.removeAllItems();

        for (Categoria categoria : model.getCategorias()) {
            categoriaCbx.addItem(categoria);
        }

        if (seleccionActual != null
                && model.getCategorias().contains(seleccionActual)) {

            categoriaCbx.setSelectedItem(seleccionActual);

        } else if (categoriaCbx.getItemCount() > 0) {

            categoriaCbx.setSelectedIndex(-1);
        }
    }

    // =========================================================
    // ACTUALIZAR TABLA (MATRIZ HORA x RECURSO)
    // =========================================================

    private void actualizarTabla() {

        if (model == null) {
            return;
        }

        int columnas = model.getRecursos().size();

        String[] encabezados = new String[columnas + 1];
        encabezados[0] = "Hora";

        for (int i = 0; i < columnas; i++) {
            encabezados[i + 1] =
                    model.getRecursos().get(i).getDescripcion();
        }

        DefaultTableModel tableModel =
                new DefaultTableModel(encabezados, 0) {

                    @Override
                    public boolean isCellEditable(
                            int row,
                            int column
                    ) {
                        return false;
                    }
                };

        Celda[][] matriz = model.getMatriz();

        for (int hora = 0; hora < HORAS_DIA; hora++) {

            Object[] fila = new Object[columnas + 1];
            fila[0] = String.format("%02d:00", hora);

            for (int columna = 0; columna < columnas; columna++) {
                fila[columna + 1] = matriz[hora][columna].getTexto();
            }

            tableModel.addRow(fila);
        }

        calendarioTable.setModel(tableModel);
        calendarioTable.setRowHeight(ALTURA_FILA_MINIMA);
        calendarioTable.setDefaultRenderer(
                Object.class,
                new CeldaRenderer(matriz)
        );

        calendarioTable.getColumnModel()
                .getColumn(0)
                .setPreferredWidth(60);

        for (int i = 1; i <= columnas; i++) {
            calendarioTable.getColumnModel()
                    .getColumn(i)
                    .setPreferredWidth(ANCHO_CELDA_HTML);
        }

        ajustarAlturaFilas(calendarioTable);
    }

    // =========================================================
    // AJUSTAR ALTO DE FILAS SEGÚN EL CONTENIDO
    // =========================================================

    private void ajustarAlturaFilas(JTable tabla) {

        for (int fila = 0; fila < tabla.getRowCount(); fila++) {

            int altura = ALTURA_FILA_MINIMA;

            for (int columna = 0; columna < tabla.getColumnCount(); columna++) {

                Component renderizado =
                        tabla.prepareRenderer(
                                tabla.getCellRenderer(fila, columna),
                                fila,
                                columna
                        );

                altura = Math.max(
                        altura,
                        renderizado.getPreferredSize().height + 10
                );
            }

            tabla.setRowHeight(fila, altura);
        }
    }

    // =========================================================
    // CONTROLLER Y LISTENERS
    // =========================================================

    public void setController(Controller controller) {

        cargarBtn.addActionListener(
                e -> controller.cargar()
        );

        imprimirBtn.addActionListener(
                e -> controller.imprimir()
        );
    }

    // =========================================================
    // FILTROS
    // =========================================================

    public LocalDate getFecha() {
        return fechaPicker.getDate();
    }

    public Categoria getCategoriaSeleccionada() {
        return (Categoria) categoriaCbx.getSelectedItem();
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
    // RENDERER: PINTA DE AMARILLO LAS CELDAS OCUPADAS
    // =========================================================

    private static class CeldaRenderer extends DefaultTableCellRenderer {

        private static final Color OCUPADA = new Color(255, 255, 204);

        private final Celda[][] matriz;

        CeldaRenderer(Celda[][] matriz) {
            this.matriz = matriz;
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column
        ) {

            Object valorMostrado = value;

            if (column > 0
                    && value instanceof String
                    && !((String) value).isEmpty()) {

                valorMostrado =
                        "<html><body style='width:"
                                + ANCHO_CELDA_HTML
                                + "px'>"
                                + value
                                + "</body></html>";
            }

            Component componente =
                    super.getTableCellRendererComponent(
                            table, valorMostrado, isSelected,
                            hasFocus, row, column
                    );

            if (column == 0) {
                componente.setBackground(Color.WHITE);
                setHorizontalAlignment(SwingConstants.CENTER);
                return componente;
            }

            boolean ocupada =
                    row < matriz.length
                            && (column - 1) < matriz[row].length
                            && matriz[row][column - 1].isOcupada();

            componente.setBackground(
                    ocupada ? OCUPADA : Color.WHITE
            );

            setHorizontalAlignment(SwingConstants.LEFT);

            return componente;
        }
    }
}
