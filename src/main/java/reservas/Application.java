package reservas;

import reservas.logic.Sesion;

import javax.swing.*;

public class Application {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            try {

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

        JFrame window =
                new JFrame("Sistema de Reservas");

        JTabbedPane tabs =
                new JTabbedPane();


        // =========================
        // ADMINISTRADOR
        // =========================

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
                    recursosView.getPanel()
            );
        }


        // =========================
        // FUNCIONARIO
        // =========================

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
                    reservasView.getPanel()
            );
        }

        // =========================
        // CALENDARIZACIÓN DE RECURSOS
        // DISPONIBLE PARA AMBOS ROLES
        // =========================

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
                calendarizacionView.getPanel()
        );
        // =========================
        // CALENDARIZACIÓN DE ACTIVIDADES
        // DISPONIBLE PARA AMBOS ROLES
        // =========================

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
                actividadesView.getPanel()
        );


        // =========================
        // ESTADÍSTICAS
        // DISPONIBLE PARA AMBOS ROLES
        // =========================

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
                estadisticasView.getPanel()
        );


        // =========================
        // VENTANA PRINCIPAL
        // =========================

        window.setContentPane(tabs);

        window.setDefaultCloseOperation(
                JFrame.EXIT_ON_CLOSE
        );

        window.setSize(
                900,
                650
        );

        window.setLocationRelativeTo(null);

        window.setVisible(true);
    }
}