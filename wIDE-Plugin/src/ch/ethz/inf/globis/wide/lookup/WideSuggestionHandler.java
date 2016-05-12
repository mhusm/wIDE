package ch.ethz.inf.globis.wide.lookup;

import ch.ethz.inf.globis.wide.communication.WideHttpCommunicator;
import ch.ethz.inf.globis.wide.logging.WideLogger;
import ch.ethz.inf.globis.wide.lookup.io.WideQueryRequest;
import ch.ethz.inf.globis.wide.lookup.io.WideQueryResponse;
import ch.ethz.inf.globis.wide.ui.action.WideReplaceAction;
import ch.ethz.inf.globis.wide.ui.action.WideSuggestAction;
import ch.ethz.inf.globis.wide.ui.popup.WidePopupHelper;
import ch.ethz.inf.globis.wide.ui.window.WideHtmlWindowFactory;
import ch.ethz.inf.globis.wide.ui.window.WideWindowFactory;
import clojure.lang.IFn;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.ide.IdeEventQueue;
import com.intellij.lang.javascript.psi.*;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.graph.io.graphml.output.WriteEventHandler;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.css.CssElement;
import com.intellij.psi.html.HtmlTag;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlElement;
import com.intellij.psi.xml.XmlToken;

import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by fabian on 20.04.16.
 */
public class WideSuggestionHandler {
    private final static WideLogger LOGGER = new WideLogger(WideDocumentationHandler.class.getName());

    public static void differentiateElements(Editor editor, PsiElement element, String newChar) {
        if (element.getParent() instanceof XmlElement) {
            handleHtml(editor, element, newChar);
        } else if (element.getParent() instanceof CssElement) {
            handleCss(editor, element, newChar);
        } else if (element.getParent() instanceof JSElement) {
            handleJS(editor, element, newChar);
        } else {
            //TODO: language is not supported
            // or don't do anything?
        }
    }

