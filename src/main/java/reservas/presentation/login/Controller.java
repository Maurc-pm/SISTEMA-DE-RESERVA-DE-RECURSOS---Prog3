package reservas.presentation.login;

import reservas.logic.Sesion;
import reservas.logic.Service;
import reservas.logic.Usuario;

public class Controller {

    private final View view;
    private final Model model;

    public Controller(View view, Model model) {
        this.view = view;
        this.model = model;

        view.setModel(model);
        view.setController(this);
    }

    public void login() {

        try {

            String id = view.getId();
            String clave = view.getClave();

            Usuario usuario =
                    Service.instance()
                            .login(id, clave);

            model.setCurrent(usuario);

            Sesion.setUsuario(usuario);

            view.dispose();

        } catch (Exception ex) {

            view.mostrarError(
                    ex.getMessage()
            );
        }
    }

    public void cancelar() {

        Sesion.logout();

        view.dispose();
    }

    public void cambiarClave() {

        reservas.presentation.cambiarclave.View viewCambiar =
                new reservas.presentation.cambiarclave.View();

        reservas.presentation.cambiarclave.Model modelCambiar =
                new reservas.presentation.cambiarclave.Model();

        new reservas.presentation.cambiarclave.Controller(viewCambiar, modelCambiar);

        viewCambiar.pack();
        viewCambiar.setLocationRelativeTo(view);
        viewCambiar.setVisible(true);
    }
}
