package ch.ethz.inf.globis.wide.ui.window;

import ch.ethz.inf.globis.wide.ui.components.WideContentBuilder;
import ch.ethz.inf.globis.wide.ui.components.WideImagePanel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;

import javax.swing.*;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import java.awt.*;

/**
 * Created by fabian on 01.04.16.
 */
public class WideWindowFactory extends WideContentBuilder implements ToolWindowFactory {
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
        // set description text
        JLabel text = new JLabel(error, SwingConstants.CENTER);
        text.setAlignmentX(Component.CENTER_ALIGNMENT);

        // use box to center image + text
        Box box = new Box(BoxLayout.Y_AXIS);
        box.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        box.setPreferredSize(new Dimension(100, 100));
        box.setMaximumSize(new Dimension(100, 100));
        box.add(Box.createVerticalGlue());
        box.add(text);
        box.add(Box.createVerticalGlue());

        // set panel to toolWindow
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content summaryContent = contentFactory.createContent(box, "Error", false);

        toolWindow.getContentManager().addContent(summaryContent);
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
