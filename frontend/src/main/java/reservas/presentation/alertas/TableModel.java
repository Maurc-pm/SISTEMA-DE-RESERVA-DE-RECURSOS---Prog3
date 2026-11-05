package reservas.presentation.alertas;

import reservas.logic.Reserva;
import reservas.presentation.AbstractTableModel;
import reservas.presentation.Icons;

import javax.swing.Icon;
import java.util.List;

public class TableModel extends AbstractTableModel<Reserva> {

    public static final int ACTIVIDAD = 0;
    public static final int VER = 1;
    public static final int DESCARTAR = 2;

    private static final Icon ICONO_VER = Icons.load("ver.png");
    private static final Icon ICONO_DESCARTAR = Icons.load("descartar.png");

    public TableModel(List<Reserva> rows) {
        super(new int[]{ACTIVIDAD, VER, DESCARTAR}, rows);
    }

    public static String encargado(Reserva reserva) {
        if (reserva.getFuncionario() == null) {
            return "";
        }
        String nombre = reserva.getFuncionario().getNombre();
        return nombre == null || nombre.isBlank() ? reserva.getFuncionario().getId() : nombre;
    }

    @Override
    protected Object getPropertyAt(Reserva reserva, int column) {
        switch (column) {
            case ACTIVIDAD:
                return encargado(reserva) + " - " + reserva.getActividad();
            case VER:
                return ICONO_VER;
            case DESCARTAR:
                return ICONO_DESCARTAR;
            default:
                return "";
        }
    }

    @Override
    protected void initColumnNames() {
        columnNames = new String[3];
        columnNames[ACTIVIDAD] = "Actividad";
        columnNames[VER] = "Ver";
        columnNames[DESCARTAR] = "Desc.";
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return columns[columnIndex] == ACTIVIDAD ? String.class : Icon.class;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }
}
