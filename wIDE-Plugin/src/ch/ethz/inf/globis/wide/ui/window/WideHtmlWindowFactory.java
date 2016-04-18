package ch.ethz.inf.globis.wide.ui.window;

import ch.ethz.inf.globis.wide.parsing.WideMDNExample;
import ch.ethz.inf.globis.wide.parsing.WideQueryResult;
import ch.ethz.inf.globis.wide.ui.components.WideFullWidthPanel;
import ch.ethz.inf.globis.wide.ui.editor.WideExampleEditorFactory;
import com.intellij.ide.highlighter.HtmlHighlighterFactory;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.intellij.lang.annotations.JdkConstants;
import sun.font.FontManager;

import javax.swing.*;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.util.List;

/**
 * Created by fabian on 16.04.16.
 */
public class WideHtmlWindowFactory extends WideWindowFactory {

    public static void createHTMLWindowContent(ToolWindow toolWindow, WideQueryResult result) {
        HTMLEditorKit kit = buildHtmlEdiorKit();
        toolWindow.getContentManager().removeAllContents(true);
        createSummaryContent(result.getMdn().getSummary(), kit, toolWindow);
        createAttributesContent(result.getMdn().getAttributes(), kit, toolWindow);
        createExamplesContent(result.getMdn().getExamples(), kit, toolWindow);
        createCompatibilityContent(result.getMdn().getCompatibility(), kit, toolWindow);
    }

    private static void createExamplesContent(List<WideMDNExample> examples, HTMLEditorKit kit, ToolWindow toolWindow) {
        WideFullWidthPanel parentPanel = new WideFullWidthPanel();

        for (WideMDNExample example : examples) {
            if (example.getTitle() != null && !example.getTitle().equals("")) {
                // title
                JLabel title = new JLabel(example.getTitle());
                title.setFont(new Font(Font.SANS_SERIF, 0, 20));
                title.setHorizontalTextPosition(SwingConstants.LEFT);
                title.setMinimumSize(new Dimension(3000, 25));
                parentPanel.add(title);
            }

            if (example.getResult() != null && !example.getResult().equals("")) {
                // explanation & result
                WideFullWidthPanel textPanel = new WideFullWidthPanel();
                textPanel.add(createNewEditorPane("<html><body>" + example.getResult() + "</body></html>", kit));
                parentPanel.add(textPanel);
            }

            if (example.getCode() != null && !example.getCode().equals("")) {
                // code snippet
                Editor editor = WideExampleEditorFactory.createExampleEditor(example.getCode(), HtmlHighlighterFactory.createHTMLHighlighter(EditorColorsManager.getInstance().getGlobalScheme()));
                parentPanel.add(editor.getComponent());
            }

            parentPanel.add(new JSeparator());
        }

        JScrollPane scrollPane = createNewScrollPane(parentPanel);
        scrollPane.setPreferredSize(toolWindow.getComponent().getPreferredSize());
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content summaryContent = contentFactory.createContent(scrollPane, "examples", false);

        toolWindow.getContentManager().addContent(summaryContent);
    }

}
