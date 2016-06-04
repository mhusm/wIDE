package ch.ethz.inf.globis.wide.language.javascript;

import ch.ethz.inf.globis.wide.io.query.WideQueryResponse;
import ch.ethz.inf.globis.wide.sources.caniuse.WideCaniuseResult;
import ch.ethz.inf.globis.wide.sources.mdn.WideMDNExample;
import ch.ethz.inf.globis.wide.sources.mdn.WideMDNResult;
import ch.ethz.inf.globis.wide.ui.components.editor.WideExampleEditorFactory;
import ch.ethz.inf.globis.wide.ui.components.panel.WideJFXPanel;
import ch.ethz.inf.globis.wide.ui.components.panel.WideResizablePane;
import ch.ethz.inf.globis.wide.ui.components.panel.WideResizablePaneBox;
import ch.ethz.inf.globis.wide.ui.components.window.WideWindowFactory;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.psi.PsiElement;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.web.WebView;

import javax.swing.*;
import javax.swing.text.html.HTMLEditorKit;
import java.util.List;

/**
 * Created by fabian on 16.04.16.
 */
public class WideJSWindowFactory extends WideWindowFactory {

    private static final WideJSWindowFactory INSTANCE = new WideJSWindowFactory();

    private WideJSWindowFactory() {

    }

    public static WideJSWindowFactory getInstance() {
        return INSTANCE;
    }

    public void showLookupWindow(ToolWindow toolWindow, WideQueryResponse result) {
        WideJFXPanel panel = addNewJFXPanleToWindow("wIDE", toolWindow);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                WideQueryResponse firstResult = null;
                ChoiceBox cb = new ChoiceBox();
                for (WideQueryResponse resp : result.getSubResults()) {
                    cb.getItems().add(resp.getKey());

                    // select first result
                    if (firstResult == null && resp.getDocumentation("mdn") != null) {
                        firstResult = resp;
                        cb.setValue(resp.getKey());
                    }
                }

                // Show new Content, when selection changes (receiver of function)
                cb.valueProperty().addListener(new ChangeListener() {
                    @Override
                    public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                        for (WideQueryResponse resp : result.getSubResults()) {
                            if (resp.getKey().equals(newValue)) {
                                showContent(result.getKey(), resp, cb, panel);
                            }
                        }
                    }
                });

                // Show child (if available) otherwise show parent
                if (firstResult != null) {
                    showContent(result.getKey(), firstResult, cb, panel);
                } else {
                    showContent(result.getKey(), result, cb, panel);
                }
            }
        });
    }

    private void showContent(String functionName, WideQueryResponse resp, ChoiceBox cb, WideJFXPanel panel) {
        WideResizablePaneBox paneBox = new WideResizablePaneBox();
        StackPane.setMargin(paneBox, new Insets(40, 0, 0, 0));
        resp.showDocumentation(paneBox);

        // layout for choicebox
        StackPane.setAlignment(cb, Pos.TOP_RIGHT);
        StackPane.setMargin(cb, new Insets(5, 10, 5, 0));

        // title
        Text title = new Text("." + functionName + "()");
        title.setStyle("-fx-font-size: 20px; -fx-fill: #333333");
        StackPane.setAlignment(title, Pos.TOP_LEFT);
        StackPane.setMargin(title, new Insets(10, 0, 0, 10));

        StackPane.setMargin(paneBox, new Insets(40, 0, 0, 0));
        panel.getEmptyContentPane().getChildren().addAll(paneBox, cb, title);
    }

    public void showSuggestionWindow(WideQueryResponse suggestion, ToolWindow toolWindow, PsiElement element, Editor editor) {
        //TODO
    }

    private void createExamplesContent(List<WideMDNExample> examples, HTMLEditorKit kit, ToolWindow toolWindow) {
        //TODO: switch to JavaFx
        JPanel parentPanel = new JPanel();
        parentPanel.setLayout(new BoxLayout(parentPanel, BoxLayout.Y_AXIS));

        for (WideMDNExample example : examples) {
            parentPanel.add(createNewEditorPane(example.getResult(), kit));
            Editor editor = WideExampleEditorFactory.createExampleEditor(example.getCode(), null);
            parentPanel.add(editor.getComponent());
        }

        JScrollPane scrollPane = createNewScrollPane(parentPanel);
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content summaryContent = contentFactory.createContent(scrollPane, "examples", false);

        toolWindow.getContentManager().addContent(summaryContent);
    }

//    private Tab createCaniuseTabFx(WideCaniuseResult content) {
//        WebView webView = createWebView();
//        webView.getEngine().loadContent("<html><body>" + content + "</body></html>");
//
//        Tab tab = new Tab("CanIUse Compatibility", webView);
//        tab.setClosable(false);
//        return tab;
//    }

}
