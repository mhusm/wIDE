package ch.ethz.inf.globis.wide.language.javascript;

import ch.ethz.inf.globis.wide.lookup.io.WideQueryResponse;
import ch.ethz.inf.globis.wide.ui.components.list.WideSuggestionListView;
import ch.ethz.inf.globis.wide.ui.components.panel.WideSuggestionJFXPanel;
import ch.ethz.inf.globis.wide.ui.components.popup.WidePopupHelper;
import ch.ethz.inf.globis.wide.ui.components.popup.WideTableCellRenderer;
import ch.ethz.inf.globis.wide.ui.components.popup.WideTableModel;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.progress.AsynchronousExecution;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.psi.PsiElement;
import com.intellij.ui.table.JBTable;
import javafx.application.Platform;

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
        toolWindow.getContentManager().removeAllContents(true);
        WideSuggestionJFXPanel panel = new WideSuggestionJFXPanel();

        // run in JavaFX Thread
        Platform.runLater(new Runnable() {
            public void run() {
                createJSSuggestionContentFx(suggestions, panel, element, editor, toolWindow);
            }
        });

        if (suggestions != null && suggestions.size() > 0) {
            showPopup(panel, new Dimension(600, 200), null, editor);
            panel.requestFocus();

        } else {
            hidePopup(editor);
        }

    }

    @AsynchronousExecution
    private void createJSSuggestionContentFx(java.util.List<WideQueryResponse> suggestions, WideSuggestionJFXPanel panel, PsiElement element, Editor editor, ToolWindow window) {
        WideSuggestionListView list = new WideSuggestionListView(suggestions, WideJavascriptSuggestionCell.class, panel, editor, window);
        panel.setList(list, editor);
    }
}
