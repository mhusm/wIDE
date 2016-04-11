package ch.ethz.inf.globis.wide.ui.window;

import ch.ethz.inf.globis.wide.parsing.WideQueryResult;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;

import javax.swing.*;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by fabian on 01.04.16.
 */
public class WideWindowFactory implements ToolWindowFactory {
    private JButton refreshToolWindowButton;
    private JButton hideToolWindowButton;
    private JLabel currentDate;
    private JPanel myToolWindowContent;
    private ToolWindow myToolWindow;


    public WideWindowFactory() {
        hideToolWindowButton = new JButton();
        refreshToolWindowButton = new JButton();
        currentDate = new JLabel();
        currentDate.setText("Hello, this is the wIDE Panel.");
        myToolWindowContent = new JPanel();
        myToolWindowContent.add(currentDate);

        hideToolWindowButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                myToolWindow.hide(null);
            }
        });
        refreshToolWindowButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //WideWindowFactory.this.currentDateTime();
            }
        });
    }

    // Create the tool window content.
    public void createToolWindowContent(Project project, ToolWindow toolWindow) {
        myToolWindow = toolWindow;
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();

        Content content = contentFactory.createContent(myToolWindowContent, "", false);
        toolWindow.getContentManager().addContent(content);

    }

    public static void createHTMLWindowContent(ToolWindow toolWindow, WideQueryResult result) {
        HTMLEditorKit kit = createHtmlEditorKit();
        toolWindow.getContentManager().removeAllContents(true);
        createSummaryContent(result.getMdn().getSummary(), kit, toolWindow);
        createAttributesContent(result.getMdn().getAttributes(), kit, toolWindow);
        createExamplesContent(result.getMdn().getExamples(), kit, toolWindow);
        createCompatibilityContent(result.getMdn().getCompatibility(), kit, toolWindow);
    }

    public static void createCSSWindowContent(ToolWindow toolWindow, WideQueryResult result) {
        // TODO;
    }

    public static void createJSWindowContent(ToolWindow toolWindow, WideQueryResult result) {
        HTMLEditorKit kit = createHtmlEditorKit();
        toolWindow.getContentManager().removeAllContents(true);
        createSyntaxContent(result.getMdn().getSyntax(), kit, toolWindow);
        createExamplesContent(result.getMdn().getExamples(), kit, toolWindow);
        createCompatibilityContent(result.getMdn().getCompatibility(), kit, toolWindow);
    }

    private static HTMLEditorKit createHtmlEditorKit() {
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

    private static JEditorPane createNewEditorPane(String content, HTMLEditorKit kit) {
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

        return editorPane;
    }

    private static JScrollPane createNewScrollPane(JEditorPane editorPane, HTMLEditorKit kit) {

        // create a scrollpane; modify its summary as desired
        JScrollPane summaryScrollPane = new JBScrollPane(editorPane);
        return summaryScrollPane;
    }

    private static void createSummaryContent(String text, HTMLEditorKit kit, ToolWindow toolWindow) {
        JEditorPane editorPane = createNewEditorPane("<html><body>" + text + "</body></html>", kit);
        JScrollPane scrollPane = createNewScrollPane(editorPane, kit);
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content summaryContent = contentFactory.createContent(scrollPane, "summary", false);

        toolWindow.getContentManager().addContent(summaryContent);
    }

    private static void createAttributesContent(String text, HTMLEditorKit kit, ToolWindow toolWindow) {
        JEditorPane editorPane = createNewEditorPane("<html><body>" + text + "</body></html>", kit);
        JScrollPane scrollPane = createNewScrollPane(editorPane, kit);
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content summaryContent = contentFactory.createContent(scrollPane, "attributes", false);

        toolWindow.getContentManager().addContent(summaryContent);
    }

    private static void createExamplesContent(String text, HTMLEditorKit kit, ToolWindow toolWindow) {
        JEditorPane editorPane = createNewEditorPane("<html><body>" + text + "</body></html>", kit);
        JScrollPane scrollPane = createNewScrollPane(editorPane, kit);
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content summaryContent = contentFactory.createContent(scrollPane, "examples", false);

        toolWindow.getContentManager().addContent(summaryContent);
    }

    private static void createCompatibilityContent(String text, HTMLEditorKit kit, ToolWindow toolWindow) {
        JEditorPane editorPane = createNewEditorPane("<html><body>" + text + "</body></html>", kit);
        JScrollPane scrollPane = createNewScrollPane(editorPane, kit);
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content summaryContent = contentFactory.createContent(scrollPane, "compatibility", false);

        toolWindow.getContentManager().addContent(summaryContent);
    }

    private static void createSyntaxContent(String text, HTMLEditorKit kit, ToolWindow toolWindow) {
        JEditorPane editorPane = createNewEditorPane("<html><body>" + text + "</body></html>", kit);
        JScrollPane scrollPane = createNewScrollPane(editorPane, kit);
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content summaryContent = contentFactory.createContent(scrollPane, "syntax", false);

        toolWindow.getContentManager().addContent(summaryContent);
    }
}
