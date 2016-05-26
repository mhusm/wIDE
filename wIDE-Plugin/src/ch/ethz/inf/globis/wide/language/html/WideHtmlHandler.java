package ch.ethz.inf.globis.wide.language.html;

import ch.ethz.inf.globis.wide.communication.WideHttpCommunicator;
import ch.ethz.inf.globis.wide.logging.WideLogger;
import ch.ethz.inf.globis.wide.io.query.WideQueryRequest;
import ch.ethz.inf.globis.wide.io.query.WideQueryResponse;
import ch.ethz.inf.globis.wide.language.IWideLanguageHandler;
import com.intellij.codeInsight.lookup.Lookup;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.css.CssDeclaration;
import com.intellij.psi.html.HtmlTag;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlToken;

/**
 * Created by fabian on 12.05.16.
 */
public class WideHtmlHandler implements IWideLanguageHandler {

    private static final WideLogger LOGGER = new WideLogger(WideHtmlHandler.class.getName());

    @Override
    public WideHtmlPopupHelper getPopupHelper() {
        return WideHtmlPopupHelper.getInstance();
    }

    @Override
    public WideHtmlWindowFactory getWindowFactory() {
        return WideHtmlWindowFactory.getInstance();
    }

    @Override
    public WideHtmlParser getLanguageParser() {
        return new WideHtmlParser();
    }

