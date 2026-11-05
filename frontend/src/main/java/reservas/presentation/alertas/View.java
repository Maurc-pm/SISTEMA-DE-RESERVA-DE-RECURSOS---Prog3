package reservas.presentation.alertas;

import reservas.logic.Recurso;
import reservas.logic.Reserva;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/** Vista del área de alertas, siempre visible a la derecha de la ventana. */
public class View implements PropertyChangeListener {

    private final JPanel panel;
    private final JTable tabla;
    private Controller controller;
    private Model model;

    public View() {
        panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Alertas"));
        panel.setPreferredSize(new Dimension(300, 0));

        tabla = new JTable();
        tabla.setRowHeight(28);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.getTableHeader().setReorderingAllowed(false);
        tabla.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int fila = tabla.rowAtPoint(e.getPoint());
                int columna = tabla.columnAtPoint(e.getPoint());
                if (controller == null || fila < 0 || columna < 0) {
                    return;
                }
                if (columna == TableModel.VER) {
                    controller.ver(fila);
                } else if (columna == TableModel.DESCARTAR) {
                    controller.descartar(fila);
                }
            }
        });

        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);
    }

    public JPanel getPanel() {
        return panel;
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public void setModel(Model model) {
        this.model = model;
        model.addPropertyChangeListener(this);
        refrescar();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (Model.ALERTAS.equals(evt.getPropertyName())) {
            refrescar();
        }
    }

    private void refrescar() {
        tabla.setModel(new TableModel(model.getAlertas()));
        tabla.getColumnModel().getColumn(TableModel.ACTIVIDAD).setPreferredWidth(200);
        tabla.getColumnModel().getColumn(TableModel.VER).setPreferredWidth(40);
        tabla.getColumnModel().getColumn(TableModel.DESCARTAR).setPreferredWidth(45);
    }

    public void mostrarDetalle(Reserva reserva) {
        JPanel datos = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.NORTHWEST;
        c.insets = new Insets(3, 3, 3, 10);

        String[][] filas = {
                {"Encargado:", TableModel.encargado(reserva)},
                {"Fecha:", String.valueOf(reserva.getFecha())},
                {"Desde:", String.valueOf(reserva.getHoraInicio())},
                {"Hasta:", String.valueOf(reserva.getHoraFin())}
        };
        for (int i = 0; i < filas.length; i++) {
            c.gridy = i;
            c.gridx = 0;
            datos.add(new JLabel(filas[i][0]), c);
            c.gridx = 1;
            datos.add(new JLabel(filas[i][1]), c);
        }

        DefaultListModel<String> recursos = new DefaultListModel<>();
        if (reserva.getRecursos() != null) {
            for (Recurso recurso : reserva.getRecursos()) {
                recursos.addElement(recurso.getDescripcion());
            }
        }
        JList<String> lista = new JList<>(recursos);
        lista.setEnabled(false);
        JScrollPane scroll = new JScrollPane(lista);
        scroll.setPreferredSize(new Dimension(160, 90));

        c.gridy = filas.length;
        c.gridx = 0;
        c.anchor = GridBagConstraints.WEST;
        datos.add(new JLabel("Recursos"), c);
        c.gridx = 1;
        datos.add(scroll, c);

        JOptionPane.showMessageDialog(panel, datos, reserva.getActividad(), JOptionPane.PLAIN_MESSAGE);
    }

    public boolean confirmarDescarte(Reserva reserva) {
        return JOptionPane.showConfirmDialog(
                panel,
                reserva.getActividad(),
                "Descartar?",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        ) == JOptionPane.YES_OPTION;
    }
}
