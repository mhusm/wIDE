package ch.ethz.inf.globis.wide.parsing.html;

import ch.ethz.inf.globis.wide.parsing.AbstractLanguageHandler;
import ch.ethz.inf.globis.wide.parsing.WideQueryResult;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;

import java.util.List;

/**
 * Created by fabian on 17.03.16.
 */
public class WideHtmlHandler implements AbstractLanguageHandler {
    public List<WideQueryResult> handle(Editor editor, PsiFile file, PsiElement startElement, PsiElement endElement) {
        return null;
    }
}
