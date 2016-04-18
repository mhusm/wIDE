package ch.ethz.inf.globis.wide.ui.popup;

import ch.ethz.inf.globis.wide.parsing.WideQueryResult;
import ch.ethz.inf.globis.wide.ui.editor.WideMouseEventListenerHelper;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.event.EditorMouseEvent;
import com.intellij.openapi.editor.event.EditorMouseMotionListener;
import com.intellij.openapi.ui.popup.*;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.table.JBTable;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import java.awt.*;
import java.util.List;

/**
 * Created by fabian on 06.04.16.
 */
public class WidePopupHelper {

    private JBPopup popup;
    private static final WidePopupHelper INSTANCE = new WidePopupHelper();

    private WidePopupHelper() {
    }

    public static WidePopupHelper getInstance() {
        return INSTANCE;
    }

    private void showPopup(JComponent content, Editor editor) {
        if (popup != null) {
            popup.cancel();
        }
        ComponentPopupBuilder popupBuilder = JBPopupFactory.getInstance().createComponentPopupBuilder(content, editor.getComponent());
        popupBuilder.setTitle("Lookup Results");
        JBPopup popup = popupBuilder.createPopup();
        popup.setSize(new Dimension(600, 200));
        popup.show(JBPopupFactory.getInstance().guessBestPopupLocation(editor));

        popup.addListener(new JBPopupListener() {
            @Override
            public void beforeShown(LightweightWindowEvent lightweightWindowEvent) {

            }

            @Override
            public void onClosed(LightweightWindowEvent lightweightWindowEvent) {
                WideMouseEventListenerHelper.getInstance().deregisterMouseEventListener();
            }
        });
    }

    public void showHtmlTagLookupResults(List<WideQueryResult> results, Editor editor) {
        WideTableModel tableModel = new WideTableModel();
        tableModel.addColumn("Name");
        tableModel.addColumn("Type");
        tableModel.addColumn("Result");
        addResultsRecursive(tableModel, results);

        JTable popupTable = new JBTable(tableModel);
        popupTable.getColumn("Name").setCellRenderer(new WideTableCellRenderer());
        popupTable.getColumn("Type").setCellRenderer(new WideTableCellRenderer());
        popupTable.getColumn("Result").setCellRenderer(new WideTableCellRenderer());

        JScrollPane scrollPane = new JBScrollPane(popupTable);
        showPopup(scrollPane, editor);

        WideMouseEventListenerHelper.getInstance().registerMouseEventListener(results, editor);
    }

    public void showHtmlNewTagLookupResults(WideQueryResult result, Editor editor) {
        // add an html editor kit
        HTMLEditorKit kit = new HTMLEditorKit();

        // add some styles to the html
        StyleSheet styleSheet = kit.getStyleSheet();
        try {
            styleSheet.importStyleSheet(WidePopupHelper.class.getResource("/MDNStyleSheet.css"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // create jeditorpane
        JEditorPane examplesEditorPane = new JEditorPane();

        // make it read-only
        examplesEditorPane.setEditable(false);
        examplesEditorPane.setContentType("text/html");

        // create a scrollpane; modify its attributes as desired
        JScrollPane examplesScrollPane = new JBScrollPane(examplesEditorPane);
        examplesEditorPane.setEditorKit(kit);

        // create some simple html as a string
        String htmlString = "<html><body>" + result.getMdn().getExamples() + "</body></html>";

        // create a document, set it on the jeditorpane, then add the html
        javax.swing.text.Document doc = kit.createDefaultDocument();
        examplesEditorPane.setDocument(doc);
        examplesEditorPane.setText(htmlString);

        showPopup(examplesScrollPane, editor);
    }

    public void showHtmlAttributeLookupResult(WideQueryResult result, Editor editor) {
        // add an html editor kit
        HTMLEditorKit kit = new HTMLEditorKit();

        // add some styles to the html
        StyleSheet styleSheet = kit.getStyleSheet();
        try {
            styleSheet.importStyleSheet(WidePopupHelper.class.getResource("/MDNStyleSheet.css"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // create jeditorpane
        JEditorPane examplesEditorPane = new JEditorPane();

        // make it read-only
        examplesEditorPane.setEditable(false);
        examplesEditorPane.setContentType("text/html");

        // create a scrollpane; modify its attributes as desired
        JScrollPane examplesScrollPane = new JBScrollPane(examplesEditorPane);
        examplesEditorPane.setEditorKit(kit);

        // create some simple html as a string
        String htmlString = "<html><body><h2>" + result.getKey() + "</h2>" + result.getInfo().replace("\n", "") + "</body></html>";

        // create a document, set it on the jeditorpane, then add the html
        javax.swing.text.Document doc = kit.createDefaultDocument();
        examplesEditorPane.setDocument(doc);
        examplesEditorPane.setText(htmlString);

        showPopup(examplesScrollPane, editor);
    }

    public void showJsLookupResults(List<WideQueryResult> results, Editor editor) {
        WideTableModel tableModel = new WideTableModel();
        tableModel.addColumn("Name");
        tableModel.addColumn("Type");
        tableModel.addColumn("Result");
        addResultsRecursive(tableModel, results);

        JTable popupTable = new JBTable(tableModel);
        popupTable.getColumn("Name").setCellRenderer(new WideTableCellRenderer());
        popupTable.getColumn("Type").setCellRenderer(new WideTableCellRenderer());
        popupTable.getColumn("Result").setCellRenderer(new WideTableCellRenderer());

        showPopup(popupTable, editor);
    }

    public void showCssLookupResults(List<WideQueryResult> results, Editor editor) {
        WideTableModel tableModel = new WideTableModel();
        tableModel.addColumn("Name");
        tableModel.addColumn("Type");
        tableModel.addColumn("Result");
        addResultsRecursive(tableModel, results);

        JTable popupTable = new JBTable(tableModel);
        popupTable.getColumn("Name").setCellRenderer(new WideTableCellRenderer());
        popupTable.getColumn("Type").setCellRenderer(new WideTableCellRenderer());
        popupTable.getColumn("Result").setCellRenderer(new WideTableCellRenderer());

        showPopup(popupTable, editor);
    }


    public void showError(String error, Editor editor) {
        //TODO: implementation
        showPopup(new JLabel(error), editor);
    }

    private void addResultsRecursive(DefaultTableModel tableModel, List<WideQueryResult> results) {
        for (WideQueryResult result : results) {
            if (result != null) {
                tableModel.addRow(result.getTableRow());
                addResultsRecursive(tableModel, result.getSubResults());
            }
        }
    }
}
