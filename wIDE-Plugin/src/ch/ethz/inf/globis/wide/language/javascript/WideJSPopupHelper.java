package ch.ethz.inf.globis.wide.language.javascript;

import ch.ethz.inf.globis.wide.lookup.io.WideQueryResponse;
import ch.ethz.inf.globis.wide.ui.components.popup.WidePopupHelper;
import ch.ethz.inf.globis.wide.ui.components.popup.WideTableCellRenderer;
import ch.ethz.inf.globis.wide.ui.components.popup.WideTableModel;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.psi.PsiElement;
import com.intellij.ui.table.JBTable;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Created by fabian on 12.05.16.
 */
public class WideJSPopupHelper extends WidePopupHelper {

    private static final WideJSPopupHelper INSTANCE = new WideJSPopupHelper();

    private WideJSPopupHelper() {

    }

    public static WideJSPopupHelper getInstance() {
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

    @Override
    public void showSuggestions(List<WideQueryResponse> suggestions, ToolWindow toolWindow, PsiElement element, Editor editor) {
        //TODO IMPLEMENTATION
    }
}
