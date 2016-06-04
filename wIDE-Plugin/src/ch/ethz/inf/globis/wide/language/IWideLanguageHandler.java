package ch.ethz.inf.globis.wide.language;

import ch.ethz.inf.globis.wide.io.query.WideQueryResponse;
import ch.ethz.inf.globis.wide.ui.components.popup.WidePopupHelper;
import ch.ethz.inf.globis.wide.ui.components.window.WideWindowFactory;
import com.intellij.codeInsight.lookup.Lookup;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;

/**
 * Created by fabian on 12.05.16.
 */
public interface IWideLanguageHandler {
    WidePopupHelper getPopupHelper();
    WideWindowFactory getWindowFactory();
    IWideLanguageParser getLanguageParser();

    WideQueryResponse lookupDocumentation(Editor editor, PsiFile file, PsiElement startElement, PsiElement endElement);
   // void lookupSuggestions(Editor editor, PsiElement element, String newChar);
    void getSuggestionDocumentation(LookupElement lookupElement, Lookup lookup);
}