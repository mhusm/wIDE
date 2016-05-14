package ch.ethz.inf.globis.wide.ui.components.window;

import ch.ethz.inf.globis.wide.lookup.io.WideQueryResponse;
import ch.ethz.inf.globis.wide.ui.components.WideContentBuilder;
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
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.web.WebView;

import java.awt.*;

/**
 * Created by fabian on 01.04.16.
 */
public abstract class WideWindowFactory extends WideContentBuilder implements ToolWindowFactory {

    private ToolWindow myToolWindow;
    private JFXPanel myToolWindowContent;

    public void createErrorWindowContent(ToolWindow toolWindow, String error) {
        //TODO: implementation
        // set description text
//        JLabel text = new JLabel(error, SwingConstants.CENTER);
//        text.setAlignmentX(Component.CENTER_ALIGNMENT);
//
//        // use box to center image + text
//        Box box = new Box(BoxLayout.Y_AXIS);
//        box.setAlignmentX(JComponent.CENTER_ALIGNMENT);
//        box.setPreferredSize(new Dimension(100, 100));
//        box.setMaximumSize(new Dimension(100, 100));
//        box.add(Box.createVerticalGlue());
//        box.add(text);
//        box.add(Box.createVerticalGlue());
//
//        // set panel to toolWindow
//        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
//        Content summaryContent = contentFactory.createContent(box, "Error", false);
//
//        toolWindow.getContentManager().addContent(summaryContent);
    }

    // Create the tool window content.
    public void createToolWindowContent(Project project, ToolWindow toolWindow) {
        this.myToolWindow = toolWindow;
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();

        Content content = contentFactory.createContent(myToolWindowContent, "", false);
        toolWindow.getContentManager().addContent(content);
        toolWindow.getComponent().setMinimumSize(new Dimension(100, 0));
    }

    @AsynchronousExecution
    protected void initWideGUI() {
        myToolWindowContent = new JFXPanel();

        Text text = new Text("Search documentation with ⌥F");
        text.setTextAlignment(TextAlignment.CENTER);

        Image img = new Image(WideWindowFactory.class.getResource("/logo.png").toString());//create an image
        ImageView v = new ImageView(img);//create an imageView and pass the image

        StackPane stackPane = new StackPane();
        stackPane.setPrefSize(700, 700);

        VBox vbox = new VBox(5);
        vbox.setAlignment(Pos.CENTER);
        vbox.setMaxSize(100, 100);
        vbox.getChildren().addAll(v, text);

        stackPane.getChildren().add(vbox);
        StackPane.setAlignment(vbox, Pos.CENTER);

        myToolWindowContent.setScene(new Scene(stackPane));

        Platform.setImplicitExit(false);

    }

    protected void createSummaryContent(String text, ToolWindow toolWindow) {
        JFXPanel panel = addNewJFXPanleToWindow("Summary", toolWindow);

        // wait for JavaFX to be ready
        Platform.runLater(new Runnable() {
            public void run() {
                createSummaryContentFx(text, panel);
            }
        });
    }

    @AsynchronousExecution
    private void createSummaryContentFx(String text, JFXPanel panel) {
        WebView webView = createWebView();
        webView.getEngine().loadContent("<html><body>" + text + "</body></html>");

        Scene scene = new Scene(webView);
        panel.setScene(scene);
    }

    protected void createAttributesContent(String text, ToolWindow toolWindow) {
        JFXPanel panel = addNewJFXPanleToWindow("Attributes", toolWindow);

        // wait for JavaFX to be ready
        Platform.runLater(new Runnable() {
            public void run() {
                createAttributesContentFx(text, panel);
            }
        });

    }

    @AsynchronousExecution
    private void createAttributesContentFx(String text, JFXPanel panel) {
        WebView webView = createWebView();
        webView.getEngine().loadContent("<html><body>" + text + "</body></html>");

        Scene scene = new Scene(webView);
        panel.setScene(scene);
    }

    protected void createCompatibilityContent(String text, ToolWindow toolWindow) {
        JFXPanel panel = addNewJFXPanleToWindow("Compatibility", toolWindow);

        // wait for JavaFX to be ready
        Platform.runLater(new Runnable() {
            public void run() {
                createCompatibilityContentFx(text, panel);
            }
        });
    }

    @AsynchronousExecution
    private void createCompatibilityContentFx(String text, JFXPanel panel) {
        WebView webView = createWebView();
        webView.getEngine().loadContent("<html><body>" + text + "</body></html>");

        Scene scene = new Scene(webView);
        panel.setScene(scene);
    }

    protected void createSyntaxContent(String text, ToolWindow toolWindow) {
        JFXPanel panel = addNewJFXPanleToWindow("Syntax", toolWindow);

        // wait for JavaFX to be ready
        Platform.runLater(new Runnable() {
            public void run() {
                createSyntaxContentFx(text, panel);
            }
        });
    }

    @AsynchronousExecution
    private void createSyntaxContentFx(String text, JFXPanel panel) {
        WebView webView = createWebView();
        webView.getEngine().loadContent("<html><body>" + text + "</body></html>");

        Scene scene = new Scene(webView);
        panel.setScene(scene);
    }

    public abstract void showLookupWindow(ToolWindow toolWindow, WideQueryResponse result);
    public abstract void showSuggestionWindow(WideQueryResponse suggestion, ToolWindow toolWindow, PsiElement element, Editor editor);

}
