package reservas.presentation.estadisticas;

import reservas.logic.EstadisticaService;
import reservas.logic.Service;

import javax.swing.*;

public class Controller {

    private final View view;
    private final Model model;
    private final EstadisticaService estadisticaService;

    public Controller(View view, Model model) {
        this.view = view;
        this.model = model;
        this.estadisticaService = new EstadisticaService();

        view.setController(this);
    }

    public void consultarRecursos() {

        try {

            model.setRecursos(
                    estadisticaService.recursosPorCategoria(
                            view.getDesdeRecursos(),
                            view.getHastaRecursos(),
                            Service.instance().listarReservas()
                    )
            );

            view.mostrarRecursos(
                    model.getRecursos()
            );

            view.mostrarGraficoRecursos(
                    model.getRecursos()
            );

        } catch (Exception ex) {

            JOptionPane.showMessageDialog(
                    view.getPanel(),
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
    public void consultarActividades() {

        try {

            model.setActividades(
                    estadisticaService.actividadesPorSemana(
                            view.getDesdeRecursos(),
                            view.getHastaRecursos(),
                            Service.instance().listarReservas()
                    )
            );

            view.mostrarActividades(
                    model.getActividades()
            );

            view.mostrarGraficoActividades(
                    model.getActividades()
            );

        } catch (Exception ex) {

            JOptionPane.showMessageDialog(
                    view.getPanel(),
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }


}