package ch.ethz.inf.globis.wide.lookup;

import ch.ethz.inf.globis.wide.logging.WideLogger;
import ch.ethz.inf.globis.wide.ui.popup.WidePopupHelper;
import ch.ethz.inf.globis.wide.ui.window.WideWindowFactory;
import com.intellij.lang.javascript.psi.JSElement;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.css.CssElement;
import com.intellij.psi.xml.XmlElement;

import java.util.List;

/**
 * Created by fabian on 20.04.16.
 */
public class WideSuggestionHandler implements IWideLookupHandler {
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
        //TODO
    }

    public void handleJS(Editor editor, PsiFile psiFile, PsiElement startElement, PsiElement endElement) {
        //TODO
    }

    public void handleCSS(Editor editor, PsiFile psiFile, PsiElement startElement, PsiElement endElement) {
        //TODO

    }

    public void handleError(Editor editor, String error) {
        WidePopupHelper.getInstance().showError(error, editor);

        Project project = editor.getProject();
        ToolWindow window = ToolWindowManager.getInstance(project).getToolWindow("wIDE");
        WideWindowFactory.createErrorWindowContent(window, error);

        LOGGER.warning(error);
    }
}
