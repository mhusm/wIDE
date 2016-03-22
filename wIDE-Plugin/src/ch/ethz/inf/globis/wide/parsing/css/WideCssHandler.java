package ch.ethz.inf.globis.wide.parsing.css;

import ch.ethz.inf.globis.wide.communication.WideHttpCommunicator;
import ch.ethz.inf.globis.wide.parsing.AbstractLanguageHandler;
import ch.ethz.inf.globis.wide.parsing.WideQueryResult;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.css.CssDeclaration;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fabian on 17.03.16.
 */
public class WideCssHandler implements AbstractLanguageHandler {
    public List<WideQueryResult> handle(Editor editor, PsiFile file, PsiElement startElement, PsiElement endElement) {

        List<WideQueryResult> results = new ArrayList<WideQueryResult>();

        String request = buildRequest(file, startElement, endElement);
        String response = WideHttpCommunicator.sendRequest(request);

        WideQueryResult result = new WideQueryResult(response);
        results.add(result);

        return results;
    }

    public String buildRequest(PsiFile file, PsiElement startElement, PsiElement endElement) {
        if (startElement.equals(endElement)) {
            // only one element.
            System.out.println("Build CSS lookup request: [attribute] " + startElement.getText());

            //TODO: class - unused?

            //TODO: id - unused?

            //TODO: simple-selector - unused?

            //TODO: attribute

            //TODO: attribute-right-side

            //TODO: pseudo-element

            String request = "{" +
                    "\"lang\": \"CSS\", " +
                    "\"type\": \"CSS\", " +
                    "\"key\": \"" + startElement.getText() + "\", " +
                    "\"value\": \"" + startElement.getText() + "\" " +
                    "}";

            return request;

        } else {

            PsiElement parent = startElement;
            while (!(parent instanceof CssDeclaration)) {
                parent = parent.getParent();
            }

            System.out.println("Build CSS lookup request: [attribute] " + parent.getFirstChild().getText() + " [value] " + parent.getLastChild().getText());

            String request = "{" +
                    "\"lang\": \"CSS\", " +
                    "\"type\": \"CSS\", " +
                    "\"key\": \"" + parent.getFirstChild().getText() + "\", " +
                    "\"value\": \"" + parent.getLastChild().getText() + "\" " +
                    "}";

            return request;

        }
    }
}
