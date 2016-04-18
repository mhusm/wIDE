package ch.ethz.inf.globis.wide.ui.window;

import ch.ethz.inf.globis.wide.ui.components.WideImagePanel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import javax.swing.*;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import java.awt.*;

/**
 * Created by fabian on 01.04.16.
 */
public class WideWindowFactory implements ToolWindowFactory {
    private Box box;
    private JLabel text;
    private WideImagePanel logo;
    private JPanel myToolWindowContent;
    private ToolWindow myToolWindow;


    public WideWindowFactory() {
        // load image
        logo = new WideImagePanel(WideWindowFactory.class.getResource("/logo.png"));
        logo.setPreferredSize(new Dimension(logo.getImageWidth(), logo.getImageHeight()));

        // set description text
        text = new JLabel("Search documentation with ⌥F", SwingConstants.CENTER);
        text.setAlignmentX(Component.CENTER_ALIGNMENT);

        // use box to center image + text
        box = new Box(BoxLayout.Y_AXIS);
        box.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        box.setPreferredSize(new Dimension(100, 100));
        box.setMaximumSize(new Dimension(100, 100));
        box.add(Box.createVerticalGlue());
        box.add(logo);
        box.add(text);
        box.add(Box.createVerticalGlue());

        // set panel to toolWindow
        myToolWindowContent = new JPanel();
        myToolWindowContent.setLayout(new BorderLayout());
        myToolWindowContent.setBackground(Color.white);
        myToolWindowContent.add(box, BorderLayout.CENTER);
    }

    // Create the tool window content.
    public void createToolWindowContent(Project project, ToolWindow toolWindow) {
        myToolWindow = toolWindow;
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();

        Content content = contentFactory.createContent(myToolWindowContent, "", false);
        toolWindow.getContentManager().addContent(content);
        toolWindow.getComponent().setMinimumSize(new Dimension(100, 0));
    }

    public static void createErrorWindowContent(ToolWindow toolWindow, String error) {
        //TODO: implementation
    }

    static HTMLEditorKit buildHtmlEdiorKit() {
        // add an html editor kit
        HTMLEditorKit kit = new HTMLEditorKit();

        // add some styles to the html
        StyleSheet styleSheet = kit.getStyleSheet();
//        styleSheet.addRule("body { " +
//                "font-family: Calibri; " +
//                "padding: 10px; }");
        try {
            styleSheet.importStyleSheet(WideWindowFactory.class.getResource("/MDNStyleSheet.css"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return kit;
    }

    static JEditorPane createNewEditorPane(String content, HTMLEditorKit kit) {
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

//        Group root = new Group();
//        Scene scene = new Scene(root);
//
//        root.getChildren().add(webView);
//        JFXPanel jfxPanel = new JFXPanel();
//        jfxPanel.setScene(scene);

        return editorPane;
    }

    static JScrollPane createNewScrollPane(Container container) {
        // create a scrollpane; modify its summary as desired
        JScrollPane summaryScrollPane = new JBScrollPane(container);
        return summaryScrollPane;
    }

    static void createSummaryContent(String text, HTMLEditorKit kit, ToolWindow toolWindow) {
        JEditorPane editorPane = createNewEditorPane("<html><body>" + text + "</body></html>", kit);
        JScrollPane scrollPane = createNewScrollPane(editorPane);
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content summaryContent = contentFactory.createContent(scrollPane, "summary", false);

        toolWindow.getContentManager().addContent(summaryContent);
    }

    static void createAttributesContent(String text, HTMLEditorKit kit, ToolWindow toolWindow) {
        JEditorPane editorPane = createNewEditorPane("<html><body>" + text + "</body></html>", kit);
        JScrollPane scrollPane = createNewScrollPane(editorPane);
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content summaryContent = contentFactory.createContent(scrollPane, "attributes", false);

        toolWindow.getContentManager().addContent(summaryContent);
    }

    static void createCompatibilityContent(String text, HTMLEditorKit kit, ToolWindow toolWindow) {
        JEditorPane editorPane = createNewEditorPane("<html><body>" + text + "</body></html>", kit);
        JScrollPane scrollPane = createNewScrollPane(editorPane);
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content summaryContent = contentFactory.createContent(scrollPane, "compatibility", false);

        toolWindow.getContentManager().addContent(summaryContent);
    }

    static void createSyntaxContent(String text, HTMLEditorKit kit, ToolWindow toolWindow) {
        JEditorPane editorPane = createNewEditorPane("<html><body>" + text + "</body></html>", kit);
        JScrollPane scrollPane = createNewScrollPane(editorPane);
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content summaryContent = contentFactory.createContent(scrollPane, "syntax", false);

        toolWindow.getContentManager().addContent(summaryContent);
    }
}
