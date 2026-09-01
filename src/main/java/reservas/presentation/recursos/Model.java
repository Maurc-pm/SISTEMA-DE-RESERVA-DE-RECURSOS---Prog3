package reservas.presentation.recursos;

import reservas.logic.Categoria;
import reservas.logic.Recurso;
import reservas.presentation.AbstractModel;

import java.util.ArrayList;
import java.util.List;

public class Model extends AbstractModel {

    public static final String CURRENT = "current";
    public static final String RECURSOS = "recursos";
    public static final String CATEGORIAS = "categorias";

    private Recurso current;
    private List<Recurso> recursos;
    private List<Categoria> categorias;

    public Model() {
        current = new Recurso();
        recursos = new ArrayList<>();
        categorias = new ArrayList<>();
    }

    // =========================================================
    // RECURSO ACTUAL
    // =========================================================

    public Recurso getCurrent() {
        return current;
    }

    public void setCurrent(Recurso current) {

        this.current = current != null
                ? current
                : new Recurso();

        firePropertyChange(CURRENT);
    }

    // =========================================================
    // LISTA DE RECURSOS
    // =========================================================

    public List<Recurso> getRecursos() {
        return recursos;
    }

    public void setRecursos(List<Recurso> recursos) {

        this.recursos = recursos != null
                ? new ArrayList<>(recursos)
                : new ArrayList<>();

        firePropertyChange(RECURSOS);
    }

    // =========================================================
    // LISTA DE CATEGORÍAS
    // =========================================================

    public List<Categoria> getCategorias() {
        return categorias;
    }

    public void setCategorias(List<Categoria> categorias) {

        this.categorias = categorias != null
                ? new ArrayList<>(categorias)
                : new ArrayList<>();

        firePropertyChange(CATEGORIAS);
    }
}