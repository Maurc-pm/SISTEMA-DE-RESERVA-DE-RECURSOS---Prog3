package reservas.presentation.cambiarclave;

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

    public void cambiarClave() {

        try {

            String id = view.getId();
            String claveActual = view.getClaveActual();
            String claveNueva = view.getClaveNueva();
            String confirmar = view.getConfirmarClave();

            if (!claveNueva.equals(confirmar)) {
                throw new Exception(
                        "La clave nueva y la confirmación no coinciden"
                );
            }

            Usuario usuario =
                    Service.instance().buscarUsuarioPorId(id);

            Service.instance()
                    .cambiarClave(usuario, claveActual, claveNueva);

            view.mostrarMensaje(
                    "Contraseña cambiada correctamente. "
                            + "Ya puede iniciar sesión con la nueva clave."
            );

            view.cerrar();

        } catch (Exception ex) {
            view.mostrarError(ex.getMessage());
        }
    }
}