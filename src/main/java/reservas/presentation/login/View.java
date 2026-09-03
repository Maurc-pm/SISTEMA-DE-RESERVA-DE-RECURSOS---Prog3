package reservas.presentation.login;

import javax.swing.*;
import java.awt.event.*;

public class View extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JPanel panelLogin;
    private JLabel clave;
    private JLabel Id;
    private JTextField idFld;
    private JPasswordField claveFld;
    private JLabel titulo;
    private JButton buttonCambiarClave;
    private Model model;
    private Controller controller;

    public View() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        buttonCambiarClave.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCambiarClave();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    public String getId() {
        return idFld.getText().trim();
    }

    public String getClave() {
        return new String(claveFld.getPassword());
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    private void onOK() {
        controller.login();
    }

    private void onCancel() {
        controller.cancelar();
    }

    public void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(
                this,
                mensaje,
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    private void onCambiarClave() {
        controller.cambiarClave();
    }

    private void createUIComponents() {
        // No hace falta poner nada aquí por ahora
    }
}
