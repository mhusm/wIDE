package ch.ethz.inf.globis.wide.language.javascript;

import ch.ethz.inf.globis.wide.communication.WideHttpCommunicator;
import ch.ethz.inf.globis.wide.language.IWideLanguageHandler;
import ch.ethz.inf.globis.wide.io.query.WideQueryRequest;
import ch.ethz.inf.globis.wide.io.query.WideQueryResponse;
import ch.ethz.inf.globis.wide.ui.components.window.WideWindowFactory;
import com.intellij.codeInsight.lookup.Lookup;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupItem;
import com.intellij.ide.IdeEventQueue;
import com.intellij.lang.javascript.psi.*;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;

/**
 * Created by fabian on 12.05.16.
 */
public class WideJavascriptHandler implements IWideLanguageHandler{
    @Override
    public WideJSPopupFactory getPopupHelper() {
        return WideJSPopupFactory.getInstance();
    }

    @Override
    public WideWindowFactory getWindowFactory() {
        return WideJSWindowFactory.getInstance();
    }

    @Override
    public WideJavascriptParser getLanguageParser() {
        return new WideJavascriptParser();
    }

    @Override
    public boolean isRelevantForCompatibilityQuery(PsiElement element) {
        return element instanceof JSCallExpression;
    }

    @Override
    public String getLanguageAbbreviation() {
        return "JS";
    }

    @Override
    public WideQueryResponse lookupDocumentation(Editor editor, PsiFile file, PsiElement startElement, PsiElement endElement) {
        WideQueryResponse response = null;

        WideQueryRequest request = getLanguageParser().buildDocumentationQuery(editor, file, startElement, endElement);
        Project project = editor.getProject();
        ToolWindow window = ToolWindowManager.getInstance(project).getToolWindow("wIDE");

        if (request != null) {
            // is a valid function call
            response = WideHttpCommunicator.sendRequest(request);
            getWindowFactory().showLookupWindow(window, response);
        } else {
            // is not a valid function call -> show error
            getWindowFactory().showErrorWindow("Sorry, this element is not supported.", window);
        }

        return response;
    }

    @Override
    public void getSuggestionDocumentation(LookupElement lookupElement, Lookup lookup) {
        WideQueryRequest request = new WideQueryRequest();
        request.setLang(getLanguageAbbreviation());
        request.setType("call");
        request.setKey(((LookupItem) lookupElement).getPresentableText());

        if (lookup.getPsiElement().getParent() instanceof JSReferenceExpression) {
           WideQueryRequest subRequest = new WideQueryRequest();
            subRequest.setType("callCandidate");

            subRequest.setKey(lookupElement.getPsiElement().getLastChild().getLastChild().getText());
            subRequest.setValue("{" +
                    "     \"receiver\": \"" + lookup.getPsiElement().getParent().getFirstChild().getText() + "\", " +
                    "     \"file\": \"" + lookupElement.getPsiElement().getContainingFile().getName() + "\"}");
            request.addChild(subRequest);

        } else if (lookupElement.getPsiElement().getParent().getLastChild() instanceof JSFunctionExpression) {
           request.setKey(lookupElement.getPsiElement().getText());

            WideQueryRequest subRequest = new WideQueryRequest();
            subRequest.setType("callCandidate");

            subRequest.setKey(lookupElement.getPsiElement().getText());
            subRequest.setValue("{" +
                    "     \"receiver\": \"" + lookupElement.getPsiElement().getLastChild().getLastChild().getText() + "\", " +
                    "     \"file\": \"" + lookupElement.getPsiElement().getContainingFile().getName() + "\"}");
            request.addChild(subRequest);

        } else if (lookupElement.getPsiElement().getParent().getLastChild() instanceof JSReferenceExpression) {
            // TODO: allow reference infos as well?
        }

                WideQueryResponse response = WideHttpCommunicator.sendRequest(request);

                Project project = lookup.getEditor().getProject();
                ToolWindow window = ToolWindowManager.getInstance(project).getToolWindow("wIDE");

                IdeEventQueue.getInstance().doWhenReady(new Runnable() {
                    @Override
                    public void run() {

                        if (response.getSubResults().size() > 0) {
                            for (WideQueryResponse resp : response.getSubResults()) {
                                if (resp.getDocumentation("mdn") != null) {
                                    getWindowFactory().showLookupWindow(window, resp);
                                    return;
                                    //getPopupHelper().showLookupResults(resp, null, editor);
                                }
                            }
                        }

                        getWindowFactory().showErrorWindow("No documentation found.", window);
                    }
                });

    }
}
