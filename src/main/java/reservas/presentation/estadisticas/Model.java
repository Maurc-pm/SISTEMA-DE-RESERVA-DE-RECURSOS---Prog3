package reservas.presentation.estadisticas;

import reservas.presentation.AbstractModel;

import java.util.LinkedHashMap;
import java.util.Map;

public class Model extends AbstractModel {

    public static final String RECURSOS = "recursos";
    public static final String ACTIVIDADES = "actividades";

    private Map<String, Integer> recursos;
    private Map<String, Integer> actividades;

    public Model() {
        recursos = new LinkedHashMap<>();
        actividades = new LinkedHashMap<>();
    }

    public Map<String, Integer> getRecursos() {
        return recursos;
    }

    public void setRecursos(Map<String, Integer> recursos) {
        this.recursos = recursos != null
                ? new LinkedHashMap<>(recursos)
                : new LinkedHashMap<>();

        firePropertyChange(RECURSOS);
    }

    public Map<String, Integer> getActividades() {
        return actividades;
    }

    public void setActividades(Map<String, Integer> actividades) {
        this.actividades = actividades != null
                ? new LinkedHashMap<>(actividades)
                : new LinkedHashMap<>();

        firePropertyChange(ACTIVIDADES);
    }
}