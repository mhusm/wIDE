package ch.ethz.inf.globis.wide.language.javascript;

import ch.ethz.inf.globis.wide.communication.WideHttpCommunicator;
import ch.ethz.inf.globis.wide.language.IWideLanguageHandler;
import ch.ethz.inf.globis.wide.lookup.io.WideQueryRequest;
import ch.ethz.inf.globis.wide.lookup.io.WideQueryResponse;
import ch.ethz.inf.globis.wide.ui.components.list.WideSuggestionCell;
import ch.ethz.inf.globis.wide.ui.components.window.WideWindowFactory;
import com.intellij.codeInsight.lookup.Lookup;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupItem;
import com.intellij.lang.javascript.psi.*;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.openapi.wm.impl.SystemDock;
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
    public WideJavascriptParser getLanguageParser() {
        return new WideJavascriptParser();
    }

    @Override
    public WideQueryResponse lookupDocumentation(Editor editor, PsiFile file, PsiElement startElement, PsiElement endElement) {
        WideQueryResponse response = null;
        boolean hasValidResult = false;

        WideQueryRequest request = getLanguageParser().buildDocumentationQuery(editor, file, startElement, endElement);
        if (request != null) {
            response = WideHttpCommunicator.sendRequest(request);

            Project project = editor.getProject();
            ToolWindow window = ToolWindowManager.getInstance(project).getToolWindow("wIDE");

            if (response.getSubResults().size() > 0) {
                for (WideQueryResponse resp : response.getSubResults()) {
                    if (resp.getMdn() != null) {
                        getWindowFactory().showLookupWindow(window, resp);
                        //getPopupHelper().showLookupResults(resp, null, editor);
                        hasValidResult = true;
                    }
                }
            }
        }

        if (!hasValidResult) {
            // no valid result -> (assume) invalid selection
            Project project = editor.getProject();
            ToolWindow window = ToolWindowManager.getInstance(project).getToolWindow("wIDE");
            getWindowFactory().createErrorWindowContent("\n" +
                    " Sorry, we did not find \n" +
                    " any documentation \n" +
                    " for the selection.", window);
        }

        return response;
    }

    @Override
    public void lookupSuggestions(Editor editor, PsiElement element, String newChar) {
        WideQueryRequest request = new WideQueryRequest();
        request.setLang("JS");

        if (newChar != "") {
            // writing a char
            if (element.getText().replace("\n", "").replace(" ", "").equals("")) {
                // started to newly write a call or reference
                System.out.println("Writing JS call or reference.");
                request.setKey(newChar);

            } else if (element.getParent().getParent() instanceof JSCallExpression) {
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
                    request.setKey(element.getParent().getText() + newChar);
                    request.setType("callCandidate");
                }
            } else if (element.getParent() instanceof JSBlockStatement) {
                System.out.println("Writing JS BlockStatement");

            } else if (element.getParent() instanceof JSFunction) {
                System.out.println("Writing JS Function");

            } else {
                //System.out.println("Unknown element: " + element.getParent().getClass());
            }

        } else {
            // deleting a char
            //TODO: implementation
        }

        WideQueryResponse response = WideHttpCommunicator.sendSuggestionRequest(request);

        Project project = editor.getProject();
        ToolWindow window = ToolWindowManager.getInstance(project).getToolWindow("wIDE");

        getPopupHelper().showSuggestions(response.getSubResults(), window, element, editor);
    }

    @Override
    public void getSuggestionDocumentation(LookupElement lookupElement, Lookup lookup) {
        WideQueryRequest request = new WideQueryRequest();
        request.setLang("JS");
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
//            System.out.println("reference expression: " + lookupElement.getPsiElement().getText());
//            request.setKey(lookupElement.getPsiElement().getText());
//            request.setType("callCandidate");

            // TODO: allow reference infos as well?
        }

        WideQueryResponse response = WideHttpCommunicator.sendRequest(request);

        Project project = lookupElement.getPsiElement().getProject();
        ToolWindow window = ToolWindowManager.getInstance(project).getToolWindow("wIDE");

        if (response.getSubResults().size() > 0) {
            for (WideQueryResponse resp : response.getSubResults()) {
                if (resp.getMdn() != null) {
                    getWindowFactory().showLookupWindow(window, resp);
                    //getPopupHelper().showLookupResults(resp, null, editor);
                }
            }
        }

        //getWindowFactory().showLookupWindow(window, response.getSubResults().get(0));

    }
}
