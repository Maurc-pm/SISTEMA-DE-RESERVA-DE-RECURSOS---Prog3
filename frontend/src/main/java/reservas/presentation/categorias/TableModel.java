package reservas.presentation.categorias;

import reservas.logic.Categoria;
import reservas.presentation.AbstractTableModel;

import java.util.List;

public class TableModel extends AbstractTableModel<Categoria> {

    public static final int ID = 0;
    public static final int DESCRIPCION = 1;

    public TableModel(int[] columns, List<Categoria> rows) {
        super(columns, rows);
    }

    @Override
    protected Object getPropertyAt(Categoria categoria, int column) {

        switch (column) {

            case ID:
                return categoria.getId();

            case DESCRIPCION:
                return categoria.getDescripcion();

            default:
                return "";
        }
    }

    @Override
    protected void initColumnNames() {

        columnNames = new String[2];

        columnNames[ID] = "ID";
        columnNames[DESCRIPCION] = "Descripción";
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