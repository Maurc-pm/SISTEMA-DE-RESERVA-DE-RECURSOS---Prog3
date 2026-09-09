package reservas.presentation.actividades;

import reservas.presentation.AbstractModel;
import reservas.presentation.Celda;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Model extends AbstractModel {

    public static final String MATRIZ = "matriz";

    private static final int HORAS_DIA = 24;
    private static final int DIAS_SEMANA = 7;

    private List<LocalDate> dias;
    private Celda[][] matriz;

    public Model() {

        dias = new ArrayList<>();
        matriz = new Celda[HORAS_DIA][DIAS_SEMANA];

        for (int hora = 0; hora < HORAS_DIA; hora++) {
            for (int dia = 0; dia < DIAS_SEMANA; dia++) {
                matriz[hora][dia] = new Celda();
            }
        }
    }

    public List<LocalDate> getDias() {
        return dias;
    }

    public Celda[][] getMatriz() {
        return matriz;
    }

    public void setMatriz(List<LocalDate> dias, Celda[][] matriz) {

        this.dias = dias != null
                ? new ArrayList<>(dias)
                : new ArrayList<>();

        this.matriz = matriz != null
                ? matriz
                : new Celda[HORAS_DIA][DIAS_SEMANA];

        firePropertyChange(MATRIZ);
    }
}
