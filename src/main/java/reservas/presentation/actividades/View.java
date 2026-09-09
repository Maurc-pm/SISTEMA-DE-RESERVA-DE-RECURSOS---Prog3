package reservas.presentation.actividades;

import com.github.lgooddatepicker.components.DatePicker;
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

    private static final String[] DIAS_ABREV = {
            "lun", "mar", "mié", "jue", "vie", "sáb", "dom"
    };

    private static final int ALTURA_FILA_MINIMA = 45;
    private static final int ANCHO_CELDA_HTML = 190;

    private JPanel panel;

    // Semana
    private JPanel semanaPanel;
    private DatePicker fechaReferenciaPicker;
    private JButton cargarBtn;
    private JButton imprimirBtn;

    // Listado
    private JPanel listadoPanel;
    private JTable actividadesTable;

    private Model model;

    public View() {
        fechaReferenciaPicker.setDate(LocalDate.now());
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

            actualizarTabla();
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {

        if (Model.MATRIZ.equals(evt.getPropertyName())) {
            actualizarTabla();
        }
    }

    // =========================================================
    // ACTUALIZAR TABLA (MATRIZ HORA x DÍA)
    // =========================================================

    private void actualizarTabla() {

        if (model == null) {
            return;
        }

        java.util.List<LocalDate> dias = model.getDias();

        String[] encabezados = new String[dias.size() + 1];
        encabezados[0] = "Hora";

        for (int i = 0; i < dias.size(); i++) {

            encabezados[i + 1] =
                    DIAS_ABREV[i] + " " + dias.get(i);
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

            Object[] fila = new Object[dias.size() + 1];
            fila[0] = String.format("%02d:00", hora);

            for (int dia = 0; dia < dias.size(); dia++) {
                fila[dia + 1] = matriz[hora][dia].getTexto();
            }

            tableModel.addRow(fila);
        }

        actividadesTable.setModel(tableModel);
        actividadesTable.setRowHeight(ALTURA_FILA_MINIMA);
        actividadesTable.setDefaultRenderer(
                Object.class,
                new CeldaRenderer(matriz)
        );

        /*
         * Por defecto JTable achica las columnas para que todas
         * quepan en el ancho visible (AUTO_RESIZE_ALL_COLUMNS),
         * lo que ignoraba el ancho que le poníamos a cada una y
         * cortaba el texto. Lo apagamos: cada columna respeta el
         * ancho fijado y, si no caben todas, aparece scroll
         * horizontal en vez de aplastar el contenido.
         */
        actividadesTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        actividadesTable.getColumnModel()
                .getColumn(0)
                .setPreferredWidth(65);

        for (int i = 1; i <= dias.size(); i++) {
            actividadesTable.getColumnModel()
                    .getColumn(i)
                    .setPreferredWidth(ANCHO_CELDA_HTML);
        }

        ajustarAlturaFilas(actividadesTable);
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

    public LocalDate getFechaReferencia() {
        return fechaReferenciaPicker.getDate();
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

                String contenido =
                        ((String) value).replace(" | ", "<br>");

                valorMostrado =
                        "<html><body style='width:"
                                + ANCHO_CELDA_HTML
                                + "px'>"
                                + contenido
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