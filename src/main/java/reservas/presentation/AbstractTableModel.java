package reservas.presentation;

import java.util.List;

public abstract class AbstractTableModel<E>
        extends javax.swing.table.AbstractTableModel {

    protected List<E> rows;
    protected int[] columns;
    protected String[] columnNames;

    public AbstractTableModel(int[] columns, List<E> rows) {
        this.columns = columns;
        this.rows = rows;
        initColumnNames();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public int getRowCount() {
        return rows.size();
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[columns[column]];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {

        E element = rows.get(rowIndex);

        return getPropertyAt(
                element,
                columns[columnIndex]
        );
    }

    public E getRowAt(int rowIndex) {
        return rows.get(rowIndex);
    }

    protected abstract Object getPropertyAt(
            E element,
            int column
    );

    protected abstract void initColumnNames();
}