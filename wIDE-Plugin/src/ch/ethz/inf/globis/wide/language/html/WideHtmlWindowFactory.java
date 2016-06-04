package ch.ethz.inf.globis.wide.language.html;

import ch.ethz.inf.globis.wide.io.query.WideQueryResponse;
import ch.ethz.inf.globis.wide.sources.mdn.WideMDNExample;
import ch.ethz.inf.globis.wide.sources.mdn.WideMDNResult;
import ch.ethz.inf.globis.wide.ui.components.panel.WideFullWidthPanel;
import ch.ethz.inf.globis.wide.ui.components.editor.WideExampleEditorFactory;
import ch.ethz.inf.globis.wide.ui.components.panel.WideJFXPanel;
import ch.ethz.inf.globis.wide.ui.components.panel.WideResizablePaneBox;
import ch.ethz.inf.globis.wide.ui.components.window.WideWindowFactory;
import com.intellij.ide.highlighter.HtmlHighlighterFactory;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.progress.AsynchronousExecution;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.psi.PsiElement;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import javafx.application.Platform;
import javafx.geometry.*;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TabPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

import javax.swing.*;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.awt.Insets;
import java.util.List;

/**
 * Created by fabian on 16.04.16.
 */
public class WideHtmlWindowFactory extends WideWindowFactory {

    private static final WideHtmlWindowFactory INSTANCE = new WideHtmlWindowFactory();

    private WideHtmlWindowFactory() {

    }

    public static WideHtmlWindowFactory getInstance() {
        return INSTANCE;
    }

    public void showLookupWindow(ToolWindow toolWindow, WideQueryResponse result) {
        WideJFXPanel panel = addNewJFXPanleToWindow("wIDE", toolWindow);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
//                TabPane tabPane = new TabPane();
//                tabPane.setStyle("-fx-background-color: darkslategray");
//                tabPane.getTabs().add(createSummaryTabFx(((WideMDNResult) result.getDocumentation("mdn")).getSummary()));
//                tabPane.getTabs().add(createAttributesTabFx(((WideMDNResult) result.getDocumentation("mdn")).getAttributes()));
//                tabPane.getTabs().add(createCompatibilityTabFx(((WideMDNResult) result.getDocumentation("mdn")).getCompatibility()));
//                //tabPane.getTabs().add(createExamplesContent(result.getMdn().getExamples()));

                showContent(result, panel);
            }
        });
    }

    @AsynchronousExecution
    private void showContent(WideQueryResponse resp, WideJFXPanel panel) {
        WideResizablePaneBox paneBox = new WideResizablePaneBox();
        StackPane.setMargin(paneBox, new javafx.geometry.Insets(40, 0, 0, 0));
        resp.showDocumentation(paneBox);


        // title
        Text title = new Text("<" + resp.getKey() + ">");
        title.setStyle("-fx-font-size: 20px; -fx-fill: #333333");
        StackPane.setAlignment(title, Pos.TOP_LEFT);
        StackPane.setMargin(title, new javafx.geometry.Insets(10, 0, 0, 10));

        StackPane.setMargin(paneBox, new javafx.geometry.Insets(40, 0, 0, 0));
        panel.getEmptyContentPane().getChildren().addAll(paneBox, title);
    }

    public void showSuggestionWindow(WideQueryResponse suggestion, ToolWindow toolWindow, PsiElement element, Editor editor) {
        //TODO
    }

    private void createExamplesContent(List<WideMDNExample> examples, HTMLEditorKit kit, ToolWindow toolWindow) {
        //TODO: change to JavaFx
        WideFullWidthPanel parentPanel = new WideFullWidthPanel();

        for (WideMDNExample example : examples) {
            // show all Examples
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