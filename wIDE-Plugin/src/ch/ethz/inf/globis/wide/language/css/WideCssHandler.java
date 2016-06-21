package ch.ethz.inf.globis.wide.language.css;

import ch.ethz.inf.globis.wide.communication.WideHttpCommunicator;
import ch.ethz.inf.globis.wide.language.IWideLanguageHandler;
import ch.ethz.inf.globis.wide.io.query.WideQueryRequest;
import ch.ethz.inf.globis.wide.io.query.WideQueryResponse;
import com.intellij.codeInsight.completion.PrioritizedLookupElement;
import com.intellij.codeInsight.lookup.Lookup;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.ide.IdeEventQueue;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.css.CssDeclaration;
import com.intellij.psi.css.CssElement;

/**
 * Created by fabian on 12.05.16.
 */
public class WideCssHandler implements IWideLanguageHandler {
    @Override
    public WideCssPopupFactory getPopupHelper() {
        return WideCssPopupFactory.getInstance();
    }

    @Override
    public WideCssWindowFactory getWindowFactory() {
        return WideCssWindowFactory.getInstance();
    }

    @Override
    public WideCssParser getLanguageParser() {
        return new WideCssParser();
    }

    @Override
    public boolean isRelevantForCompatibilityQuery(PsiElement element) {
        return (element instanceof CssElement && !(element instanceof CssDeclaration));
    }

    @Override
    public String getLanguageAbbreviation() {
        return "CSS";
    }

    @Override
    public WideQueryResponse lookupDocumentation(Editor editor, PsiFile file, PsiElement startElement, PsiElement endElement) {
        WideQueryRequest request = getLanguageParser().buildDocumentationQuery(editor, file, startElement, endElement);
        WideQueryResponse response = WideHttpCommunicator.sendRequest(request);


        Project project = editor.getProject();
        ToolWindow window = ToolWindowManager.getInstance(project).getToolWindow("wIDE");

        if (response != null) {
            getWindowFactory().showLookupWindow(window, response);
        } else {
            getWindowFactory().showErrorWindow("Sorry, this element is not supported.", window);
        }

        return response;
    }

    @Override
    public void getSuggestionDocumentation(LookupElement lookupElement, Lookup lookup) {

            if (lookup.getPsiElement().getPrevSibling() != null
                    && lookup.getPsiElement().getPrevSibling() instanceof CssDeclaration) {
                // attribute value

            } else if (lookupElement instanceof PrioritizedLookupElement){
                // attribute

                WideQueryRequest request = new WideQueryRequest();
                request.setLang(getLanguageAbbreviation());
                request.setType("attribute");
                request.setKey(lookupElement.getLookupString());
                        WideQueryResponse response = WideHttpCommunicator.sendRequest(request);

                        Project project = lookup.getEditor().getProject();
                        ToolWindow window = ToolWindowManager.getInstance(project).getToolWindow("wIDE");

                        if (response != null) {
                            getWindowFactory().showLookupWindow(window, response);
                        } else {
                            getWindowFactory().showErrorWindow("No documentation found.", window);
                        }

            } else {
                //TODO: empty window
            }

        //TODO: pseudo-selectors
        //TODO: selectors
    }
}
