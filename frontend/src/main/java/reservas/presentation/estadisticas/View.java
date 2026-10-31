package reservas.presentation.estadisticas;

import reservas.presentation.Icons;

import com.github.lgooddatepicker.components.DatePicker;

import javax.swing.table.DefaultTableModel;
import java.time.LocalDate;
import java.util.Map;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;

public class View {

    private JPanel panel;
    private JPanel filtrosRecursosPanel;
    private DatePicker desdeRecursosPicker;
    private DatePicker hastaRecursosPicker;
    private JButton consultarRecursosBtn;
    private JPanel resultadosRecursosPanel;
    private JButton consultarActividadesBtn;
    private JButton imprimirRecursosButton;
    private JButton imprimirActividadesButton;

    private JTable recursosTable;
    private JPanel graficoRecursosPanel;

    public View() {

        resultadosRecursosPanel.setLayout(
                new GridLayout(2, 1, 0, 8)
        );

        recursosTable = new JTable();

        JScrollPane scrollRecursos =
                new JScrollPane(recursosTable);

        graficoRecursosPanel =
                new JPanel(new BorderLayout());

        resultadosRecursosPanel.add(
                scrollRecursos
        );

        resultadosRecursosPanel.add(
                graficoRecursosPanel
        );

        Icons.set(consultarRecursosBtn, "search.png");
        Icons.set(consultarActividadesBtn, "search.png");
        Icons.set(imprimirRecursosButton, "pdf.png");
        Icons.set(imprimirActividadesButton, "pdf.png");
    }

    public JPanel getPanel() {
        return panel;
    }

    public LocalDate getDesdeRecursos() {
        return desdeRecursosPicker.getDate();
    }

    public LocalDate getHastaRecursos() {
        return hastaRecursosPicker.getDate();
    }

    public void setController(Controller controller) {

        consultarRecursosBtn.addActionListener(
                e -> controller.consultarRecursos()
        );

        consultarActividadesBtn.addActionListener(
                e -> controller.consultarActividades()
        );

        imprimirRecursosButton.addActionListener(e ->
                controller.imprimirRecursos()
        );

        imprimirActividadesButton.addActionListener(e ->
                controller.imprimirActividades()
        );
    }

    public void mostrarRecursos(Map<String, Integer> datos) {

        DefaultTableModel tableModel = new DefaultTableModel(
                new Object[]{"Categoría", "Cantidad"},
                0
        );

        for (Map.Entry<String, Integer> entry : datos.entrySet()) {
            tableModel.addRow(
                    new Object[]{
                            entry.getKey(),
                            entry.getValue()
                    }
            );
        }

        recursosTable.setModel(tableModel);
    }

    public void mostrarGraficoRecursos(Map<String, Integer> datos) {

        DefaultCategoryDataset dataset =
                new DefaultCategoryDataset();

        for (Map.Entry<String, Integer> entry : datos.entrySet()) {
            dataset.addValue(
                    entry.getValue(),
                    "Cantidad",
                    entry.getKey()
            );
        }

        JFreeChart grafico = ChartFactory.createBarChart(
                "Recursos reservados por categoría",
                "Categoría",
                "Cantidad",
                dataset
        );

        ChartPanel chartPanel =
                new ChartPanel(grafico);

        graficoRecursosPanel.removeAll();
        graficoRecursosPanel.add(
                chartPanel,
                BorderLayout.CENTER
        );

        graficoRecursosPanel.revalidate();
        graficoRecursosPanel.repaint();
    }

    public void mostrarActividades(Map<String, Integer> datos) {

        DefaultTableModel tableModel = new DefaultTableModel(
                new Object[]{"Semana", "Cantidad"},
                0
        );

        for (Map.Entry<String, Integer> entry : datos.entrySet()) {
            tableModel.addRow(
                    new Object[]{
                            entry.getKey(),
                            entry.getValue()
                    }
            );
        }

        recursosTable.setModel(tableModel);
    }

