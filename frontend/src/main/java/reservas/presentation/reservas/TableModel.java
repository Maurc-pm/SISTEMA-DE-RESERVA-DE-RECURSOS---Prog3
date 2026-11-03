package reservas.presentation.reservas;

import reservas.logic.Reserva;
import reservas.presentation.AbstractTableModel;

import java.util.List;

public class TableModel extends AbstractTableModel<Reserva> {

    public static final int ID = 0;
    public static final int ACTIVIDAD = 1;
    public static final int FECHA = 2;
    public static final int INICIO = 3;
    public static final int FIN = 4;
    public static final int ESTADO = 5;

    public TableModel(int[] columns, List<Reserva> rows) {
        super(columns, rows);
    }

    @Override
    protected Object getPropertyAt(Reserva reserva, int column) {

        switch (column) {

            case ID:
                return reserva.getId();

            case ACTIVIDAD:
                return reserva.getActividad();

            case FECHA:
                return reserva.getFecha();

            case INICIO:
                return reserva.getHoraInicio();

            case FIN:
                return reserva.getHoraFin();

            case ESTADO:
                return reserva.getEstado();

            default:
                return "";
        }
    }

    @Override
    protected void initColumnNames() {

        columnNames = new String[6];

        columnNames[ID] = "ID";
        columnNames[ACTIVIDAD] = "Actividad";
        columnNames[FECHA] = "Fecha";
        columnNames[INICIO] = "Inicio";
        columnNames[FIN] = "Fin";
        columnNames[ESTADO] = "Estado";
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return String.class;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }
}