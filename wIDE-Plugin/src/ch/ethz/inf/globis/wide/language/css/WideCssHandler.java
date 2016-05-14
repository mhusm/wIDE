package ch.ethz.inf.globis.wide.language.css;

import ch.ethz.inf.globis.wide.language.IWideLanguageHandler;
import ch.ethz.inf.globis.wide.lookup.io.WideQueryResponse;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import javafx.embed.swing.JFXPanel;

/**
 * Created by fabian on 12.05.16.
 */
public class WideCssHandler implements IWideLanguageHandler {
    @Override
    public WideCssPopupHelper getPopupHelper() {
        return WideCssPopupHelper.getInstance();
    }

    @Override
    public WideCssWindowFactory getWindowFactory() {
        return WideCssWindowFactory.getInstance();
    }

    @Override
    public WideCssSuggestionCell getSuggestionCell(JFXPanel panel) {
        return new WideCssSuggestionCell(panel);
    }

    @Override
    public WideCssParser getLanguageParser() {
        return new WideCssParser();
    }

    @Override
    public WideQueryResponse lookupDocumentation(Editor editor, PsiFile file, PsiElement startElement, PsiElement endElement) {
        return null;
    }

    @Override
    public void lookupSuggestions(Editor editor, PsiElement element, String newChar) {

    }
}
