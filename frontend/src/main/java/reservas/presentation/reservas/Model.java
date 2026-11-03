package reservas.presentation.reservas;

import reservas.logic.Categoria;
import reservas.logic.Reserva;
import reservas.presentation.AbstractModel;

import java.util.ArrayList;
import java.util.List;

public class Model extends AbstractModel {

    public static final String CURRENT = "current";
    public static final String RESERVAS = "reservas";
    public static final String CATEGORIAS = "categorias";
    public static final String CATEGORIAS_SELECCIONADAS = "categoriasSeleccionadas";

    private Reserva current;
    private List<Reserva> reservas;
    private List<Categoria> categorias;
    private List<Categoria> categoriasSeleccionadas;

    public Model() {

        current = new Reserva();
        reservas = new ArrayList<>();
        categorias = new ArrayList<>();
        categoriasSeleccionadas = new ArrayList<>();
    }

    public Reserva getCurrent() {
        return current;
    }

    public void setCurrent(Reserva current) {

        this.current = current != null
                ? current
                : new Reserva();

        firePropertyChange(CURRENT);
    }

    public List<Reserva> getReservas() {
        return reservas;
    }

    public void setReservas(List<Reserva> reservas) {

        this.reservas = reservas != null
                ? new ArrayList<>(reservas)
                : new ArrayList<>();

        firePropertyChange(RESERVAS);
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

    public List<Categoria> getCategoriasSeleccionadas() {
        return categoriasSeleccionadas;
    }

    public void setCategoriasSeleccionadas(
            List<Categoria> categoriasSeleccionadas
    ) {

        this.categoriasSeleccionadas = categoriasSeleccionadas != null
                        ? new ArrayList<>(categoriasSeleccionadas)
                        : new ArrayList<>();

        firePropertyChange(CATEGORIAS_SELECCIONADAS);
    }
}
