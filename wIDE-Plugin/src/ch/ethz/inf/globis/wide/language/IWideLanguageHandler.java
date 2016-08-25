package ch.ethz.inf.globis.wide.language;

import ch.ethz.inf.globis.wide.io.query.WideQueryRequest;
import ch.ethz.inf.globis.wide.io.query.WideQueryResponse;
import ch.ethz.inf.globis.wide.ui.components.popup.WidePopupFactory;
import ch.ethz.inf.globis.wide.ui.components.window.WideWindowFactory;
import com.intellij.codeInsight.lookup.Lookup;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;

/**
 * Created by fabian on 12.05.16.
 */
public interface IWideLanguageHandler {
    WidePopupFactory getPopupFactory();
    WideWindowFactory getWindowFactory();
    IWideLanguageParser getLanguageParser();
    String getLanguageAbbreviation();

    WideQueryRequest getDocumentationRequest(Editor editor, PsiFile file, PsiElement startElement, PsiElement endElement);
    WideQueryRequest getSuggestionRequest(LookupElement element, PsiElement psiElement, Lookup lookup);

    void showDocumentationResults(WideQueryResponse response, PsiElement selectedElement, Editor editor, ToolWindow window);
    void showSuggestionResults(WideQueryResponse response, Editor editor, ToolWindow toolWindow);
}
