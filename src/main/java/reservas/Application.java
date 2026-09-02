package reservas;

//import reservas.logic.Categoria;
//import reservas.logic.Service;
import reservas.logic.Sesion;
//import reservas.presentation.recursos.Controller;
//import reservas.presentation.recursos.Model;
//import reservas.presentation.recursos.View;

import javax.swing.*;

public class Application {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            try {

                doLogin();

                if (Sesion.isLogged()) {
                    doRun();
                }

//                cargarCategoriasPrueba();
//
//                View view = new View();
//                Model model = new Model();
//
//                new Controller(view, model);
//
//                JFrame window = new JFrame(
//                        "Sistema de Reservas - Recursos"
//                );
//
//                window.setContentPane(view.getPanel());
//                window.setDefaultCloseOperation(
//                        JFrame.EXIT_ON_CLOSE
//                );
//
//                window.setSize(800, 650);
//                window.setLocationRelativeTo(null);
//                window.setVisible(true);

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

//    // SOLO PARA PROBAR RECURSOS
//    private static void cargarCategoriasPrueba()
//            throws Exception {
//
//        if (!Service.instance()
//                .listarCategorias()
//                .isEmpty()) {
//
//            return;
//        }
//
//        Categoria c1 = new Categoria();
//        c1.setDescripcion("Laptop Windows 11");
//        Service.instance().crearCategoria(c1);
//
//        Categoria c2 = new Categoria();
//        c2.setDescripcion("Proyector");
//        Service.instance().crearCategoria(c2);
//
//        Categoria c3 = new Categoria();
//        c3.setDescripcion("Sala de reuniones");
//        Service.instance().crearCategoria(c3);
//    }

    private static void doLogin() {
        reservas.presentation.login.View view = new reservas.presentation.login.View();
        reservas.presentation.login.Model model = new reservas.presentation.login.Model();
        new reservas.presentation.login.Controller(view, model);

        view.pack();
        view.setLocationRelativeTo(null);
        view.setVisible(true);
    }

    private static void doRun() {

        JFrame window = new JFrame("Sistema de Reservas");

        JTabbedPane tabs = new JTabbedPane();

        if (Sesion.getUsuario().esAdministrador()) {
            // LOS FUNCIONARIOS
            reservas.presentation.funcionarios.View funcionariosView = new reservas.presentation.funcionarios.View();
            reservas.presentation.funcionarios.Model funcionariosModel = new reservas.presentation.funcionarios.Model();
            new reservas.presentation.funcionarios.Controller(funcionariosView, funcionariosModel);
            tabs.addTab("Funcionarios", funcionariosView.getPanel());

            // CATEGORÍAS
            reservas.presentation.categorias.View categoriasView = new reservas.presentation.categorias.View();
            reservas.presentation.categorias.Model categoriasModel = new reservas.presentation.categorias.Model();
            new reservas.presentation.categorias.Controller(categoriasView, categoriasModel);
            tabs.addTab("Categorías", categoriasView.getPanel());

            // RECURSOS
            reservas.presentation.recursos.View recursosView = new reservas.presentation.recursos.View();
            reservas.presentation.recursos.Model recursosModel = new reservas.presentation.recursos.Model();
            new reservas.presentation.recursos.Controller(recursosView, recursosModel);
            tabs.addTab("Recursos", recursosView.getPanel());
        }

        // UN FUNCIONARIO
        if (Sesion.getUsuario().esFuncionario()) {
            reservas.presentation.reservas.View reservasView = new reservas.presentation.reservas.View();
            reservas.presentation.reservas.Model reservasModel = new reservas.presentation.reservas.Model();
            new reservas.presentation.reservas.Controller(reservasView, reservasModel);
            tabs.addTab("Reservas", reservasView.getPanel());
        }

        window.setContentPane(tabs);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setSize(900, 650);
        window.setLocationRelativeTo(null);
        window.setVisible(true);
    }
}