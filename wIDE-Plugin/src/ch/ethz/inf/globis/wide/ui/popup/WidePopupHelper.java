package ch.ethz.inf.globis.wide.ui.popup;

import ch.ethz.inf.globis.wide.lookup.io.WideQueryResponse;
import ch.ethz.inf.globis.wide.ui.components.WideContentBuilder;
import ch.ethz.inf.globis.wide.ui.components.panel.WideSuggestionJFXPanel;
import ch.ethz.inf.globis.wide.ui.components.list.WideSuggestionListView;
import ch.ethz.inf.globis.wide.ui.listener.WideMouseEventListenerHelper;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.progress.AsynchronousExecution;
import com.intellij.openapi.ui.popup.*;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.psi.PsiElement;
import com.intellij.ui.table.JBTable;
import javafx.application.Platform;
import javafx.scene.control.ListView;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Created by fabian on 06.04.16.
 */
public class WidePopupHelper extends WideContentBuilder {

    private JBPopup popup;
    private static final WidePopupHelper INSTANCE = new WidePopupHelper();

    private WidePopupHelper() {
    }

    public static WidePopupHelper getInstance() {
        return INSTANCE;
    }

    public void hidePopup(Editor editor) {
        if (popup != null) {
            popup.cancel();
            this.popup = null;
        }
    }

    private void showPopup(JComponent content, Dimension size, String title, Editor editor) {
        hidePopup(editor);

            ComponentPopupBuilder popupBuilder = JBPopupFactory.getInstance().createComponentPopupBuilder(content, editor.getComponent());
            if (title != null) {
                popupBuilder.setTitle(title);
            }
            JBPopup popup = popupBuilder.createPopup();
            popup.setSize(size);

            popup.addListener(new JBPopupListener() {
                @Override
                public void beforeShown(LightweightWindowEvent lightweightWindowEvent) {
                }

                @Override
                public void onClosed(LightweightWindowEvent lightweightWindowEvent) {
                    WideMouseEventListenerHelper.getInstance().deregisterMouseEventListener();
                }
            });

            this.popup = popup;

        //popup.setRequestFocus(true);
        popup.show(JBPopupFactory.getInstance().guessBestPopupLocation(editor));
//        popup.showInBestPositionFor(editor);

        this.popup = popup;
    }

    public void showHtmlTagLookupResults(List<WideQueryResponse> results, Editor editor) {
        // Show appropriate content of an existing HTML Tag
        JEditorPane editorPane = createNewEditorPane("<html><body>" + results.get(0).getMdn().getAttributes() + "</body></html>", buildHtmlEdiorKit());
        JScrollPane scrollPane = createNewScrollPane(editorPane);
        showPopup(scrollPane, new Dimension(600, 200), results.get(0).getKey(), editor);

        // Register Mouse-Movement-Event-Listener to change popup content
        WideMouseEventListenerHelper.getInstance().registerMouseEventListener(results, results.get(0), editor);
    }

    public void showHtmlNewTagLookupResults(WideQueryResponse result, Editor editor) {
        // Show appropriate content of a new HTML Tag
        JEditorPane editorPane = createNewEditorPane("<html><body>" + result.getMdn().getExamples() + "</body></html>", buildHtmlEdiorKit());
        JScrollPane scrollPane = createNewScrollPane(editorPane);
        showPopup(scrollPane, new Dimension(600, 200), "Lookup Results", editor);
    }

    public void showHtmlAttributeLookupResult(List<WideQueryResponse> results, WideQueryResponse result, Editor editor) {
        // Show appropriate content of an HTML attribute
        JEditorPane editorPane = createNewEditorPane("<html><body>" + result.getMdn().getSummary().replace("\n", "") + "</body></html>", buildHtmlEdiorKit());
        JScrollPane scrollPane = createNewScrollPane(editorPane);
        showPopup(scrollPane, new Dimension(300, 200), result.getKey(), editor);

        WideMouseEventListenerHelper.getInstance().registerMouseEventListener(results, result, editor);
    }

    public void showJsLookupResults(List<WideQueryResponse> results, Editor editor) {
        WideTableModel tableModel = new WideTableModel();
        tableModel.addColumn("Name");
        tableModel.addColumn("Type");
        tableModel.addColumn("Result");
        addResultsRecursive(tableModel, results);

        JTable popupTable = new JBTable(tableModel);
        popupTable.getColumn("Name").setCellRenderer(new WideTableCellRenderer());
        popupTable.getColumn("Type").setCellRenderer(new WideTableCellRenderer());
        popupTable.getColumn("Result").setCellRenderer(new WideTableCellRenderer());

        showPopup(popupTable, new Dimension(600, 200), "Lookup Results", editor);
    }

    public void showCssLookupResults(List<WideQueryResponse> results, Editor editor) {
        WideTableModel tableModel = new WideTableModel();
        tableModel.addColumn("Name");
        tableModel.addColumn("Type");
        tableModel.addColumn("Result");
        addResultsRecursive(tableModel, results);

        JTable popupTable = new JBTable(tableModel);
        popupTable.getColumn("Name").setCellRenderer(new WideTableCellRenderer());
        popupTable.getColumn("Type").setCellRenderer(new WideTableCellRenderer());
        popupTable.getColumn("Result").setCellRenderer(new WideTableCellRenderer());

        showPopup(popupTable, new Dimension(600, 200), "Lookup Results", editor);
    }


    public void showError(String error, Editor editor) {
        //TODO: implementation
        showPopup(new JLabel(error), new Dimension(300, 50), "Error", editor);
    }

    private void addResultsRecursive(DefaultTableModel tableModel, List<WideQueryResponse> results) {
        for (WideQueryResponse result : results) {
            if (result != null) {
                tableModel.addRow(result.getTableRow());
                addResultsRecursive(tableModel, result.getSubResults());
            }
        }
    }

    public void showHtmlSuggestion(java.util.List<WideQueryResponse> suggestions, ToolWindow toolWindow, PsiElement element, Editor editor) {
        toolWindow.getContentManager().removeAllContents(true);
        WideSuggestionJFXPanel panel = new WideSuggestionJFXPanel();

        // wait for JavaFX to be ready
        Platform.runLater(new Runnable() {
            public void run() {
                createHtmlSuggestionContentFx(suggestions, panel, element, editor, toolWindow);
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
    private void createHtmlSuggestionContentFx(java.util.List<WideQueryResponse> suggestions, WideSuggestionJFXPanel panel, PsiElement element, Editor editor, ToolWindow window) {
        WideSuggestionListView list = new WideSuggestionListView(suggestions, panel, element, editor, window);
        panel.setList(list, editor);
    }
}
