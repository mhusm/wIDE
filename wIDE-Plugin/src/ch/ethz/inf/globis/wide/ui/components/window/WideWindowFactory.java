package ch.ethz.inf.globis.wide.ui.components.window;

import ch.ethz.inf.globis.wide.io.query.WideQueryResponse;
import ch.ethz.inf.globis.wide.ui.components.WideContentBuilder;
import ch.ethz.inf.globis.wide.ui.components.panel.WideJFXPanel;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.progress.AsynchronousExecution;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.psi.PsiElement;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.geometry.Pos;
import javafx.scene.control.Tab;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.web.WebView;

import java.awt.*;


/**
 * Created by fabian on 01.04.16.
 */
public abstract class WideWindowFactory extends WideContentBuilder implements ToolWindowFactory {

    private JFXPanel myToolWindowContent;

    public void createErrorWindowContent(String error, ToolWindow toolWindow) {
        toolWindow.getContentManager().removeAllContents(true);
        WideJFXPanel panel = addNewJFXPanleToWindow("wIDE", toolWindow);

        // wait for JavaFX to be ready
        Platform.runLater(new Runnable() {
            public void run() {
                createErrorWindowContentFx(error, panel.getEmptyContentPane());
            }
        });

    }

    @AsynchronousExecution
    private void createErrorWindowContentFx(String error, Pane pane) {

        Text sorry = new Text(error);
        sorry.setTextAlignment(TextAlignment.CENTER);
        sorry.setStyle("-fx-fill: darkred; -fx-font-size: 20px; -fx-font-weight: lighter;");

        DropShadow ds = new DropShadow();
        ds.setOffsetY(0.5f);
        ds.setColor(Color.color(0.8f, 0.8f, 0.8f));
        sorry.setEffect(ds);

        pane.getChildren().add(sorry);
        StackPane.setAlignment(sorry, Pos.CENTER);
    }

    // Create the tool window content.
    public void createToolWindowContent(Project project, ToolWindow toolWindow) {
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();

        Content content = contentFactory.createContent(myToolWindowContent, "", false);
        toolWindow.getContentManager().addContent(content);
        toolWindow.getComponent().setMinimumSize(new Dimension(100, 0));
    }

    @AsynchronousExecution
    protected void initWideGUI() {
        myToolWindowContent = new WideJFXPanel();
        Platform.setImplicitExit(false);

    }

    @AsynchronousExecution
    protected Tab createSummaryTabFx(String content) {
        WebView webView = createWebView();
        webView.getEngine().loadContent("<html><body>" + content + "</body></html>");

        Tab tab = new Tab("Summary", webView);
        tab.setClosable(false);
        return tab;
    }

    @AsynchronousExecution
    protected Tab createAttributesTabFx(String content) {
        WebView webView = createWebView();
        webView.getEngine().loadContent("<html><body>" + content + "</body></html>");

        Tab tab = new Tab("Attributes", webView);
        tab.setClosable(false);
        return tab;
    }

    @AsynchronousExecution
    protected Tab createCompatibilityTabFx(String content) {
        WebView webView = createWebView();
        webView.getEngine().loadContent("<html><body>" + content + "</body></html>");

        Tab tab = new Tab("Compatibility", webView);
        tab.setClosable(false);
        return tab;
    }

    @AsynchronousExecution
    protected Tab createSyntaxTabFx(String content) {
        WebView webView = createWebView();
        webView.getEngine().loadContent("<html><body>" + content + "</body></html>");

        Tab tab = new Tab("Syntax", webView);
        tab.setClosable(false);
        return tab;
    }

    public abstract void showLookupWindow(ToolWindow toolWindow, WideQueryResponse result);
    public abstract void showSuggestionWindow(WideQueryResponse suggestion, ToolWindow toolWindow, PsiElement element, Editor editor);

}
