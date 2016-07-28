package ch.ethz.inf.globis.wide.language.html;

import ch.ethz.inf.globis.wide.communication.WideHttpCommunicator;
import ch.ethz.inf.globis.wide.logging.WideLogger;
import ch.ethz.inf.globis.wide.io.query.WideQueryRequest;
import ch.ethz.inf.globis.wide.io.query.WideQueryResponse;
import ch.ethz.inf.globis.wide.language.IWideLanguageHandler;
import com.intellij.codeInsight.lookup.Lookup;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.ide.IdeEventQueue;
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
    public WideHtmlPopupFactory getPopupHelper() {
        return WideHtmlPopupFactory.getInstance();
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
    public String getLanguageAbbreviation() {
        return "HTML";
    }

    @Override
    public WideQueryRequest getDocumentationRequest(Editor editor, PsiFile file, PsiElement startElement, PsiElement endElement) {
        if (startElement.equals(endElement)) {
            // only one element.

            // ATTRIBUTE SELECTED
            if (startElement.getParent() instanceof XmlAttribute) {
                LOGGER.info("HTML Attribute");
                return new WideHtmlParser().buildDocumentationQuery(file, startElement.getParent().getParent(), startElement.getParent().getParent());

                // ATTRIBUTE VALUE SELECTED
            } else if (startElement.getParent() instanceof XmlAttributeValue) {
                LOGGER.info("HTML AttributeValue");
                return new WideHtmlParser().buildDocumentationQuery(file, startElement.getParent().getParent(), startElement.getParent().getParent());

                // TAG SELECTED
            } else if (startElement.getParent() instanceof HtmlTag) {
                LOGGER.info("HTML Tag");
                return new WideHtmlParser().buildDocumentationQuery(file, startElement.getParent(), startElement.getParent());
            }
        }

        return null;
    }

    @Override
    public WideQueryRequest getSuggestionRequest(LookupElement lookupElement, PsiElement psiElement, Lookup lookup) {
        if ((psiElement.getParent() instanceof HtmlTag
                && psiElement.getPrevSibling() == null)
                || psiElement.getText().equals("<")) {
            // HTML Tag
            WideQueryRequest request = new WideQueryRequest();
            request.setLang(getLanguageAbbreviation());
            request.setType("tag");
            request.setKey(lookupElement.getLookupString());

            return request;

        } else if (lookup.getPsiElement().getParent() instanceof HtmlTag) {
            // HTML attribute
            // NOOP

        } else if (lookup.getPsiElement().getParent() instanceof XmlAttributeValue) {
            // HTML attribute value
            //System.out.println("HTML Attribute Value");
            //NOOP

        }

        return null;
    }

    @Override
    public void showDocumentationResults(WideQueryResponse response, PsiElement selectedElement, Editor editor, ToolWindow window) {
        // Show Popup
        // ATTRIBUTE SELECTED
        if (selectedElement.getParent() instanceof XmlAttribute) {
            // show correct information
            for (WideQueryResponse attribute : response.getSubResults()) {
                if (attribute.getKey().equals(selectedElement.getText())) {
                    getPopupHelper().showLookupResults(response, attribute, editor);
                }
            }

            // ATTRIBUTE VALUE SELECTED
        } else if (selectedElement.getParent() instanceof XmlAttributeValue) {
            // show correct information
            for (WideQueryResponse attribute : response.getSubResults()) {
                if (attribute.getKey().equals(selectedElement.getParent().getFirstChild().getText())) {
                    getPopupHelper().showLookupResults(response, attribute, editor);
                }
            }

            // TAG SELECTED
        } else if (selectedElement.getParent() instanceof HtmlTag) {
            getPopupHelper().showLookupResults(response, response, editor);
        }

        // Show window
        getWindowFactory().showLookupWindow(window, response);
    }

    @Override
    public void showSuggestionResults(WideQueryResponse response, Editor editor, ToolWindow toolWindow) {
        getWindowFactory().showLookupWindow(toolWindow, response);
    }

    public WideQueryResponse lookupDocumentation(Editor editor, PsiFile file, PsiElement startElement, PsiElement endElement) {

        WideQueryResponse response = null;
        if (startElement.equals(endElement)) {
            // only one element.

            // ATTRIBUTE SELECTED
            if (startElement.getParent() instanceof XmlAttribute) {
                LOGGER.info("HTML Attribute");

                WideQueryRequest request = new WideHtmlParser().buildDocumentationQuery(file, startElement.getParent().getParent(), startElement.getParent().getParent());
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

                WideQueryRequest request = new WideHtmlParser().buildDocumentationQuery(file, startElement.getParent().getParent(), startElement.getParent().getParent());
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

                WideQueryRequest request = new WideHtmlParser().buildDocumentationQuery(file, startElement.getParent(), startElement.getParent());
                response = WideHttpCommunicator.sendRequest(request);

                getPopupHelper().showLookupResults(response, response, editor);
            }

            Project project = editor.getProject();
            ToolWindow window = ToolWindowManager.getInstance(project).getToolWindow("wIDE");

            getWindowFactory().showLookupWindow(window, response);
        }

        return response;
    }



    public void getSuggestionDocumentation(LookupElement lookupElement, PsiElement psiElement, Lookup lookup) {
        if ((psiElement.getParent() instanceof HtmlTag
                && psiElement.getPrevSibling() == null)
                || psiElement.getText().equals("<")) {
            // HTML Tag
            WideQueryRequest request = new WideQueryRequest();
            request.setLang(getLanguageAbbreviation());
            request.setType("tag");
            request.setKey(lookupElement.getLookupString());

                    WideQueryResponse response = WideHttpCommunicator.sendRequest(request);

                    Project project = lookup.getEditor().getProject();
                    ToolWindow window = ToolWindowManager.getInstance(project).getToolWindow("wIDE");

                    IdeEventQueue.getInstance().doWhenReady(new Runnable() {
                        @Override
                        public void run() {
                            getWindowFactory().showLookupWindow(window, response);
                        }
                    });


        } else if (lookup.getPsiElement().getParent() instanceof HtmlTag) {
            // HTML attribute
            // NOOP

        } else if (lookup.getPsiElement().getParent() instanceof XmlAttributeValue) {
            // HTML attribute value
            //System.out.println("HTML Attribute Value");
            //NOOP

        }
    }
}
