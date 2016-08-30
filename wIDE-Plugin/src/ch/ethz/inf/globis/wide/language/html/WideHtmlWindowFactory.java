package ch.ethz.inf.globis.wide.language.html;

import ch.ethz.inf.globis.wide.io.query.WideQueryResponse;
import ch.ethz.inf.globis.wide.ui.components.panel.WideJFXPanel;
import ch.ethz.inf.globis.wide.ui.components.panel.WideResizablePaneBox;
import ch.ethz.inf.globis.wide.ui.components.window.WideWindowFactory;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.progress.AsynchronousExecution;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.psi.PsiElement;
import javafx.application.Platform;
import javafx.geometry.*;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

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
        WideJFXPanel panel = getJFXPanel(toolWindow);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
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
}
