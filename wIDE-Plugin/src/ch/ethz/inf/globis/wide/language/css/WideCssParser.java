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

    public WideQueryRequest buildDocumentationQuery(Editor editor, PsiFile file, PsiElement startElement, PsiElement endElement) {
        if (startElement.equals(endElement)
                && startElement.getParent() instanceof CssDeclaration) {
                // only one element.
                //TODO: class - unused?

                //TODO: id - unused?

                //TODO: simple-selector - unused?

                //TODO: attribute

                //TODO: attribute-right-side

                //TODO: pseudo-element

                WideQueryRequest request = new WideQueryRequest();
                request.setLang("CSS");
                request.setType("CSS");
                request.setKey(startElement.getParent().getFirstChild().getText());
                request.setValue(startElement.getParent().getLastChild().getText());

                return request;
        } else {
//
//            PsiElement parent = startElement;
//            while (!(parent instanceof CssDeclaration)) {
//                if (parent == null) {
//                    return null;
//                }
//                parent = parent.getParent();
//            }
//            WideQueryRequest request = new WideQueryRequest();
//            request.setLang("CSS");
//            request.setType("CSS");
//            request.setKey(parent.getFirstChild().getText());
//            request.setValue(parent.getLastChild().getText());

            return null;
        }
    }

    @Override
    public PsiElement getRelevantElement(PsiElement element) {
        return element.getParent().getFirstChild();
    }
}
