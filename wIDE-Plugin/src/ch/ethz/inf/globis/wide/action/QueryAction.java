package ch.ethz.inf.globis.wide.action;

import ch.ethz.inf.globis.wide.parsing.WideQueryResult;
import ch.ethz.inf.globis.wide.parsing.css.WideCssHandler;
import ch.ethz.inf.globis.wide.parsing.html.WideHtmlHandler;
import ch.ethz.inf.globis.wide.parsing.javascript.WideJavascriptHandler;
import com.intellij.lang.javascript.psi.*;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.editor.actionSystem.EditorAction;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.PopupChooserBuilder;
import com.intellij.psi.*;
import com.intellij.psi.css.CssFile;
import com.intellij.psi.xml.XmlFile;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
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

                    if (psiFile instanceof XmlFile) {
                        // HTML
                        System.out.println("HTML File");
                        WideHtmlHandler handler = new WideHtmlHandler();
                        List<WideQueryResult> results = handler.handle(editor, psiFile, startElement, endElement);
                        showLookupResults(results, editor);

                    } else if (psiFile instanceof CssFile) {
                        // CSS
                        System.out.println("CSS File");
                        WideCssHandler handler = new WideCssHandler();
                        List<WideQueryResult> results = handler.handle(editor, psiFile, startElement, endElement);
                        showLookupResults(results, editor);

                    } else if (psiFile.getClass().getSimpleName().equals("PhpFileImpl")) {
                        // PHP

                        //TODO: use instanceof
                        System.out.println("PHP File");
                        //TODO: should we use a specific PHP handler here?
                        WideHtmlHandler handler = new WideHtmlHandler();
                        List<WideQueryResult> results = handler.handle(editor, psiFile, startElement, endElement);
                        showLookupResults(results, editor);

                    } else if (psiFile instanceof JSFile) {
                        // JavaScript
                        System.out.println("JavaScript File");
                        WideJavascriptHandler handler = new WideJavascriptHandler();
                        List<WideQueryResult> results = handler.handle(editor, psiFile, startElement, endElement);
                        showLookupResults(results, editor);
                    }
                }

            });
        }
    }

    private static void showLookupResults(List<WideQueryResult> results, Editor editor) {
        DefaultListModel functionModel = new DefaultListModel();
        for(WideQueryResult result : results) {
            functionModel.addElement(result.getFileName() + ": " + result.getLookupName() + " " + result.getResponse());
        }
        JList popupList = new JList(functionModel);

        PopupChooserBuilder popupBuilder = JBPopupFactory.getInstance().createListPopupBuilder(popupList);
        JBPopup popup = popupBuilder.createPopup();
        popup.setSize(new Dimension(400, 100));
        popup.show(JBPopupFactory.getInstance().guessBestPopupLocation(editor));
    }
}
