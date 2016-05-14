package ch.ethz.inf.globis.wide.language.css;

import ch.ethz.inf.globis.wide.communication.WideHttpCommunicator;
import ch.ethz.inf.globis.wide.lookup.io.WideQueryRequest;
import ch.ethz.inf.globis.wide.parsing.IWideLanguageParser;
import ch.ethz.inf.globis.wide.lookup.io.WideQueryResponse;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.css.CssDeclaration;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fabian on 17.03.16.
 */
public class WideCssParser implements IWideLanguageParser {

    public WideQueryRequest buildDocumentationQuery(Editor editor, PsiFile file, PsiElement startElement, PsiElement endElement) {
        if (startElement.equals(endElement)) {
            // only one element.
            System.out.println("Build CSS lookup request: [attribute] " + startElement.getText());

            //TODO: class - unused?

            //TODO: id - unused?

            //TODO: simple-selector - unused?

            //TODO: attribute

            //TODO: attribute-right-side

            //TODO: pseudo-element

            WideQueryRequest request = new WideQueryRequest();
            request.setLang("CSS");
            request.setType("CSS");
            request.setKey(startElement.getText());
            request.setValue(startElement.getText());

            return request;

        } else {

            PsiElement parent = startElement;
            while (!(parent instanceof CssDeclaration)) {
                parent = parent.getParent();
            }

            System.out.println("Build CSS lookup request: [attribute] " + parent.getFirstChild().getText() + " [value] " + parent.getLastChild().getText());

            WideQueryRequest request = new WideQueryRequest();
            request.setLang("CSS");
            request.setType("CSS");
            request.setKey(parent.getFirstChild().getText());
            request.setValue(parent.getLastChild().getText());

            return request;

        }
    }
}
