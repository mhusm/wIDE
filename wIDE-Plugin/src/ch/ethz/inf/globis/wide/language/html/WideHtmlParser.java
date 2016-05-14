package ch.ethz.inf.globis.wide.language.html;

import ch.ethz.inf.globis.wide.language.IWideLanguageHandler;
import ch.ethz.inf.globis.wide.logging.WideLogger;
import ch.ethz.inf.globis.wide.lookup.io.WideQueryRequest;
import ch.ethz.inf.globis.wide.parsing.IWideLanguageParser;
import ch.ethz.inf.globis.wide.language.css.WideCssParser;
import ch.ethz.inf.globis.wide.registry.WideLanguageRegistry;
import com.intellij.lang.javascript.psi.JSElement;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.css.CssElement;
import com.intellij.psi.xml.XmlAttribute;

/**
 * Created by fabian on 17.03.16.
 */
public class WideHtmlParser implements IWideLanguageParser {

    private static final WideLogger LOGGER = new WideLogger(WideHtmlParser.class.getName());

    public WideQueryRequest buildDocumentationQuery(Editor editor, PsiFile file, PsiElement startElement, PsiElement endElement) {
        WideQueryRequest request = new WideQueryRequest();
        request.setLang("HTML");
        request.setType("tag");
        request.setKey(startElement.getText());

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

            IWideLanguageParser parser = WideLanguageRegistry.getInstance().getLanguageHandler(CssElement.class).getLanguageParser();
            WideQueryRequest childRequest = new WideQueryRequest();
            childRequest = parser.buildDocumentationQuery(editor, file, cssStart, cssEnd);
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
            childRequest = parser.buildDocumentationQuery(editor, file, jsStart, jsEnd);
            request.addChild(childRequest);
        }

        return request;
    }
}
