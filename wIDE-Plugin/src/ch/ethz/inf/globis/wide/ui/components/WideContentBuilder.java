package ch.ethz.inf.globis.wide.ui.components;

import com.intellij.ui.components.JBScrollPane;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import java.awt.*;

/**
 * Created by fabian on 18.04.16.
 */
public class WideContentBuilder {


    protected static HTMLEditorKit buildHtmlEdiorKit() {
        // add an html editor kit
        HTMLEditorKit kit = new HTMLEditorKit();

        // add some styles to the html
        StyleSheet styleSheet = kit.getStyleSheet();
//        styleSheet.addRule("body { " +
//                "font-family: Calibri; " +
//                "padding: 10px; }");
        try {
            styleSheet.importStyleSheet(WideContentBuilder.class.getResource("/MDNStyleSheet.css"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return kit;
    }

    protected static JEditorPane createNewEditorPane(String content, HTMLEditorKit kit) {
//        WebView webView = new WebView();
//        webView.getEngine().loadContent(content);
//
//        // Load CSS
//        WebEngine engine = webView.getEngine();
//        engine.setUserStyleSheetLocation(WideWindowFactory.class.getResource("/MDNStyleSheet.css").toString());

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

//        Group root = new Group();
//        Scene scene = new Scene(root);
//
//        root.getChildren().add(webView);
//        JFXPanel jfxPanel = new JFXPanel();
//        jfxPanel.setScene(scene);

        return editorPane;
    }

    protected static JScrollPane createNewScrollPane(Container container) {
        // create a scrollpane; modify its summary as desired
        JScrollPane scrollPane = new JBScrollPane(container);
        scrollPane.getVerticalScrollBar().setValue(0);
        return scrollPane;
    }
}
