package ch.ethz.inf.globis.wide.ui.components.popup;

import javax.swing.table.DefaultTableModel;
import java.util.Vector;

/**
 * Created by fabian on 20.03.16.
 */
public class WideTableModel extends DefaultTableModel {
    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    @Override
    public void insertRow(int row, Vector rowData) {
        dataVector.insertElementAt(rowData, row);
        fireTableRowsInserted(row, row);
    }

}
