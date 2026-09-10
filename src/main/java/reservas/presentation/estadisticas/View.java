package reservas.presentation.estadisticas;

import com.github.lgooddatepicker.components.DatePicker;

import javax.swing.table.DefaultTableModel;
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
    }
    public JPanel getPanel() {
        return panel;
    }
    public java.time.LocalDate getDesdeRecursos() {
        return desdeRecursosPicker.getDate();
    }

    public java.time.LocalDate getHastaRecursos() {
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

}