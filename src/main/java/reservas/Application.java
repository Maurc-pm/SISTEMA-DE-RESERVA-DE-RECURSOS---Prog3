package reservas;

import reservas.logic.Categoria;
import reservas.logic.Service;
import reservas.presentation.recursos.Controller;
import reservas.presentation.recursos.Model;
import reservas.presentation.recursos.View;

import javax.swing.*;

public class Application {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            try {

                cargarCategoriasPrueba();

                View view = new View();
                Model model = new Model();

                new Controller(view, model);

                JFrame window = new JFrame(
                        "Sistema de Reservas - Recursos"
                );

                window.setContentPane(view.getPanel());
                window.setDefaultCloseOperation(
                        JFrame.EXIT_ON_CLOSE
                );

                window.setSize(800, 650);
                window.setLocationRelativeTo(null);
                window.setVisible(true);

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

    // SOLO PARA PROBAR RECURSOS
    private static void cargarCategoriasPrueba()
            throws Exception {

        if (!Service.instance()
                .listarCategorias()
                .isEmpty()) {

            return;
        }

        Categoria c1 = new Categoria();
        c1.setDescripcion("Laptop Windows 11");
        Service.instance().crearCategoria(c1);

        Categoria c2 = new Categoria();
        c2.setDescripcion("Proyector");
        Service.instance().crearCategoria(c2);

        Categoria c3 = new Categoria();
        c3.setDescripcion("Sala de reuniones");
        Service.instance().crearCategoria(c3);
    }
}