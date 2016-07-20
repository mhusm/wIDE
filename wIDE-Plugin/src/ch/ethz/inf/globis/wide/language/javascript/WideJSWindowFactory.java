package ch.ethz.inf.globis.wide.language.javascript;

import ch.ethz.inf.globis.wide.io.query.WideQueryResponse;
import ch.ethz.inf.globis.wide.ui.components.panel.WideJFXPanel;
import ch.ethz.inf.globis.wide.ui.components.panel.WideResizablePaneBox;
import ch.ethz.inf.globis.wide.ui.components.window.WideWindowFactory;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.psi.PsiElement;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

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
        WideJFXPanel panel = getJFXPanel(toolWindow);

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
        Text title = new Text();
        // title
        if (resp.getType() == "reference") {
            title.setText(functionName);
        } else {
            title.setText("." + functionName + "()");
        }
        title.setStyle("-fx-font-size: 20px; -fx-fill: #333333");
        StackPane.setAlignment(title, Pos.TOP_LEFT);
        StackPane.setMargin(title, new Insets(10, 0, 0, 10));

        StackPane.setMargin(paneBox, new Insets(40, 0, 0, 0));
        panel.getEmptyContentPane().getChildren().addAll(paneBox, cb, title);
    }

    public void showSuggestionWindow(WideQueryResponse suggestion, ToolWindow toolWindow, PsiElement element, Editor editor) {
        //TODO
    }

}
