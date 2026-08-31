package reservas;

import reservas.presentation.funcionarios.Controller;
import reservas.presentation.funcionarios.Model;
import reservas.presentation.funcionarios.View;

import javax.swing.*;

public class Application {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            View view = new View();
            Model model = new Model();

            new Controller(view, model);

            JFrame frame = new JFrame(
                    "Sistema de Reservas - Funcionarios"
            );

            frame.setContentPane(
                    view.getPanel()
            );

            frame.setDefaultCloseOperation(
                    JFrame.EXIT_ON_CLOSE
            );

            frame.setSize(
                    750,
                    600
            );

            frame.setLocationRelativeTo(null);

            frame.setVisible(true);
        });
    }
}