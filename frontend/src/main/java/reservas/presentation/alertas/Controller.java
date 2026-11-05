package reservas.presentation.alertas;

import reservas.logic.Reserva;

/**
 * Controlador del área de alertas. Recibe las reservas notificadas
 * asincrónicamente por el Backend (vía el Proxy) y atiende Ver/Descartar.
 * Descartar solo remueve la alerta de la lista local: no se envía
 * ninguna petición al Backend.
 */
public class Controller {

    private final View view;
    private final Model model;

    public Controller(View view, Model model) {
        this.view = view;
        this.model = model;
        view.setController(this);
        view.setModel(model);
    }

    /** Se invoca (en el hilo de Swing) cuando llega una notificación de nueva reserva. */
    public void notificar(Reserva reserva) {
        model.agregar(reserva);
    }

    public void ver(int fila) {
        if (fila < 0 || fila >= model.getAlertas().size()) {
            return;
        }
        view.mostrarDetalle(model.getAlerta(fila));
    }

    public void descartar(int fila) {
        if (fila < 0 || fila >= model.getAlertas().size()) {
            return;
        }
        if (view.confirmarDescarte(model.getAlerta(fila))) {
            model.quitar(fila);
        }
    }
}
