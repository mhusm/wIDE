package ch.ethz.inf.globis.wide.language.css;

import ch.ethz.inf.globis.wide.lookup.io.WideQueryResponse;
import ch.ethz.inf.globis.wide.lookup.io.mdn.WideMDNExample;
import ch.ethz.inf.globis.wide.ui.components.editor.WideExampleEditorFactory;
import ch.ethz.inf.globis.wide.ui.components.window.WideWindowFactory;
import com.intellij.ide.highlighter.HtmlHighlighterFactory;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.psi.PsiElement;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;

import javax.swing.*;
import javax.swing.text.html.HTMLEditorKit;
import java.util.List;

/**
 * Created by fabian on 16.04.16.
 */
public class WideCssWindowFactory extends WideWindowFactory {

    private static final WideCssWindowFactory INSTANCE = new WideCssWindowFactory();

    private WideCssWindowFactory() {

    }

    public static WideCssWindowFactory getInstance() {
        return INSTANCE;
    }

    public void showLookupWindow(ToolWindow toolWindow, WideQueryResponse result) {
        HTMLEditorKit kit = buildHtmlEdiorKit();
        toolWindow.getContentManager().removeAllContents(true);
        createSummaryContent(result.getMdn().getSummary(), toolWindow);
        createSyntaxContent(result.getMdn().getSyntax(), toolWindow);
        createExamplesContent(result.getMdn().getExamples(), kit, toolWindow);
        createCompatibilityContent(result.getMdn().getCompatibility(), toolWindow);
    }

    public void showSuggestionWindow(WideQueryResponse suggestion, ToolWindow toolWindow, PsiElement element, Editor editor) {
        //TODO
    }

    private void createExamplesContent(List<WideMDNExample> examples, HTMLEditorKit kit, ToolWindow toolWindow) {
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
