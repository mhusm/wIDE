package ch.ethz.inf.globis.wide.parsing;

import ch.ethz.inf.globis.wide.lookup.io.WideQueryRequest;
import ch.ethz.inf.globis.wide.lookup.io.WideQueryResponse;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;

import java.util.List;

/**
 * Created by fabian on 17.03.16.
 */
public interface IWideLanguageParser {
    public WideQueryRequest buildDocumentationQuery(Editor editor, PsiFile file, PsiElement startElement, PsiElement endElement);
}
