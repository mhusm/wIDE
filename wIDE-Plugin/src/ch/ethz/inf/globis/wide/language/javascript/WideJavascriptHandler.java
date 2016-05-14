package ch.ethz.inf.globis.wide.language.javascript;

import ch.ethz.inf.globis.wide.communication.WideHttpCommunicator;
import ch.ethz.inf.globis.wide.language.IWideLanguageHandler;
import ch.ethz.inf.globis.wide.lookup.io.WideQueryRequest;
import ch.ethz.inf.globis.wide.lookup.io.WideQueryResponse;
import ch.ethz.inf.globis.wide.ui.components.list.WideSuggestionCell;
import ch.ethz.inf.globis.wide.ui.components.window.WideWindowFactory;
import com.intellij.lang.javascript.psi.JSBlockStatement;
import com.intellij.lang.javascript.psi.JSCallExpression;
import com.intellij.lang.javascript.psi.JSFunction;
import com.intellij.lang.javascript.psi.JSReferenceExpression;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import javafx.embed.swing.JFXPanel;

/**
 * Created by fabian on 12.05.16.
 */
public class WideJavascriptHandler implements IWideLanguageHandler{
    @Override
    public WideJSPopupHelper getPopupHelper() {
        return WideJSPopupHelper.getInstance();
    }

    @Override
    public WideWindowFactory getWindowFactory() {
        return WideJSWindowFactory.getInstance();
    }

    @Override
    public WideSuggestionCell getSuggestionCell(JFXPanel panel) {
        return new WideJavascriptSuggestionCell(panel);
    }

    @Override
    public WideJavascriptParser getLanguageParser() {
        return new WideJavascriptParser();
    }

    @Override
    public WideQueryResponse lookupDocumentation(Editor editor, PsiFile file, PsiElement startElement, PsiElement endElement) {
        WideQueryResponse response = null;

        WideQueryRequest request = getLanguageParser().buildDocumentationQuery(editor, file, startElement, endElement);
        response = WideHttpCommunicator.sendRequest(request);

        Project project = editor.getProject();
        ToolWindow window = ToolWindowManager.getInstance(project).getToolWindow("wIDE");

        if (response.getSubResults().size() > 0) {
            getWindowFactory().showLookupWindow(window, response);
            getPopupHelper().showLookupResults(response, null, editor);
        } else {
            //TODO: no results found.
        }

        return response;
    }

    @Override
    public void lookupSuggestions(Editor editor, PsiElement element, String newChar) {
        WideQueryRequest request = new WideQueryRequest();
        request.setLang("JS");

        if (element.getParent().getParent() instanceof JSCallExpression) {
            System.out.println("Writing JS call to receiver " + element.getParent().getParent().getFirstChild().getText());
            System.out.println(element.getParent().getParent().getText());
            request.setValue(element.getParent().getParent().getText());
            request.setKey(element.getParent().getParent().getFirstChild().getText());
            request.setType("callCandidate");

        } else if (element.getParent() instanceof JSReferenceExpression) {
            if (newChar.equals(".")) {
                System.out.println("Writing JS call to receiver " + element.getParent().getText());
                request.setKey(element.getParent().getText());
                request.setValue("");
                request.setType("callCandidate");
            } else {
                System.out.println("Writing JS reference");
                request.setKey(element.getParent().getText());
                request.setType("callCandidate");
            }
        } else if (element.getParent() instanceof JSBlockStatement) {
            System.out.println("Writing JS BlockStatement");

        } else if (element.getParent() instanceof JSFunction) {
            System.out.println("Writing JS Function");

        } else {
            //System.out.println("Unknown element: " + element.getParent().getClass());
        }

        WideQueryResponse response = WideHttpCommunicator.sendSuggestionRequest(request);

        Project project = editor.getProject();
        ToolWindow window = ToolWindowManager.getInstance(project).getToolWindow("wIDE");

        getPopupHelper().showSuggestions(response.getSubResults(), window, element, editor);
    }
}
