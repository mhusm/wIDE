package ch.ethz.inf.globis.wide.parsing.css;

import ch.ethz.inf.globis.wide.communication.WideHttpCommunicator;
import ch.ethz.inf.globis.wide.parsing.AbstractLanguageHandler;
import ch.ethz.inf.globis.wide.parsing.WideQueryResult;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.css.CssDeclaration;
import com.intellij.psi.css.CssElement;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fabian on 17.03.16.
 */
public class WideCssHandler implements AbstractLanguageHandler {
    public List<WideQueryResult> handle(Editor editor, PsiFile file, PsiElement startElement, PsiElement endElement) {

        List<WideQueryResult> results = new ArrayList<WideQueryResult>();

        if (startElement.equals(endElement)) {
            // only one element.
            System.out.println("CSS lookup request");

            //TODO: class - unused?

            //TODO: id - unused?

            //TODO: simple-selector - unused?

            //TODO: attribute

            //TODO: attribute-right-side

            //TODO: pseudo-element

            String response = WideHttpCommunicator.sendQuery("function_name=" + startElement.getText() + "&file_name=" + startElement.getContainingFile().getName());

            WideQueryResult result = new WideQueryResult(response);
            result.setLookupName(startElement.getText());
            result.setFileName(startElement.getContainingFile().getName());
            results.add(result);

        } else {
            System.out.println("related objects");

            PsiElement parent = startElement;
            while (!(parent instanceof CssDeclaration)) {
                parent = parent.getParent();
            }

            // TODO: declaration + term

        }

        return results;
    }
}
