
package reservas;

import reservas.logic.Sesion;
import reservas.logic.Reserva;
import reservas.presentation.Icons;
import reservas.presentation.Servicio;
import reservas.client.ServiceProxy;
import reservas.protocol.Protocolo;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

public class Application {

    public static void main(String[] args) {

        String host = args.length > 0 ? args[0] : "localhost";
        int puerto = args.length > 1
                ? Integer.parseInt(args[1])
                : Protocolo.PUERTO_POR_DEFECTO;

        SwingUtilities.invokeLater(() -> {

            try {

                ServiceProxy proxy;

                try {
                    proxy = new ServiceProxy(host, puerto);

                } catch (Exception e) {

                    JOptionPane.showMessageDialog(
                            null,
                            "No se pudo conectar al servidor en "
                                    + host + ":" + puerto,
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );

                    System.exit(1);
                    return;
                }

                Servicio.set(proxy);

                proxy.agregarListenerDesconexion(() ->
                        JOptionPane.showMessageDialog(
                                null,
                                "Se perdió la conexión con el servidor",
                                "Error",
                                JOptionPane.ERROR_MESSAGE
                        )
                );

                doLogin();

                if (Sesion.isLogged()) {
                    doRun();
                }

            } catch (Exception e) {

                JOptionPane.showMessageDialog(
                        null,
                        e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });
    }

    private static void doLogin() {

        reservas.presentation.login.View view =
                new reservas.presentation.login.View();

        reservas.presentation.login.Model model =
                new reservas.presentation.login.Model();

        new reservas.presentation.login.Controller(
                view,
                model
        );

        view.pack();
        view.setLocationRelativeTo(null);
        view.setVisible(true);
    }

    private static void doRun() {

        JFrame window = new JFrame("Sistema de Reservas");

        Icons.setWindowIcon(window, "icon.png");

        JTabbedPane tabs = new JTabbedPane();

        // =================================
        // PANEL DE ALERTAS
        // =================================

        JPanel panelAlertas = new JPanel(new BorderLayout());

        panelAlertas.setBorder(
                BorderFactory.createTitledBorder("Alertas")
        );

        panelAlertas.setPreferredSize(new Dimension(320, 0));

        DefaultTableModel modeloAlertas = new DefaultTableModel(
                new Object[]{"Actividad", "Ver", "Descartar"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 1 || column == 2;
            }
        };

        JTable tablaAlertas = new JTable(modeloAlertas);

        tablaAlertas.setRowHeight(35);
        tablaAlertas.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        tablaAlertas.getColumnModel()
                .getColumn(0).setPreferredWidth(190);

        tablaAlertas.getColumnModel()
                .getColumn(1).setPreferredWidth(55);

        tablaAlertas.getColumnModel()
                .getColumn(2).setPreferredWidth(75);

        // Ícono para VER
        tablaAlertas.getColumnModel()
                .getColumn(1)
                .setCellRenderer((table, value, selected, focus, row, column) -> {

                    JButton boton = new JButton();
                    Icons.set(boton, "ver.png");

                    boton.setToolTipText("Ver reserva");
                    boton.setBorderPainted(false);
                    boton.setContentAreaFilled(false);
                    boton.setFocusPainted(false);

                    return boton;
                });

// Ícono para DESCARTAR
        tablaAlertas.getColumnModel()
                .getColumn(2)
                .setCellRenderer((table, value, selected, focus, row, column) -> {

                    JButton boton = new JButton();
                    Icons.set(boton, "descartar.png");

                    boton.setToolTipText("Descartar notificación");
                    boton.setBorderPainted(false);
                    boton.setContentAreaFilled(false);
                    boton.setFocusPainted(false);

                    return boton;
                });

        List<Reserva> reservasNotificadas = new ArrayList<>();

        ServiceProxy proxy = (ServiceProxy) Servicio.get();

        // =================================
        // RECEPCIÓN DE NOTIFICACIONES
        // =================================

        proxy.agregarListenerReservas(reserva -> {

            reservasNotificadas.add(reserva);

            String funcionario =
                    String.valueOf(reserva.getFuncionario());

            modeloAlertas.addRow(new Object[]{
                    funcionario + " - " + reserva.getActividad(),
                    "Ver",
                    "Descartar"
            });
        });

        // =================================
        // ACCIONES DE LA TABLA
        // =================================

        tablaAlertas.addMouseListener(
                new java.awt.event.MouseAdapter() {

                    @Override
                    public void mouseClicked(java.awt.event.MouseEvent e) {

                        int fila = tablaAlertas.rowAtPoint(e.getPoint());

                        int columna = tablaAlertas.columnAtPoint(e.getPoint());

                        if (fila < 0 || columna < 1) {
                            return;
                        }

                        Reserva reserva = reservasNotificadas.get(fila);

                        // VER DETALLES
                        if (columna == 1) {

                            StringBuilder detalle = new StringBuilder();

                            detalle.append("ID: ").append(reserva.getId());
                            detalle.append("\nFuncionario: ").append(reserva.getFuncionario());
                            detalle.append("\nActividad: ").append(reserva.getActividad());
                            detalle.append("\nFecha: ").append(reserva.getFecha());
                            detalle.append("\nHora: ")
                                    .append(reserva.getHoraInicio())
                                    .append(" - ")
                                    .append(reserva.getHoraFin());
                            detalle.append("\nEstado: ").append(reserva.getEstado());

                            detalle.append("\nRecursos reservados:");

                            if (reserva.getRecursos() != null
                                    && !reserva.getRecursos().isEmpty()) {

                                for (var recurso : reserva.getRecursos()) {
                                    detalle.append("\n- ").append(recurso);
                                }

                            } else {
                                detalle.append("\nNo hay recursos asociados.");
                            }

                            JOptionPane.showMessageDialog(
                                    window,
                                    detalle.toString(),
                                    "Detalle de la reserva",
                                    JOptionPane.INFORMATION_MESSAGE
                            );
                        }

                        // DESCARTAR NOTIFICACIÓN
                        if (columna == 2) {

                            int opcion = JOptionPane.showConfirmDialog(
                                    window,

                                    "¿Desea descartar esta notificación?",

                                    "Confirmar",

                                    JOptionPane.YES_NO_OPTION
                            );

                            if (opcion == JOptionPane.YES_OPTION) {

                                reservasNotificadas.remove(fila);

                                modeloAlertas.removeRow(fila);
                            }
                        }
                    }
                }
        );

        panelAlertas.add(
                new JScrollPane(tablaAlertas),
                BorderLayout.CENTER
        );

        // =================================
        // ADMINISTRADOR
        // =================================

        if (Sesion.getUsuario().esAdministrador()) {

            // FUNCIONARIOS
            reservas.presentation.funcionarios.View funcionariosView =
                    new reservas.presentation.funcionarios.View();

            reservas.presentation.funcionarios.Model funcionariosModel =
                    new reservas.presentation.funcionarios.Model();

            new reservas.presentation.funcionarios.Controller(
                    funcionariosView,
                    funcionariosModel
            );

            tabs.addTab(
                    "Funcionarios",
                    Icons.load("funcionarios.png"),
                    funcionariosView.getPanel()
            );

            // CATEGORÍAS
            reservas.presentation.categorias.View categoriasView =
                    new reservas.presentation.categorias.View();

            reservas.presentation.categorias.Model categoriasModel =
                    new reservas.presentation.categorias.Model();

            new reservas.presentation.categorias.Controller(
                    categoriasView,
                    categoriasModel
            );

            tabs.addTab(
                    "Categorías",
                    Icons.load("categorias.png"),
                    categoriasView.getPanel()
            );

            // RECURSOS
            reservas.presentation.recursos.View recursosView =
                    new reservas.presentation.recursos.View();

            reservas.presentation.recursos.Model recursosModel =
                    new reservas.presentation.recursos.Model();

            new reservas.presentation.recursos.Controller(
                    recursosView,
                    recursosModel
            );

            tabs.addTab(
                    "Recursos",
                    Icons.load("recursos.png"),
                    recursosView.getPanel()
            );
        }

        // =================================
        // FUNCIONARIO
        // =================================

        if (Sesion.getUsuario().esFuncionario()) {

            reservas.presentation.reservas.View reservasView =
                    new reservas.presentation.reservas.View();

            reservas.presentation.reservas.Model reservasModel =
                    new reservas.presentation.reservas.Model();

            new reservas.presentation.reservas.Controller(
                    reservasView,
                    reservasModel
            );

            tabs.addTab(
                    "Reservas",
                    Icons.load("reservas.png"),
                    reservasView.getPanel()
            );
        }

        // =================================
        // CALENDARIZACIÓN DE RECURSOS
        // =================================

        reservas.presentation.calendarizacion.View calendarizacionView =
                new reservas.presentation.calendarizacion.View();

        reservas.presentation.calendarizacion.Model calendarizacionModel =
                new reservas.presentation.calendarizacion.Model();

        new reservas.presentation.calendarizacion.Controller(
                calendarizacionView,
                calendarizacionModel
        );

        tabs.addTab(
                "Calendarización",
                Icons.load("calendarizacion.png"),
                calendarizacionView.getPanel()
        );

        // =================================
        // CALENDARIZACIÓN DE ACTIVIDADES
        // =================================

        reservas.presentation.actividades.View actividadesView =
                new reservas.presentation.actividades.View();

        reservas.presentation.actividades.Model actividadesModel =
                new reservas.presentation.actividades.Model();

        new reservas.presentation.actividades.Controller(
                actividadesView,
                actividadesModel
        );

        tabs.addTab(
                "Actividades",
                Icons.load("actividades.png"),
                actividadesView.getPanel()
        );

        // =================================
        // ESTADÍSTICAS
        // =================================

        reservas.presentation.estadisticas.View estadisticasView =
                new reservas.presentation.estadisticas.View();

        reservas.presentation.estadisticas.Model estadisticasModel =
                new reservas.presentation.estadisticas.Model();

        new reservas.presentation.estadisticas.Controller(
                estadisticasView,
                estadisticasModel
        );

        tabs.addTab(
                "Estadísticas",
                Icons.load("statistics.png"),
                estadisticasView.getPanel()
        );

        // =================================
        // VENTANA PRINCIPAL
        // =================================

        JPanel panelPrincipal = new JPanel(new BorderLayout());

        panelPrincipal.add(tabs, BorderLayout.CENTER);
        panelPrincipal.add(panelAlertas, BorderLayout.EAST);

        window.setContentPane(panelPrincipal);

        window.setDefaultCloseOperation(
                JFrame.EXIT_ON_CLOSE
        );

        window.setSize(1150, 700);

        window.setLocationRelativeTo(null);

        window.setVisible(true);
    }
}
