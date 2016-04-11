package ch.ethz.inf.globis.wide.action;

import ch.ethz.inf.globis.wide.parsing.WideQueryResult;
import ch.ethz.inf.globis.wide.parsing.css.WideCssHandler;
import ch.ethz.inf.globis.wide.parsing.html.WideHtmlHandler;
import ch.ethz.inf.globis.wide.parsing.javascript.WideJavascriptHandler;
import ch.ethz.inf.globis.wide.ui.popup.WidePopupHelper;
import ch.ethz.inf.globis.wide.ui.popup.WideTableCellRenderer;
import ch.ethz.inf.globis.wide.ui.popup.WideTableModel;
import ch.ethz.inf.globis.wide.ui.window.WideWindowFactory;
import com.intellij.lang.javascript.psi.*;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.actionSystem.EditorAction;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.ComponentPopupBuilder;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.psi.*;
import com.intellij.psi.css.CssElement;
import com.intellij.psi.xml.XmlElement;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.*;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import java.awt.*;
import java.net.URL;
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

                    @NotNull
                    PsiFile psiFile = PsiDocumentManager.getInstance(editor.getProject()).getPsiFile(editor.getDocument());
                    int start = selectionModel.getSelectionStart();
                    int end = selectionModel.getSelectionEnd() - 1;
                    PsiElement startElement = psiFile.findElementAt(start);
                    PsiElement endElement = psiFile.findElementAt(end);

                    List<WideQueryResult> results = new ArrayList<WideQueryResult>();

                    if (startElement.getParent() instanceof XmlElement && endElement.getParent() instanceof XmlElement) {
                        // DO HTML
                        System.out.println("HTML");
                        WideHtmlHandler handler = new WideHtmlHandler();
                        results = handler.handle(editor, psiFile, startElement, endElement, !(startElement instanceof PsiWhiteSpace));

                        Project project = editor.getProject();
                        ToolWindow window = ToolWindowManager.getInstance(project).getToolWindow("wIDE");

                        WideWindowFactory.createHTMLWindowContent(window, results.get(0));
                        //TODO: differentiate
                        WidePopupHelper.showHtmlTagLookupResults(results, editor);

                    } else if (startElement.getParent() instanceof JSElement && startElement.getParent() instanceof JSElement) {
                        // DO JS
                        System.out.println("JS");
                        WideJavascriptHandler handler = new WideJavascriptHandler();
                        results = handler.handle(editor, psiFile, startElement, endElement, true);

                        Project project = editor.getProject();
                        ToolWindow window = ToolWindowManager.getInstance(project).getToolWindow("wIDE");

                        WideWindowFactory.createJSWindowContent(window, results.get(0).getSubResults().get(0));
                        WidePopupHelper.showJsLookupResults(results, editor);

                    } else if (startElement.getParent() instanceof CssElement && startElement.getParent() instanceof CssElement) {
                        // DO CSS
                        System.out.println("CSS");
                        WideCssHandler handler = new WideCssHandler();
                        results = handler.handle(editor, psiFile, startElement, endElement, true);

                        WidePopupHelper.showCssLookupResults(results, editor);
                        //TODO: show results in window

                    } else if (startElement.getParent().getClass() != endElement.getParent().getClass()) {
                        // Mix of various Languages: Show message.
                        results = new ArrayList<WideQueryResult>();
                        results.add(new WideQueryResult("Please do not mix different languages."));

                    } else {
                        // Not supported language: Show message.

                        results.add(new WideQueryResult("This language is not supported."));
                    }
//                    showLookupResults(results, editor);
                }

            });
        }
    }

//    private static void addResultsRecursive(DefaultTableModel tableModel, List<WideQueryResult> results) {
//        for (WideQueryResult result : results) {
//            String prefix = StringUtils.repeat("    ", result.getLevel());
//            tableModel.addRow(result.getTableRow());
//            addResultsRecursive(tableModel, result.getSubResults());
//        }
//    }

//    private static void addResultsRecursive(DefaultListModel listModel, List<WideQueryResult> results) {
//        for(WideQueryResult result : results) {
//            String prefix = StringUtils.repeat("    ", result.getLevel());
//            listModel.addElement(prefix + result.getLookupName() + " (" + result.getLookupType() + "): " + result.getResponse());
//            //listModel.addElement(result);
//            addResultsRecursive(listModel, result.getSubResults());
//        }
//    }
}
