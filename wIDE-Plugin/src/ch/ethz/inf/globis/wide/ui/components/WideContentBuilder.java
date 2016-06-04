package ch.ethz.inf.globis.wide.ui.components;

import ch.ethz.inf.globis.wide.ui.components.panel.WideJFXPanel;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import javafx.scene.web.WebView;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;

/**
 * Created by fabian on 18.04.16.
 */
public class WideContentBuilder {

    protected WideJFXPanel addNewJFXPanleToWindow(String title, ToolWindow toolWindow) {
        WideJFXPanel panel = new WideJFXPanel();

        // set panel to toolWindow
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content summaryContent = contentFactory.createContent(panel, title, false);

        toolWindow.getContentManager().removeAllContents(true);
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
