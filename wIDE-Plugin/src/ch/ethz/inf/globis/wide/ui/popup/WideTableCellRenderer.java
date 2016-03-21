package ch.ethz.inf.globis.wide.ui.popup;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class WideTableCellRenderer extends DefaultTableCellRenderer {

    /* (non-Javadoc)
     * @see javax.swing.table.DefaultTableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
     */
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus, int row, int column) {

        Component rendererComp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
                row, column);

        switch(column) {
            case 0:
                rendererComp.setFont(new Font("Dialog", Font.BOLD, 12));
                break;
            case 1:
                break;
            case 2:
                break;
            default:
                break;
        }

        return rendererComp ;
    }

}