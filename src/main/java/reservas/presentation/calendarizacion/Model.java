package reservas.presentation.calendarizacion;

import reservas.logic.Categoria;
import reservas.logic.Recurso;
import reservas.presentation.AbstractModel;
import reservas.logic.Celda;

import java.util.ArrayList;
import java.util.List;

public class Model extends AbstractModel {

    public static final String CATEGORIAS = "categorias";
    public static final String MATRIZ = "matriz";

    private static final int HORAS_DIA = 24;

    private List<Categoria> categorias;
    private List<Recurso> recursos;
    private Celda[][] matriz;

    public Model() {
        categorias = new ArrayList<>();
        recursos = new ArrayList<>();
        matriz = new Celda[HORAS_DIA][0];
    }

    // =========================================================
    // CATEGORÍAS
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

    // =========================================================
    // RECURSOS (columnas) Y MATRIZ (celdas)
    // =========================================================

    public List<Recurso> getRecursos() {
        return recursos;
    }

    public Celda[][] getMatriz() {
        return matriz;
    }

    public void setMatriz(List<Recurso> recursos, Celda[][] matriz) {

        this.recursos = recursos != null
                ? new ArrayList<>(recursos)
                : new ArrayList<>();

        this.matriz = matriz != null
                ? matriz
                : new Celda[HORAS_DIA][0];

        firePropertyChange(MATRIZ);
    }
}
