package ch.ethz.inf.globis.wide.lookup;

import ch.ethz.inf.globis.wide.logging.WideLogger;
import ch.ethz.inf.globis.wide.registry.WideLanguageRegistry;
import ch.ethz.inf.globis.wide.language.IWideLanguageHandler;
import com.intellij.lang.javascript.psi.JSElement;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;

/**
 * Created by fabian on 20.04.16.
 */
public class WideDocumentationHandler {

    private final static WideLogger LOGGER = new WideLogger(WideDocumentationHandler.class.getName());

    public void differentiateLanguages(Editor editor, PsiFile psiFile, PsiElement startElement, PsiElement endElement) {

        if (startElement.getParent().getClass().equals(endElement.getParent().getClass())) {
            IWideLanguageHandler languageHandler = WideLanguageRegistry.getInstance().getLanguageHandler(startElement.getParent().getClass());

            if (languageHandler == null) {
                // Not supported language: Show message.
                handleError(editor, "This language is not supported.");
            } else {
                languageHandler.lookupDocumentation(editor, psiFile, startElement, endElement);
            }

        } else {
            // Mix of various Languages: Show message.
            handleError(editor, "Please do not mix different languages.");
        }
    }

    public void handleHtml(Editor editor, PsiFile psiFile, PsiElement startElement, PsiElement endElement) {
//        // DO HTML
//        WideHtmlParser parser = new WideHtmlParser();
//        WideQueryRequest request = parser.handle(editor, psiFile, startElement, endElement);
//
//        WideQueryResponse response = WideHttpCommunicator.sendRequest(request);
//
//        Project project = editor.getProject();
//        ToolWindow window = ToolWindowManager.getInstance(project).getToolWindow("wIDE");
//
//        WideHtmlWindowFactory.createHTMLWindowContent(window, results.get(0));
    }

    public void handleJS(Editor editor, PsiFile psiFile, PsiElement startElement, PsiElement endElement) {
        // DO JS
    }

    public void handleCSS(Editor editor, PsiFile psiFile, PsiElement startElement, PsiElement endElement) {
        // DO CSS
//        List<WideQueryResponse> results;
//        WideCssParser handler = new WideCssParser();
//        results = handler.handle(editor, psiFile, startElement, endElement, true);
//
//        Project project = editor.getProject();
//        ToolWindow window = ToolWindowManager.getInstance(project).getToolWindow("wIDE");
//
//        WidePopupHelper.getInstance().showCssLookupResults(results, editor);
//        WideCSSWindowFactory.createCSSWindowContent(window, results.get(0));
    }

    public void handleError(Editor editor, String error) {
//        WidePopupHelper.getInstance().showError(error, editor);
//
//        Project project = editor.getProject();
//        ToolWindow window = ToolWindowManager.getInstance(project).getToolWindow("wIDE");
//        WideWindowFactory.createErrorWindowContent(window, error);

        LOGGER.warning(error);
    }
}