    private static void handleHtml(Editor editor, PsiElement element, String newChar) {

        WideQueryRequest request = new WideQueryRequest();
        request.setLang("HTML");

        //TODO: set properly -> will potentially be replaced by suggestion
        PsiElement editedElement;

        if (!newChar.equals("")) {
            // new char entered

            if (newChar.equals("<")) {
                // start writing a tag
                System.out.println("Writing HTML tag");
                request.setKey("");
                request.setType("tag");

            } else if (element.getParent() instanceof HtmlTag && !newChar.equals(" ") && !element.getText().substring(element.getTextLength()-1).equals(" ")) {
                System.out.println("Writing HTML tag");
                request.setKey(element.getParent().getText().substring(1) + newChar);
                request.setType("tag");

            } else if (element instanceof XmlToken && element.getText().charAt(0) == '<') {
                // start writing a tag
                System.out.println("Writing HTML tag");
                request.setKey(element.getText().substring(1) + newChar);
                request.setType("tag");

            } else if (element.getParent() instanceof XmlAttribute) {
                System.out.println("Writing HTML attribute");
                request.setKey(element.getText() + newChar);
                request.setValue(element.getParent().getParent().getFirstChild().getNextSibling().getText());
                request.setType("attribute");

            } else if (element.getParent() instanceof HtmlTag && newChar.equals(" ")) {
                // start writing an attribute
                System.out.println("Writing HTML attribute");
                request.setKey("");
                request.setValue(element.getParent().getFirstChild().getNextSibling().getText());
                request.setType("attribute");

            } else if (element.getPrevSibling() instanceof HtmlTag && !newChar.equals(" ") && element.getText().substring(element.getTextLength()-1).equals(" ")) {
                System.out.println("Writing HTML attribute");
                request.setKey(newChar);
                request.setValue(element.getPrevSibling().getFirstChild().getNextSibling().getText());
                request.setType("attribute");

            } else if (element.getParent() instanceof XmlAttributeValue && newChar.equals(" ")) {
                // Start writing attribute after attributeValue
                System.out.println("Writing HTML attribute");
                request.setKey("");
                request.setValue(((HtmlTag) element.getParent().getParent().getParent()).getLocalName());
                request.setType("attribute");

            } else if (element.getParent() instanceof HtmlTag && element instanceof PsiWhiteSpace) {
                // First char of new Attribute typed
                System.out.println("Writing HTML attribute");
                request.setKey(newChar);
                request.setValue(((HtmlTag) element.getParent()).getLocalName());
                request.setType("attribute");

            } else if (element.getParent() instanceof XmlAttributeValue) {
                System.out.println("Writing HTML attribute value");
                request.setKey(element.getText() + newChar);
                request.setType("attribute");
            }
        } else {
            // char deleted

            if (element.getParent() instanceof HtmlTag && !element.getText().equals(" ")) {
                System.out.println("Writing HTML tag");
                request.setKey(element.getParent().getText().substring(1, element.getParent().getText().length() - 1) + newChar);
                request.setType("tag");

            } else if (element instanceof XmlToken && element.getText().charAt(0) == '<') {
                // start writing a tag
                System.out.println("Writing HTML tag");
                request.setKey(element.getText().substring(1) + newChar);
                request.setType("tag");

            } else if (element.getParent() instanceof HtmlTag && element.getText().equals(" ")) {
                System.out.println("Writing HTML attribute");
                request.setKey("");
                request.setValue(((HtmlTag) element.getParent()).getLocalName());
                request.setType("attribute");

            } else if (element.getParent() instanceof XmlAttribute) {
                System.out.println("Writing HTML attribute");
                request.setKey(element.getText().substring(0, element.getText().length() - 1) + newChar);
                request.setValue(element.getParent().getParent().getFirstChild().getNextSibling().getText());
                request.setType("attribute");

            } else if (element.getParent() instanceof HtmlTag && element instanceof PsiWhiteSpace) {
                // First char of new Attribute typed
                System.out.println("Writing HTML attribute");
                request.setKey("");
                request.setValue(((HtmlTag) element.getParent()).getLocalName());
                request.setType("attribute");

            } else if (element.getParent() instanceof HtmlTag && element.getText().equals(" ")) {
                // at beginning of writing an attribute
                System.out.println("Writing HTML attribute");
                request.setKey("");
                request.setValue(element.getParent().getFirstChild().getNextSibling().getText());
                request.setType("attribute");

            }
        }

        WideQueryResponse response = WideHttpCommunicator.sendSuggestionRequest(request);

        Project project = editor.getProject();
        ToolWindow window = ToolWindowManager.getInstance(project).getToolWindow("wIDE");

        WidePopupHelper helper = WidePopupHelper.getInstance();
        helper.showHtmlSuggestion(response.getSubResults(), window, element, editor);
        //editor.getComponent().getListeners(KeyListener.class)
    }

    private static void handleJS(Editor editor, PsiElement element, String newChar) {
        //TODO
        if (element.getParent().getParent() instanceof JSCallExpression) {
            System.out.println("Writing JS call to receiver " + element.getParent().getParent().getFirstChild().getText());
        } else if (element.getParent() instanceof JSReferenceExpression) {
            if (newChar.equals(".")) {
                System.out.println("Writing JS call to receiver " + element.getParent().getText());
            } else {
                System.out.println("Writing JS reference");
            }
        } else if (element.getParent() instanceof JSBlockStatement) {
            System.out.println("Writing JS BlockStatement");
        } else if (element.getParent() instanceof JSFunction) {
            System.out.println("Writing JS Function");
        } else {
            //System.out.println("Unknown element: " + element.getParent().getClass());
        }

    }

    private static void handleCss(Editor editor, PsiElement element, String newChar) {
        //TODO
        if (element.getParent() instanceof CssElement) {
            System.out.println("Writing CSS");
        }

    }
}