    public void mostrarGraficoActividades(Map<String, Integer> datos) {

        boolean hayActividades = datos.values()
                .stream()
                .anyMatch(cantidad -> cantidad != null && cantidad > 0);

        if (!hayActividades) {

            graficoRecursosPanel.removeAll();
            graficoRecursosPanel.setLayout(new BorderLayout());

            JLabel mensaje = new JLabel(
                    "No hay actividades programadas en el período seleccionado",
                    SwingConstants.CENTER
            );

            graficoRecursosPanel.add(
                    mensaje,
                    BorderLayout.CENTER
            );

            graficoRecursosPanel.revalidate();
            graficoRecursosPanel.repaint();

            return;
        }

        DefaultCategoryDataset dataset =
                new DefaultCategoryDataset();

        for (Map.Entry<String, Integer> entry : datos.entrySet()) {
            dataset.addValue(
                    entry.getValue(),
                    "Cantidad",
                    entry.getKey()
            );
        }

        JFreeChart grafico = ChartFactory.createBarChart(
                "Actividades programadas por semana",
                "Semana",
                "Cantidad",
                dataset
        );

        ChartPanel chartPanel =
                new ChartPanel(grafico);

        graficoRecursosPanel.removeAll();

        graficoRecursosPanel.add(
                chartPanel,
                BorderLayout.CENTER
        );

        graficoRecursosPanel.revalidate();
        graficoRecursosPanel.repaint();
    }

    public JFreeChart crearGraficoRecursos(Map<String, Integer> datos) {

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (Map.Entry<String, Integer> entry : datos.entrySet()) {
            dataset.addValue(
                    entry.getValue(),
                    "Cantidad",
                    entry.getKey()
            );
        }

        return ChartFactory.createBarChart(
                "Recursos reservados por categoría",
                "Categoría",
                "Cantidad",
                dataset
        );
    }

    public JFreeChart crearGraficoActividades(Map<String, Integer> datos) {

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (Map.Entry<String, Integer> entry : datos.entrySet()) {
            dataset.addValue(
                    entry.getValue(),
                    "Cantidad",
                    entry.getKey()
            );
        }

        return ChartFactory.createBarChart(
                "Actividades programadas por semana",
                "Semana",
                "Cantidad",
                dataset
        );
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
        panel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(3, 3, new Insets(0, 0, 0, 0), -1, -1));
        final JLabel label1 = new JLabel();
        label1.setForeground(new Color(-2535132));
        label1.setText("Estadísticas");
        panel.add(label1, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        filtrosRecursosPanel = new JPanel();
        filtrosRecursosPanel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(4, 3, new Insets(0, 0, 0, 0), -1, -1));
        panel.add(filtrosRecursosPanel, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 3, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Desde:");
        filtrosRecursosPanel.add(label2, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        desdeRecursosPicker = new DatePicker();
        filtrosRecursosPanel.add(desdeRecursosPicker, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Hasta:");
        filtrosRecursosPanel.add(label3, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        hastaRecursosPicker = new DatePicker();
        filtrosRecursosPanel.add(hastaRecursosPicker, new com.intellij.uiDesigner.core.GridConstraints(1, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        consultarRecursosBtn = new JButton();
        consultarRecursosBtn.setText("Consultar recursos");
        filtrosRecursosPanel.add(consultarRecursosBtn, new com.intellij.uiDesigner.core.GridConstraints(1, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        consultarActividadesBtn = new JButton();
        consultarActividadesBtn.setText("Consultar actividades");
        filtrosRecursosPanel.add(consultarActividadesBtn, new com.intellij.uiDesigner.core.GridConstraints(0, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        imprimirActividadesButton = new JButton();
        imprimirActividadesButton.setText("Imprimir actividades");
        filtrosRecursosPanel.add(imprimirActividadesButton, new com.intellij.uiDesigner.core.GridConstraints(2, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        imprimirRecursosButton = new JButton();
        imprimirRecursosButton.setText("Imprimir recursos");
        filtrosRecursosPanel.add(imprimirRecursosButton, new com.intellij.uiDesigner.core.GridConstraints(3, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        resultadosRecursosPanel = new JPanel();
        resultadosRecursosPanel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel.add(resultadosRecursosPanel, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 3, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer1 = new com.intellij.uiDesigner.core.Spacer();
        resultadosRecursosPanel.add(spacer1, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 2, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel;
    }
}