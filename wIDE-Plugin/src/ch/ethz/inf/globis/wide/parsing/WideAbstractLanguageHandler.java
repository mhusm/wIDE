package ch.ethz.inf.globis.wide.parsing;

import ch.ethz.inf.globis.wide.lookup.response.WideQueryResponse;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;

import java.util.List;

/**
 * Created by fabian on 17.03.16.
 */
public interface WideAbstractLanguageHandler {
    public List<WideQueryResponse> handle(Editor editor, PsiFile file, PsiElement startElement, PsiElement endElement, boolean isFinished);
}
