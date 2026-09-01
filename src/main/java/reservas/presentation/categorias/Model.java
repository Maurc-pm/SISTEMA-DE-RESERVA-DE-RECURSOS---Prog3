package reservas.presentation.categorias;

import reservas.logic.Categoria;
import reservas.presentation.AbstractModel;

import java.util.ArrayList;
import java.util.List;

public class Model extends AbstractModel {

    public static final String CURRENT = "current";
    public static final String CATEGORIAS = "categorias";

    private Categoria current;
    private List<Categoria> categorias;

    public Model() {
        current = new Categoria();
        categorias = new ArrayList<>();
    }

    public Categoria getCurrent() {
        return current;
    }

    public void setCurrent(Categoria current) {

        this.current = current != null
                ? current
                : new Categoria();

        firePropertyChange(CURRENT);
    }

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