package ch.ethz.inf.globis.wide.parsing.html;

import ch.ethz.inf.globis.wide.communication.WideHttpCommunicator;
import ch.ethz.inf.globis.wide.parsing.AbstractLanguageHandler;
import ch.ethz.inf.globis.wide.parsing.WideQueryResult;
import ch.ethz.inf.globis.wide.parsing.javascript.WideJavascriptHandler;
import com.intellij.lang.javascript.psi.JSElement;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.css.CssDeclaration;
import com.intellij.psi.html.HtmlTag;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlToken;
import com.intellij.util.xml.XmlName;
import com.sun.deploy.xml.XMLAttribute;

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

            if (startElement.getParent() instanceof XmlAttribute) {
                System.out.println("HTML Attribute");

                //TODO: style: interpret whole CSS

                if (startElement.getParent().getLastChild().getFirstChild().getNextSibling() instanceof JSElement) {
                    // Javascript in the attribute's value: Handle JS.
                    System.out.println("Interpreting JS Attribute value.");

                    PsiElement jsStart = startElement.getParent().getLastChild().getFirstChild().getNextSibling();
                    while (jsStart.getFirstChild() != null) {
                        jsStart = jsStart.getFirstChild();
                    }

                    PsiElement jsEnd = startElement.getParent().getLastChild().getLastChild().getPrevSibling();
                    while (jsEnd.getLastChild() != null) {
                        jsEnd = jsEnd.getLastChild();
                    }

                    WideJavascriptHandler jsHandler = new WideJavascriptHandler();
                    List<WideQueryResult> subResults = jsHandler.handle(editor, file, jsStart, jsEnd);

                    results.addAll(subResults);
                }

            } else if (startElement.getParent() instanceof XmlAttributeValue) {
                System.out.println("HTML AttributeValue");

                //TODO: is this value allowed?

            } else if (startElement.getParent() instanceof HtmlTag) {
                System.out.println("HTML Tag");

            }

//            String response = WideHttpCommunicator.sendQuery("function_name=" + startElement.getText() + "&file_name=" + startElement.getContainingFile().getName());
//
//            WideQueryResult result = new WideQueryResult(response);
//            result.setLookupName(startElement.getText());
//            result.setFileName(startElement.getContainingFile().getName());
//            results.add(result);

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
