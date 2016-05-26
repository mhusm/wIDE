package ch.ethz.inf.globis.wide.language.css;

import ch.ethz.inf.globis.wide.io.query.WideQueryResponse;
import ch.ethz.inf.globis.wide.ui.components.popup.WidePopupHelper;
import ch.ethz.inf.globis.wide.ui.components.popup.WideTableCellRenderer;
import ch.ethz.inf.globis.wide.ui.components.popup.WideTableModel;
import com.intellij.openapi.editor.Editor;
import com.intellij.ui.table.JBTable;

import javax.swing.*;
import java.awt.*;

/**
 * Created by fabian on 12.05.16.
 */
public class WideCssPopupHelper extends WidePopupHelper {

    private static final WideCssPopupHelper INSTANCE = new WideCssPopupHelper();

    private WideCssPopupHelper() {

    }

    public static WideCssPopupHelper getInstance() {
        return INSTANCE;
    }


    @Override
    public void showLookupResults(WideQueryResponse parentResult, WideQueryResponse subResult, Editor editor) {
        WideTableModel tableModel = new WideTableModel();
        tableModel.addColumn("Name");
        tableModel.addColumn("Type");
        tableModel.addColumn("Result");
        addResultsRecursive(tableModel, parentResult);

        JTable popupTable = new JBTable(tableModel);
        popupTable.getColumn("Name").setCellRenderer(new WideTableCellRenderer());
        popupTable.getColumn("Type").setCellRenderer(new WideTableCellRenderer());
        popupTable.getColumn("Result").setCellRenderer(new WideTableCellRenderer());

        showPopup(popupTable, new Dimension(600, 200), "Lookup Results", editor);
    }

//    @Override
//    public void showSuggestions(List<WideQueryResponse> suggestions, ToolWindow toolWindow, PsiElement element, Editor editor) {
//
//    }
}
