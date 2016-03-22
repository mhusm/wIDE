package ch.ethz.inf.globis.wide.parsing.html;

import ch.ethz.inf.globis.wide.communication.WideHttpCommunicator;
import ch.ethz.inf.globis.wide.parsing.AbstractLanguageHandler;
import ch.ethz.inf.globis.wide.parsing.WideQueryResult;
import ch.ethz.inf.globis.wide.parsing.css.WideCssHandler;
import ch.ethz.inf.globis.wide.parsing.javascript.WideJavascriptHandler;
import com.intellij.lang.javascript.psi.JSElement;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.css.CssDeclaration;
import com.intellij.psi.html.HtmlTag;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlAttributeValue;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fabian on 17.03.16.
 */
public class WideHtmlHandler implements AbstractLanguageHandler {
    public List<WideQueryResult> handle(Editor editor, PsiFile file, PsiElement startElement, PsiElement endElement) {

        List<WideQueryResult> results = new ArrayList<WideQueryResult>();

        if (startElement.equals(endElement)) {
            // only one element.
            System.out.println("HTML lookup request");

            // ATTRIBUTE SELECTED
            if (startElement.getParent() instanceof XmlAttribute) {
                System.out.println("HTML Attribute");

                WideQueryResult result = lookupAttribute(editor, file, startElement.getParent().getParent().getFirstChild().getNextSibling(), startElement, startElement.getParent().getLastChild(), 0);
                results.add(result);

                // ATTRIBUTE VALUE SELECTED
            } else if (startElement.getParent() instanceof XmlAttributeValue) {
                System.out.println("HTML AttributeValue");

                WideQueryResult result = lookupAttribute(editor, file, startElement.getParent().getParent().getFirstChild().getNextSibling(), startElement.getParent().getFirstChild(), startElement, 0);
                results.add(result);

                // TAG SELECTED
            } else if (startElement.getParent() instanceof HtmlTag) {
                System.out.println("HTML Tag");

                WideQueryResult parentResult = lookupTag(editor, file, startElement);
                results.add(parentResult);
            }

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

    private static WideQueryResult lookupTag(Editor editor, PsiFile file, PsiElement tag) {

        String request = buildTagRequest(editor, file, tag);
        String response = WideHttpCommunicator.sendRequest(request);

        WideQueryResult result = new WideQueryResult(response);
        return result;
    }

    private static WideQueryResult lookupAttribute(Editor editor, PsiFile file, PsiElement tag, PsiElement attribute, PsiElement value, int parentLevel) {

       String request = "{" +
               "    \"lang\": \"HTML\", " +
               "    \"type\": \"tag\", " +
               "    \"key\": \"" + tag.getText() + "\", " +
               "    \"children\": [" + buildAttributeRequest(editor, file, attribute, value) + "]}";

        String response = WideHttpCommunicator.sendRequest(request);
        WideQueryResult result = new WideQueryResult(response);
        return result.getSubResults().get(0);
    }

    private static String buildAttributeRequest(Editor editor, PsiFile file, PsiElement attribute, PsiElement value) {
        // LOOKUP HTML ATTRIBUTE
        String request = "{" +
                "    \"lang\": \"HTML\", " +
                "    \"type\": \"attribute\", " +
                "    \"key\": \"" + attribute.getText() + "\", " +
                "    \"value\": \"" + value.getText().substring(1, value.getText().length()-1) + "\", ";

        // LOOKUP CSS ATTRIBUTES
        if ("style".equals(attribute.getParent().getFirstChild().getText())) {
            // CSS in the attribute's value: Handle JS.
            System.out.println("Interpreting CSS Attribute value.");

            PsiElement cssStart = attribute.getParent().getLastChild().getFirstChild().getNextSibling();
            while (cssStart.getFirstChild() != null) {
                cssStart = cssStart.getFirstChild();
            }

            PsiElement cssEnd = attribute.getParent().getLastChild().getLastChild().getPrevSibling();
            while (cssEnd.getLastChild() != null) {
                cssEnd = cssEnd.getLastChild();
            }

            WideCssHandler cssHandler = new WideCssHandler();
            request += "    \"children\": [" + cssHandler.buildRequest(file, cssStart, cssEnd) + "]}";
        }

        // LOOKUP JS FUNCTIONS
        else if (attribute.getParent().getLastChild().getFirstChild() != null && attribute.getParent().getLastChild().getFirstChild().getNextSibling() instanceof JSElement) {
            // Javascript in the attribute's value: Handle JS.
            System.out.println("Interpreting JS Attribute value.");

            PsiElement jsStart = attribute.getParent().getLastChild().getFirstChild().getNextSibling();
            while (jsStart.getFirstChild() != null) {
                jsStart = jsStart.getFirstChild();
            }

            PsiElement jsEnd = attribute.getParent().getLastChild().getLastChild().getPrevSibling();
            while (jsEnd.getLastChild() != null) {
                jsEnd = jsEnd.getLastChild();
            }

            WideJavascriptHandler jsHandler = new WideJavascriptHandler();
            request += "    \"children\": [" + jsHandler.buildRequest(editor, file, jsStart, jsEnd) + "]}";
        }

        else {
            request += "    \"children\": []}";
        }

        return request;
    }

    private static String buildTagRequest(Editor editor, PsiFile file, PsiElement tag) {
        String request = "{" +
                "\"lang\": \"HTML\", " +
                "\"type\": \"tag\", " +
                "\"key\": \"" + tag.getText() + "\", " +
                "\"children\" : [";

        PsiElement startElement = tag;

        while (!(">".equals(startElement.getText())) && !("/>".equals(startElement.getText()))) {
            while (!(startElement instanceof XmlAttribute)) {
                startElement = startElement.getNextSibling();
            }

            request += buildAttributeRequest(editor, file, startElement.getFirstChild(), startElement.getLastChild()) + ", ";

            startElement = startElement.getNextSibling();

            while (startElement instanceof PsiWhiteSpace) {
                startElement = startElement.getNextSibling();
            }

        }

        request += "null] " +
                "}";

        return request;
    }
}
