package ch.ethz.inf.globis.wide.language.html;

import ch.ethz.inf.globis.wide.logging.WideLogger;
import ch.ethz.inf.globis.wide.io.query.WideQueryRequest;
import ch.ethz.inf.globis.wide.language.IWideLanguageParser;
import ch.ethz.inf.globis.wide.registry.WideLanguageRegistry;
import com.intellij.lang.javascript.psi.JSElement;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.css.CssElement;
import com.intellij.psi.html.HtmlTag;
import com.intellij.psi.xml.XmlAttribute;
import com.sun.tools.doclets.formats.html.markup.HtmlAttr;

/**
 * Created by fabian on 17.03.16.
 */
public class WideHtmlParser implements IWideLanguageParser {

    private static final WideLogger LOGGER = new WideLogger(WideHtmlParser.class.getName());

    public WideQueryRequest buildDocumentationQuery(PsiFile file, PsiElement startElement, PsiElement endElement) {
        if (startElement instanceof HtmlTag) {
            WideQueryRequest request = new WideQueryRequest();
            request.setLang("HTML");
            request.setType("tag");
            request.setKey(startElement.getFirstChild().getNextSibling().getText());

            startElement = startElement.getFirstChild().getNextSibling();

            while (!(">".equals(startElement.getText())) && !("/>".equals(startElement.getText()))) {
                while (!( startElement == null) && !(startElement instanceof XmlAttribute)) {
                    startElement = startElement.getNextSibling();
                }

                if (startElement == null) {
                    break;
                }

                WideQueryRequest childRequest = new WideQueryRequest();
                childRequest = buildAttributeRequest(file, startElement.getFirstChild(), startElement.getLastChild());
                request.addChild(childRequest);

                startElement = startElement.getNextSibling();

                while (startElement instanceof PsiWhiteSpace) {
                    startElement = startElement.getNextSibling();
                }

            }

            return request;
        } else {
            return null;
        }
    }

    @Override
    public PsiElement getRelevantElement(PsiElement element) {
        if (element instanceof HtmlTag) {
            return element.getFirstChild().getNextSibling();
        }

        return element;
    }

    private static WideQueryRequest buildAttributeRequest(PsiFile file, PsiElement attribute, PsiElement value) {
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

            IWideLanguageParser parser = WideLanguageRegistry.getInstance().getLanguageHandler(CssElement.class).getLanguageParser();
            WideQueryRequest childRequest = new WideQueryRequest();
            childRequest = parser.buildDocumentationQuery(file, cssStart, cssEnd);
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

            IWideLanguageParser parser = WideLanguageRegistry.getInstance().getLanguageHandler(JSElement.class).getLanguageParser();
            WideQueryRequest childRequest = new WideQueryRequest();
            childRequest = parser.buildDocumentationQuery(file, jsStart, jsEnd);
            request.addChild(childRequest);
        }

        return request;
    }


}
