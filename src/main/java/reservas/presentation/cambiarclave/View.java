package reservas.presentation.cambiarclave;

import javax.swing.*;
import java.awt.event.*;

public class View extends JDialog {
    private JPanel contentPane;
    private JTextField idFld;
    private JLabel ID;
    private JPasswordField claveActualFld;
    private JPasswordField claveNuevaFld;
    private JLabel claveActualLabel;
    private JLabel claveNuevaLabel;
    private JPasswordField confirmarFld;
    private JLabel confirmarContraseñaLabel;
    private JPanel ButtonPannel;
    private JButton buttonCancelar;
    private JButton buttonCambiar;
    private JLabel Titulo;
    private JPanel TituloJPanel;

    private Model model;
    private Controller controller;

    public View() {
        setTitle("Cambiar contraseña");
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonCambiar);

        buttonCambiar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCambiar();
            }
        });

        buttonCancelar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancelar();
            }
        });

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancelar();
            }
        });

        contentPane.registerKeyboardAction(new ActionListener() {
                                               public void actionPerformed(ActionEvent e) {
                                                   onCancelar();
                                               }
                                           }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    public String getId() {
        return idFld.getText().trim();
    }

    public String getClaveActual() {
        return new String(claveActualFld.getPassword());
    }

    public String getClaveNueva() {
        return new String(claveNuevaFld.getPassword());
    }

    public String getConfirmarClave() {
        return new String(confirmarFld.getPassword());
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    private void onCambiar() {
        controller.cambiarClave();
    }

    private void onCancelar() {
        dispose();
    }

    public void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(
                this, mensaje, "Error", JOptionPane.ERROR_MESSAGE
        );
    }

    public void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(
                this, mensaje, "Éxito", JOptionPane.INFORMATION_MESSAGE
        );
    }

    public void cerrar() {
        dispose();
    }
}

