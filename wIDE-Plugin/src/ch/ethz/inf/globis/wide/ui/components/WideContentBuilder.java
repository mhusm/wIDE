package ch.ethz.inf.globis.wide.ui.components;

import com.intellij.openapi.wm.ToolWindow;
import com.intellij.tools.Tool;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.JFXPanel;
import javafx.scene.web.WebView;
import netscape.javascript.JSException;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import java.awt.*;

/**
 * Created by fabian on 18.04.16.
 */
public class WideContentBuilder {

    protected HTMLEditorKit buildHtmlEdiorKit() {
        // add an html editor kit
        HTMLEditorKit kit = new HTMLEditorKit();

        // add some styles to the html
        StyleSheet styleSheet = kit.getStyleSheet();
        try {
            styleSheet.importStyleSheet(WideContentBuilder.class.getResource("/MDNStyleSheet.css"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return kit;
    }

    public static WebView createWebView() {
        WebView webView = new WebView();
        webView.getEngine().setUserStyleSheetLocation(WideContentBuilder.class.getResource("/MDNStyleSheet.css").toString());

        return webView;
    }

    protected JFXPanel addNewJFXPanleToWindow(String title, ToolWindow toolWindow) {
        JFXPanel panel = new JFXPanel();

        // set panel to toolWindow
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content summaryContent = contentFactory.createContent(panel, title, false);

        toolWindow.getContentManager().addContent(summaryContent);
        return panel;
    }

    protected JEditorPane createNewEditorPane(String content, HTMLEditorKit kit) {

        // create jeditorpane
        JEditorPane editorPane = new JEditorPane();

        // make it read-only
        editorPane.setEditable(false);
        editorPane.setContentType("text/html");
        editorPane.setEditorKit(kit);

        // create a document, set it on the jeditorpane, then add the html
        javax.swing.text.Document summaryDoc = kit.createDefaultDocument();
        editorPane.setDocument(summaryDoc);
        editorPane.setText(content);

        DefaultCaret caret = (DefaultCaret) editorPane.getCaret();
        caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
        editorPane.setCaretPosition(0);

        return editorPane;
    }

    protected JScrollPane createNewScrollPane(Container container) {
        // create a scrollpane; modify its summary as desired
        JScrollPane scrollPane = new JBScrollPane(container);
        scrollPane.getVerticalScrollBar().setValue(0);
        return scrollPane;
    }
}
