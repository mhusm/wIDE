package ch.ethz.inf.globis.wide.language;

import ch.ethz.inf.globis.wide.io.query.WideQueryRequest;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;

/**
 * Created by fabian on 17.03.16.
 */
public interface IWideLanguageParser {
    WideQueryRequest buildDocumentationQuery(PsiFile file, PsiElement startElement, PsiElement endElement);
    PsiElement getRelevantElement(PsiElement element);
}
