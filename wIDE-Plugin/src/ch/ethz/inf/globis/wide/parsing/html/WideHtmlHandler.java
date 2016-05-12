package ch.ethz.inf.globis.wide.parsing.html;

import ch.ethz.inf.globis.wide.communication.WideHttpCommunicator;
import ch.ethz.inf.globis.wide.logging.WideLogger;
import ch.ethz.inf.globis.wide.lookup.io.WideQueryRequest;
import ch.ethz.inf.globis.wide.lookup.io.WideQueryResponse;
import ch.ethz.inf.globis.wide.parsing.WideAbstractLanguageHandler;
import ch.ethz.inf.globis.wide.parsing.css.WideCssHandler;
import ch.ethz.inf.globis.wide.parsing.javascript.WideJavascriptHandler;
import ch.ethz.inf.globis.wide.ui.popup.WidePopupHelper;
import com.intellij.lang.javascript.psi.JSElement;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.css.CssDeclaration;
import com.intellij.psi.html.HtmlTag;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlToken;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fabian on 17.03.16.
 */
public class WideHtmlHandler implements WideAbstractLanguageHandler {

    private static final WideLogger LOGGER = new WideLogger(WideHtmlHandler.class.getName());

    public List<WideQueryResponse> handle(Editor editor, PsiFile file, PsiElement startElement, PsiElement endElement, boolean isFinished) {

        List<WideQueryResponse> results = new ArrayList<WideQueryResponse>();

        if (startElement.equals(endElement)) {
            // only one element.

            // ATTRIBUTE SELECTED
            if (startElement.getParent() instanceof XmlAttribute) {
                LOGGER.info("HTML Attribute");

                WideQueryResponse result = lookupTag(editor, file, startElement.getParent().getParent().getFirstChild().getNextSibling());
                results.add(result);

                // show correct information
                for (WideQueryResponse attribute : results.get(0).getSubResults()) {
                    if (attribute.getKey().equals(startElement.getText())) {
                        WidePopupHelper.getInstance().showHtmlAttributeLookupResult(results, attribute, editor);
                    }
                }

                //WidePopupHelper.getInstance().showHtmlAttributeLookupResult(results, results.get(0).getSubResults().get(0), editor);

                // ATTRIBUTE VALUE SELECTED
            } else if (startElement.getParent() instanceof XmlAttributeValue) {
                LOGGER.info("HTML AttributeValue");

                WideQueryResponse result = lookupTag(editor, file, startElement.getParent().getParent().getFirstChild());
                results.add(result);

                // show correct information
                for (WideQueryResponse attribute : results.get(0).getSubResults()) {
                    if (attribute.getKey().equals(startElement.getParent().getFirstChild().getText())) {
                        WidePopupHelper.getInstance().showHtmlAttributeLookupResult(results, attribute, editor);
                    }
                }

               // WidePopupHelper.getInstance().showHtmlAttributeLookupResult(results, results.get(0).getSubResults().get(0), editor);

                // TAG SELECTED
            } else if (startElement.getParent() instanceof HtmlTag) {
                LOGGER.info("HTML Tag");

                WideQueryResponse parentResult = lookupTag(editor, file, startElement);
                results.add(parentResult);

                WidePopupHelper.getInstance().showHtmlTagLookupResults(results, editor);
            }

        } else if (startElement instanceof PsiWhiteSpace && endElement instanceof XmlToken) {
            LOGGER.info("New HTML tag");

            WideQueryResponse parentResult = lookupTag(editor, file, endElement);
            results.add(parentResult);

            WidePopupHelper.getInstance().showHtmlNewTagLookupResults(results.get(0), editor);

        } else {
            LOGGER.warning("Multiple related HTML objects are not yet supported.");

            PsiElement parent = startElement;
            while (!(parent instanceof CssDeclaration)) {
                parent = parent.getParent();
            }

            // TODO: declaration + term

        }

        return results;
    }

    private static WideQueryResponse lookupTag(Editor editor, PsiFile file, PsiElement tag) {
        WideQueryRequest request = buildTagRequest(editor, file, tag);
        WideQueryResponse response = WideHttpCommunicator.sendRequest(request);
        return response;
    }

    private static WideQueryRequest buildAttributeRequest(Editor editor, PsiFile file, PsiElement attribute, PsiElement value) {
        // LOOKUP HTML ATTRIBUTE
        WideQueryRequest request = new WideQueryRequest();
        request.setLang("HTML");
        request.setType("attribute");
        request.setKey(attribute.getText());
        request.setValue(value.getText().substring(1, value.getText().length()-1));

        // LOOKUP CSS ATTRIBUTES
        if ("style".equals(attribute.getParent().getFirstChild().getText())) {
            // CSS in the attribute's value: Handle JS.
            LOGGER.info("Interpreting CSS Attribute value.");

            PsiElement cssStart = attribute.getParent().getLastChild().getFirstChild().getNextSibling();
            while (cssStart.getFirstChild() != null) {
                cssStart = cssStart.getFirstChild();
            }

            PsiElement cssEnd = attribute.getParent().getLastChild().getLastChild().getPrevSibling();
            while (cssEnd.getLastChild() != null) {
                cssEnd = cssEnd.getLastChild();
            }

            WideCssHandler cssHandler = new WideCssHandler();
            WideQueryRequest childRequest = new WideQueryRequest();
            childRequest = cssHandler.buildRequest(file, cssStart, cssEnd);
            request.addChild(childRequest);
        }

        // LOOKUP JS FUNCTIONS
        else if (attribute.getParent().getLastChild().getFirstChild() != null && attribute.getParent().getLastChild().getFirstChild().getNextSibling() instanceof JSElement) {
            // Javascript in the attribute's value: Handle JS.
            LOGGER.info("Interpreting JS Attribute value.");

            PsiElement jsStart = attribute.getParent().getLastChild().getFirstChild().getNextSibling();
            while (jsStart.getFirstChild() != null) {
                jsStart = jsStart.getFirstChild();
            }

            PsiElement jsEnd = attribute.getParent().getLastChild().getLastChild().getPrevSibling();
            while (jsEnd.getLastChild() != null) {
                jsEnd = jsEnd.getLastChild();
            }

            WideJavascriptHandler jsHandler = new WideJavascriptHandler();
            WideQueryRequest childRequest = new WideQueryRequest();
            childRequest = jsHandler.buildRequest(editor, file, jsStart, jsEnd);
            request.addChild(childRequest);
        }

        return request;
    }

    private static WideQueryRequest buildTagRequest(Editor editor, PsiFile file, PsiElement tag) {

        WideQueryRequest request = new WideQueryRequest();
        request.setLang("HTML");
        request.setType("tag");
        request.setKey(tag.getText());

        PsiElement startElement = tag;

        while (!(">".equals(startElement.getText())) && !("/>".equals(startElement.getText()))) {
            while (!( startElement == null) && !(startElement instanceof XmlAttribute)) {
                startElement = startElement.getNextSibling();
            }

            if (startElement == null) {
                break;
            }

            WideQueryRequest childRequest = new WideQueryRequest();
            childRequest = buildAttributeRequest(editor, file, startElement.getFirstChild(), startElement.getLastChild());
            request.addChild(childRequest);

            startElement = startElement.getNextSibling();

            while (startElement instanceof PsiWhiteSpace) {
                startElement = startElement.getNextSibling();
            }

        }

        return request;
    }
}
