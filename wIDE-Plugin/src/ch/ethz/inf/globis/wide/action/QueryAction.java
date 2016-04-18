package ch.ethz.inf.globis.wide.action;

import ch.ethz.inf.globis.wide.logging.WideLogger;
import ch.ethz.inf.globis.wide.parsing.WideQueryResult;
import ch.ethz.inf.globis.wide.parsing.css.WideCssHandler;
import ch.ethz.inf.globis.wide.parsing.html.WideHtmlHandler;
import ch.ethz.inf.globis.wide.parsing.javascript.WideJavascriptHandler;
import ch.ethz.inf.globis.wide.ui.popup.WidePopupHelper;
import ch.ethz.inf.globis.wide.ui.window.WideHtmlWindowFactory;
import ch.ethz.inf.globis.wide.ui.window.WideJSWindowFactory;
import ch.ethz.inf.globis.wide.ui.window.WideWindowFactory;
import com.intellij.lang.javascript.psi.JSElement;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.actionSystem.EditorAction;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.psi.*;
import com.intellij.psi.css.CssElement;
import com.intellij.psi.xml.XmlElement;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.logging.Logger;


/**
 * Created by Fabian Stutz on 09.03.16.
 */
public class QueryAction extends EditorAction {

    private final static WideLogger LOGGER = new WideLogger(QueryAction.class.getName());

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

                    if (editor.getProject() != null) {
                        @NotNull
                        PsiFile psiFile = PsiDocumentManager.getInstance(editor.getProject()).getPsiFile(editor.getDocument());
                        int start = selectionModel.getSelectionStart();
                        int end = selectionModel.getSelectionEnd() - 1;
                        PsiElement startElement = psiFile.findElementAt(start);
                        PsiElement endElement = psiFile.findElementAt(end);

                        differentiateLanguages(editor, psiFile, startElement, endElement);
                    } else {
                        LOGGER.info("Invalid place to start a query.");
                    }
                }

            });
        }

        public void differentiateLanguages(Editor editor, PsiFile psiFile, PsiElement startElement, PsiElement endElement) {
            if (startElement.getParent() instanceof XmlElement && endElement.getParent() instanceof XmlElement) {
                // handle HTML
                handleHtml(editor, psiFile, startElement, endElement);

            } else if (startElement.getParent() instanceof JSElement && startElement.getParent() instanceof JSElement) {
                // handle JS
                handleJS(editor, psiFile, startElement, endElement);

            } else if (startElement.getParent() instanceof CssElement && startElement.getParent() instanceof CssElement) {
                // handle CSS
                handleCSS(editor, psiFile, startElement, endElement);

            } else if (startElement.getParent().getClass() != endElement.getParent().getClass()) {
                // Mix of various Languages: Show message.
                handleError(editor, "Please do not mix different languages.");

            } else {
                // Not supported language: Show message.
                handleError(editor, "This language is not supported.");
            }
        }

        public void handleHtml(Editor editor, PsiFile psiFile, PsiElement startElement, PsiElement endElement) {
            // DO HTML
            List<WideQueryResult> results;
            WideHtmlHandler handler = new WideHtmlHandler();
            results = handler.handle(editor, psiFile, startElement, endElement, !(startElement instanceof PsiWhiteSpace));

            Project project = editor.getProject();
            ToolWindow window = ToolWindowManager.getInstance(project).getToolWindow("wIDE");

            WideHtmlWindowFactory.createHTMLWindowContent(window, results.get(0));
            //TODO: differentiate
            WidePopupHelper.getInstance().showHtmlTagLookupResults(results, editor);
        }

        public void handleJS(Editor editor, PsiFile psiFile, PsiElement startElement, PsiElement endElement) {
            // DO JS
            List<WideQueryResult> results;
            WideJavascriptHandler handler = new WideJavascriptHandler();
            results = handler.handle(editor, psiFile, startElement, endElement, true);

            Project project = editor.getProject();
            ToolWindow window = ToolWindowManager.getInstance(project).getToolWindow("wIDE");

            WideJSWindowFactory.createJSWindowContent(window, results.get(0).getSubResults().get(0), project);
            WidePopupHelper.getInstance().showJsLookupResults(results, editor);
        }

        public void handleCSS(Editor editor, PsiFile psiFile, PsiElement startElement, PsiElement endElement) {
            // DO CSS
            List<WideQueryResult> results;
            WideCssHandler handler = new WideCssHandler();
            results = handler.handle(editor, psiFile, startElement, endElement, true);

            WidePopupHelper.getInstance().showCssLookupResults(results, editor);
            //TODO: show results in window
        }

        public void handleError(Editor editor, String error) {
            WidePopupHelper.getInstance().showError(error, editor);

            Project project = editor.getProject();
            ToolWindow window = ToolWindowManager.getInstance(project).getToolWindow("wIDE");
            WideWindowFactory.createErrorWindowContent(window, error);

            LOGGER.warning(error);
        }
    }
}
