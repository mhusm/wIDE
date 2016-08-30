package ch.ethz.inf.globis.wide.language.css;

import ch.ethz.inf.globis.wide.io.query.WideQueryRequest;
import ch.ethz.inf.globis.wide.language.IWideLanguageParser;
import ch.ethz.inf.globis.wide.registry.WideLanguageRegistry;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.css.CssDeclaration;

/**
 * Created by fabian on 17.03.16.
 */
public class WideCssParser implements IWideLanguageParser {

    public WideQueryRequest buildDocumentationQuery(PsiFile file, PsiElement startElement, PsiElement endElement) {
        if (startElement.equals(endElement)
                && startElement.getParent() instanceof CssDeclaration) {
                // only one element.

                WideQueryRequest request = new WideQueryRequest();
                request.setLang("CSS");
                request.setType("CSS");
                request.setKey(startElement.getParent().getFirstChild().getText());
                request.setValue(startElement.getParent().getLastChild().getText());

                return request;
        } else {
            return null;
        }
    }

    @Override
    public PsiElement getRelevantElement(PsiElement element) {
        return element.getParent().getFirstChild();
    }
}
