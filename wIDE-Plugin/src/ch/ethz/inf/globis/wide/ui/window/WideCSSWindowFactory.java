package ch.ethz.inf.globis.wide.ui.window;

import ch.ethz.inf.globis.wide.parsing.WideMDNExample;
import ch.ethz.inf.globis.wide.parsing.WideQueryResult;
import ch.ethz.inf.globis.wide.ui.editor.WideExampleEditorFactory;
import com.intellij.ide.highlighter.HtmlHighlighterFactory;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;

import javax.swing.*;
import javax.swing.text.html.HTMLEditorKit;
import java.util.List;

/**
 * Created by fabian on 16.04.16.
 */
public class WideCSSWindowFactory extends WideWindowFactory {

    public static void createCSSWindowContent(ToolWindow toolWindow, WideQueryResult result) {
        // TODO: implementation
    }

    private static void createExamplesContent(List<WideMDNExample> examples, HTMLEditorKit kit, ToolWindow toolWindow) {
        JPanel parentPanel = new JPanel();
        parentPanel.setLayout(new BoxLayout(parentPanel, BoxLayout.Y_AXIS));

        for (WideMDNExample example : examples) {
            parentPanel.add(createNewEditorPane(example.getResult(), kit));
            Editor editor = WideExampleEditorFactory.createExampleEditor(example.getCode(), HtmlHighlighterFactory.createHTMLHighlighter(EditorColorsManager.getInstance().getGlobalScheme()));
            parentPanel.add(editor.getComponent());
        }

        JScrollPane scrollPane = createNewScrollPane(parentPanel);
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content summaryContent = contentFactory.createContent(scrollPane, "examples", false);

        toolWindow.getContentManager().addContent(summaryContent);
    }
}
