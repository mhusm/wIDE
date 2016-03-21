package ch.ethz.inf.globis.wide.action;

import ch.ethz.inf.globis.wide.parsing.WideQueryResult;
import ch.ethz.inf.globis.wide.parsing.css.WideCssHandler;
import ch.ethz.inf.globis.wide.parsing.html.WideHtmlHandler;
import ch.ethz.inf.globis.wide.parsing.javascript.WideJavascriptHandler;
import ch.ethz.inf.globis.wide.ui.popup.WideTableCellRenderer;
import ch.ethz.inf.globis.wide.ui.popup.WideTableModel;
import com.intellij.lang.javascript.psi.*;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.editor.actionSystem.EditorAction;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.ComponentPopupBuilder;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.psi.*;
import com.intellij.psi.css.CssElement;
import com.intellij.psi.xml.XmlElement;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Fabian Stutz on 09.03.16.
 */
public class QueryAction extends EditorAction {

    protected QueryAction() {
        super(new QueryHandler());
    }


    @Override
    public void update(AnActionEvent e) {
        // WHEN SHOULD THE MENU BE VISIBLE?
        //Get required data keys
        final Project project = e.getData(CommonDataKeys.PROJECT);
        final Editor editor = e.getData(CommonDataKeys.EDITOR);

        //Set visibility only in case of existing project and editor and there is selected text
        e.getPresentation().setVisible((project != null && editor != null && editor.getSelectionModel().hasSelection()));
    }


    private static class QueryHandler extends EditorActionHandler {
        @Override
        public void doExecute(final Editor editor, final Caret caret, final DataContext dataContext) {
            ApplicationManager.getApplication().runWriteAction(new Runnable() {
                @Override
                public void run() {
                    SelectionModel selectionModel = editor.getSelectionModel();
                    //editor.getDocument().replaceString(selectionModel.getSelectionStart(), selectionModel.getSelectionEnd(), "fuuuu");

                    @NotNull
                    PsiFile psiFile = PsiDocumentManager.getInstance(editor.getProject()).getPsiFile(editor.getDocument());
                    int start = selectionModel.getSelectionStart();
                    int end = selectionModel.getSelectionEnd() - 1;
                    PsiElement startElement = psiFile.findElementAt(start);
                    PsiElement endElement = psiFile.findElementAt(end);

                    if (startElement.getParent() instanceof XmlElement && endElement.getParent() instanceof XmlElement) {
                        // DO HTML
                        System.out.println("HTML");
                        WideHtmlHandler handler = new WideHtmlHandler();
                        List<WideQueryResult> results = handler.handle(editor, psiFile, startElement, endElement);
                        showLookupResults(results, editor);

                    } else if (startElement.getParent() instanceof JSElement && startElement.getParent() instanceof JSElement) {
                        // DO JS
                        System.out.println("JS");
                        WideJavascriptHandler handler = new WideJavascriptHandler();
                        List<WideQueryResult> results = handler.handle(editor, psiFile, startElement, endElement);
                        showLookupResults(results, editor);

                    } else if (startElement.getParent() instanceof CssElement && startElement.getParent() instanceof CssElement) {
                        // DO CSS
                        System.out.println("CSS");
                        WideCssHandler handler = new WideCssHandler();
                        List<WideQueryResult> results = handler.handle(editor, psiFile, startElement, endElement);
                        showLookupResults(results, editor);

                    } else if (startElement.getParent().getClass() != endElement.getParent().getClass()) {
                        // Mix of various Languages: Show message.
                        List<WideQueryResult> results = new ArrayList<WideQueryResult>();
                        results.add(new WideQueryResult("Please do not mix different languages."));
                        showLookupResults(results, editor);

                    } else {
                        // Not supported language: Show message.
                        List<WideQueryResult> results = new ArrayList<WideQueryResult>();
                        results.add(new WideQueryResult("This language is not supported."));
                        showLookupResults(results, editor);
                    }
                }

            });
        }
    }

    private static void showLookupResults(List<WideQueryResult> results, Editor editor) {
        DefaultListModel listModel = new DefaultListModel();
        WideTableModel tableModel = new WideTableModel();
        tableModel.addColumn("Name");
        tableModel.addColumn("Type");
        tableModel.addColumn("Result");
        addResultsRecursive(tableModel, results);

        JList popupList = new JList(listModel);
        JTable popupTable = new JTable(tableModel);
        popupTable.getColumn("Name").setCellRenderer(new WideTableCellRenderer());
        popupTable.getColumn("Type").setCellRenderer(new WideTableCellRenderer());
        popupTable.getColumn("Result").setCellRenderer(new WideTableCellRenderer());

        ComponentPopupBuilder popupBuilder = JBPopupFactory.getInstance().createComponentPopupBuilder(popupTable, editor.getComponent());
        popupBuilder.setTitle("Lookup Results");
        JBPopup popup = popupBuilder.createPopup();
        popup.setSize(new Dimension(600, 100));
        popup.show(JBPopupFactory.getInstance().guessBestPopupLocation(editor));
    }

    private static void addResultsRecursive(DefaultTableModel tableModel, List<WideQueryResult> results) {
        for(WideQueryResult result : results) {
            String prefix = StringUtils.repeat("    ", result.getLevel());
            tableModel.addRow(result.getTableRow());
            addResultsRecursive(tableModel, result.getSubResults());
        }
    }

    private static void addResultsRecursive(DefaultListModel listModel, List<WideQueryResult> results) {
        for(WideQueryResult result : results) {
            String prefix = StringUtils.repeat("    ", result.getLevel());
            listModel.addElement(prefix + result.getLookupName() + " (" + result.getLookupType() + "): " + result.getResponse());
            //listModel.addElement(result);
            addResultsRecursive(listModel, result.getSubResults());
        }
    }
}