    @Override
    public WideQueryResponse lookupDocumentation(Editor editor, PsiFile file, PsiElement startElement, PsiElement endElement) {

        WideQueryResponse response = null;
        if (startElement.equals(endElement)) {
            // only one element.

            // ATTRIBUTE SELECTED
            if (startElement.getParent() instanceof XmlAttribute) {
                LOGGER.info("HTML Attribute");

                WideQueryRequest request = new WideHtmlParser().buildDocumentationQuery(editor, file, startElement.getParent().getParent().getFirstChild().getNextSibling(), startElement.getParent().getParent().getFirstChild().getNextSibling());
                response = WideHttpCommunicator.sendRequest(request);

                // show correct information
                for (WideQueryResponse attribute : response.getSubResults()) {
                    if (attribute.getKey().equals(startElement.getText())) {
                        getPopupHelper().showLookupResults(response, attribute, editor);
                    }
                }

                //WidePopupHelper.getInstance().showHtmlAttributeLookupResult(results, results.get(0).getSubResults().get(0), editor);

                // ATTRIBUTE VALUE SELECTED
            } else if (startElement.getParent() instanceof XmlAttributeValue) {
                LOGGER.info("HTML AttributeValue");

                WideQueryRequest request = new WideHtmlParser().buildDocumentationQuery(editor, file, startElement.getParent().getParent().getFirstChild().getNextSibling(), startElement.getParent().getParent().getFirstChild().getNextSibling());
                response = WideHttpCommunicator.sendRequest(request);

                // show correct information
                for (WideQueryResponse attribute : response.getSubResults()) {
                    if (attribute.getKey().equals(startElement.getParent().getFirstChild().getText())) {
                        getPopupHelper().showLookupResults(response, attribute, editor);
                    }
                }

                // WidePopupHelper.getInstance().showHtmlAttributeLookupResult(results, results.get(0).getSubResults().get(0), editor);

                // TAG SELECTED
            } else if (startElement.getParent() instanceof HtmlTag) {
                LOGGER.info("HTML Tag");

                WideQueryRequest request = new WideHtmlParser().buildDocumentationQuery(editor, file, startElement, startElement);
                response = WideHttpCommunicator.sendRequest(request);

                getPopupHelper().showLookupResults(response, response, editor);
            }

        } else if (startElement instanceof PsiWhiteSpace && endElement instanceof XmlToken) {
            LOGGER.info("New HTML tag");

            WideQueryRequest request = new WideHtmlParser().buildDocumentationQuery(editor,
                    file,
                    startElement.getParent().getParent().getFirstChild().getNextSibling(),
                    startElement.getParent().getParent().getFirstChild().getNextSibling());

            response = WideHttpCommunicator.sendRequest(request);

        } else {
            LOGGER.warning("Multiple related HTML objects are not yet supported.");

            PsiElement parent = startElement;
            while (!(parent instanceof CssDeclaration)) {
                parent = parent.getParent();
            }

            // TODO: declaration + term

        }

        Project project = editor.getProject();
        ToolWindow window = ToolWindowManager.getInstance(project).getToolWindow("wIDE");

        getWindowFactory().showLookupWindow(window, response);

        return response;
    }

//    @Override
//    public void lookupSuggestions(Editor editor, PsiElement element, String newChar) {
//        boolean isSuggestionPosition = false;
//        WideQueryRequest request = new WideQueryRequest();
//        request.setLang("HTML");
//
//        if (!newChar.equals("")) {
//            // new char entered
//
//            if (newChar.equals("<")) {
//                // start writing a tag
//                System.out.println("Writing HTML tag");
//                request.setKey("");
//                request.setType("tag");
//                isSuggestionPosition = true;
//
//            } else if (element.getParent() instanceof HtmlTag && !newChar.equals(" ") && !element.getText().substring(element.getTextLength()-1).equals(" ")) {
//                System.out.println("Writing HTML tag");
//                request.setKey(element.getParent().getText().substring(1) + newChar);
//                request.setType("tag");
//                isSuggestionPosition = true;
//
//            } else if (element instanceof XmlToken && element.getText().charAt(0) == '<') {
//                // start writing a tag
//                System.out.println("Writing HTML tag");
//                request.setKey(element.getText().substring(1) + newChar);
//                request.setType("tag");
//                isSuggestionPosition = true;
//
//            } else if (element.getParent() instanceof XmlAttribute) {
//                System.out.println("Writing HTML attribute");
//                request.setKey(element.getText() + newChar);
//                request.setValue(element.getParent().getParent().getFirstChild().getNextSibling().getText());
//                request.setType("attribute");
//                isSuggestionPosition = true;
//
//            } else if (element.getParent() instanceof HtmlTag && newChar.equals(" ")) {
//                // start writing an attribute
//                System.out.println("Writing HTML attribute");
//                request.setKey("");
//                request.setValue(element.getParent().getFirstChild().getNextSibling().getText());
//                request.setType("attribute");
//                isSuggestionPosition = true;
//
//            } else if (element.getPrevSibling() instanceof HtmlTag && !newChar.equals(" ") && element.getText().substring(element.getTextLength()-1).equals(" ")) {
//                System.out.println("Writing HTML attribute");
//                request.setKey(newChar);
//                request.setValue(element.getPrevSibling().getFirstChild().getNextSibling().getText());
//                request.setType("attribute");
//                isSuggestionPosition = true;
//
//            } else if (element.getParent() instanceof XmlAttributeValue && newChar.equals(" ")) {
//                // Start writing attribute after attributeValue
//                System.out.println("Writing HTML attribute");
//                request.setKey("");
//                request.setValue(((HtmlTag) element.getParent().getParent().getParent()).getLocalName());
//                request.setType("attribute");
//                isSuggestionPosition = true;
//
//            } else if (element.getParent() instanceof HtmlTag && element instanceof PsiWhiteSpace) {
//                // First char of new Attribute typed
//                System.out.println("Writing HTML attribute");
//                request.setKey(newChar);
//                request.setValue(((HtmlTag) element.getParent()).getLocalName());
//                request.setType("attribute");
//                isSuggestionPosition = true;
//
//            } else if (element.getParent() instanceof XmlAttributeValue) {
//                System.out.println("Writing HTML attribute value");
//                request.setKey(element.getText() + newChar);
//                request.setType("attribute");
//                isSuggestionPosition = true;
//            }
//        } else {
//            // char deleted
//
//            if (element.getParent() instanceof HtmlTag && !element.getText().equals(" ")) {
//                System.out.println("Writing HTML tag");
//                request.setKey(element.getParent().getText().substring(1, element.getParent().getText().length() - 1) + newChar);
//                request.setType("tag");
//                isSuggestionPosition = true;
//
//            } else if (element instanceof XmlToken && element.getText().charAt(0) == '<') {
//                // start writing a tag
//                System.out.println("Writing HTML tag");
//                request.setKey(element.getText().substring(1) + newChar);
//                request.setType("tag");
//                isSuggestionPosition = true;
//
//            } else if (element.getParent() instanceof HtmlTag && element.getText().equals(" ")) {
//                System.out.println("Writing HTML attribute");
//                request.setKey("");
//                request.setValue(((HtmlTag) element.getParent()).getLocalName());
//                request.setType("attribute");
//                isSuggestionPosition = true;
//
//            } else if (element.getParent() instanceof XmlAttribute) {
//                System.out.println("Writing HTML attribute");
//                request.setKey(element.getText().substring(0, element.getText().length() - 1) + newChar);
//                request.setValue(element.getParent().getParent().getFirstChild().getNextSibling().getText());
//                request.setType("attribute");
//                isSuggestionPosition = true;
//
//            } else if (element.getParent() instanceof HtmlTag && element instanceof PsiWhiteSpace) {
//                // First char of new Attribute typed
//                System.out.println("Writing HTML attribute");
//                request.setKey("");
//                request.setValue(((HtmlTag) element.getParent()).getLocalName());
//                request.setType("attribute");
//                isSuggestionPosition = true;
//
//            } else if (element.getParent() instanceof HtmlTag && element.getText().equals(" ")) {
//                // at beginning of writing an attribute
//                System.out.println("Writing HTML attribute");
//                request.setKey("");
//                request.setValue(element.getParent().getFirstChild().getNextSibling().getText());
//                request.setType("attribute");
//                isSuggestionPosition = true;
//
//            }
//        }
//
//        if (isSuggestionPosition) {
//            // we were at a valid suggestion position -> do lookup and show results
//            WideQueryResponse response = WideHttpCommunicator.sendSuggestionRequest(request);
//
//            Project project = editor.getProject();
//            ToolWindow window = ToolWindowManager.getInstance(project).getToolWindow("wIDE");
//
//            getPopupHelper().showSuggestions(response.getSubResults(), window, element, editor);
//            //editor.getComponent().getListeners(KeyListener.class)
//        }
//    }

    @Override
    public void getSuggestionDocumentation(LookupElement lookupElement, Lookup lookup) {

        if ((lookup.getPsiElement().getParent() instanceof HtmlTag
                && lookup.getPsiElement().getPrevSibling() == null)
                || lookup.getPsiElement().getText().equals("<")) {
            // HTML Tag
            WideQueryRequest request = new WideQueryRequest();
            request.setLang("HTML");
            request.setType("tag");
            request.setKey(lookupElement.getPsiElement().getText());
            WideQueryResponse response = WideHttpCommunicator.sendRequest(request);

            Project project = lookup.getEditor().getProject();
            ToolWindow window = ToolWindowManager.getInstance(project).getToolWindow("wIDE");

            getWindowFactory().showLookupWindow(window, response);

        } else if (lookup.getPsiElement().getParent() instanceof HtmlTag) {
            // HTML attribute
            // NOOP

        } else if (lookup.getPsiElement().getParent() instanceof XmlAttributeValue) {
            // HTML attribute value
            System.out.println("HTML Attribute Value");
            //NOOP

        }

    }
}
