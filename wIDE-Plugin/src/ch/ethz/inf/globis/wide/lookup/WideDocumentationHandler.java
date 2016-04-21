package ch.ethz.inf.globis.wide.lookup;

import ch.ethz.inf.globis.wide.logging.WideLogger;
import ch.ethz.inf.globis.wide.lookup.response.WideQueryResponse;
import ch.ethz.inf.globis.wide.parsing.css.WideCssHandler;
import ch.ethz.inf.globis.wide.parsing.html.WideHtmlHandler;
import ch.ethz.inf.globis.wide.parsing.javascript.WideJavascriptHandler;
import ch.ethz.inf.globis.wide.ui.popup.WidePopupHelper;
import ch.ethz.inf.globis.wide.ui.window.WideCSSWindowFactory;
import ch.ethz.inf.globis.wide.ui.window.WideHtmlWindowFactory;
import ch.ethz.inf.globis.wide.ui.window.WideJSWindowFactory;
import ch.ethz.inf.globis.wide.ui.window.WideWindowFactory;
import com.intellij.lang.javascript.psi.JSElement;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.css.CssElement;
import com.intellij.psi.xml.XmlElement;

import java.util.List;

/**
 * Created by fabian on 20.04.16.
 */
public class WideDocumentationHandler implements IWideLookupHandler {

    private final static WideLogger LOGGER = new WideLogger(WideDocumentationHandler.class.getName());

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
        List<WideQueryResponse> results;
        WideHtmlHandler handler = new WideHtmlHandler();
        results = handler.handle(editor, psiFile, startElement, endElement, !(startElement instanceof PsiWhiteSpace));

        Project project = editor.getProject();
        ToolWindow window = ToolWindowManager.getInstance(project).getToolWindow("wIDE");

        WideHtmlWindowFactory.createHTMLWindowContent(window, results.get(0));
        //TODO: differentiate
        //WidePopupHelper.getInstance().showHtmlTagLookupResults(results, editor);
    }

    public void handleJS(Editor editor, PsiFile psiFile, PsiElement startElement, PsiElement endElement) {
        // DO JS
        List<WideQueryResponse> results;
        WideJavascriptHandler handler = new WideJavascriptHandler();
        results = handler.handle(editor, psiFile, startElement, endElement, true);

        Project project = editor.getProject();
        ToolWindow window = ToolWindowManager.getInstance(project).getToolWindow("wIDE");

        if (results.get(0).getSubResults().size() > 0) {
            WideJSWindowFactory.createJSWindowContent(window, results.get(0).getSubResults().get(0), project);
            WidePopupHelper.getInstance().showJsLookupResults(results, editor);
        } else {
            //TODO: no results found.
        }
    }

    public void handleCSS(Editor editor, PsiFile psiFile, PsiElement startElement, PsiElement endElement) {
        // DO CSS
        List<WideQueryResponse> results;
        WideCssHandler handler = new WideCssHandler();
        results = handler.handle(editor, psiFile, startElement, endElement, true);

        Project project = editor.getProject();
        ToolWindow window = ToolWindowManager.getInstance(project).getToolWindow("wIDE");

        WidePopupHelper.getInstance().showCssLookupResults(results, editor);
        WideCSSWindowFactory.createCSSWindowContent(window, results.get(0));
    }

    public void handleError(Editor editor, String error) {
        WidePopupHelper.getInstance().showError(error, editor);

        Project project = editor.getProject();
        ToolWindow window = ToolWindowManager.getInstance(project).getToolWindow("wIDE");
        WideWindowFactory.createErrorWindowContent(window, error);

        LOGGER.warning(error);
    }
}
