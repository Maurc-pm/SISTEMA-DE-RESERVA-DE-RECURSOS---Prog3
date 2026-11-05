package reservas.presentation.alertas;

import reservas.logic.Reserva;
import reservas.presentation.AbstractModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Modelo del área de alertas: reservas creadas por otros usuarios durante
 * la sesión actual. Es solo informativo; descartar no afecta al Backend.
 */
public class Model extends AbstractModel {

    public static final String ALERTAS = "alertas";

    private final List<Reserva> alertas = new ArrayList<>();

    public List<Reserva> getAlertas() {
        return Collections.unmodifiableList(alertas);
    }

    public Reserva getAlerta(int indice) {
        return alertas.get(indice);
    }

    public void agregar(Reserva reserva) {
        if (reserva == null) {
            return;
        }
        alertas.add(reserva);
        firePropertyChange(ALERTAS);
    }

    public void quitar(int indice) {
        if (indice < 0 || indice >= alertas.size()) {
            return;
        }
        alertas.remove(indice);
        firePropertyChange(ALERTAS);
    }
}
