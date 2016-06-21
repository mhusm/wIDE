package ch.ethz.inf.globis.wide.language.html;

import ch.ethz.inf.globis.wide.io.query.WideQueryResponse;
import ch.ethz.inf.globis.wide.ui.listener.WideMouseEventListenerHelper;
import ch.ethz.inf.globis.wide.ui.components.popup.WidePopupFactory;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.progress.AsynchronousExecution;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;

import java.awt.*;

/**
 * Created by fabian on 12.05.16.
 */
public class WideHtmlPopupFactory extends WidePopupFactory {

    private static final WideHtmlPopupFactory INSTANCE = new WideHtmlPopupFactory();

    private WideHtmlPopupFactory() {
    }

    public static WideHtmlPopupFactory getInstance() {
        return INSTANCE;
    }

    public void showLookupResults(WideQueryResponse parentResult, WideQueryResponse result, Editor editor) {
        if (result.getType().equals("tag")) {
            showHtmlTagLookupResults(parentResult, result, editor);
        } else if (result.getType().equals("attribute")) {
            showHtmlAttributeLookupResult(parentResult, result, editor);
        }

    }

    private void showHtmlTagLookupResults(WideQueryResponse parentResult, WideQueryResponse result, Editor editor) {
        // Show appropriate content of an HTML attribute
        JFXPanel panel = new JFXPanel();
        showPopup(panel, new Dimension(300, 200), result.getKey(), editor);

        // run in JavaFX Thread
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                createHtlTagLookupResultFx(result, panel);
            }
        });

        WideMouseEventListenerHelper.getInstance().registerMouseEventListener(parentResult, result, editor);
    }

    @AsynchronousExecution
    private void createHtlTagLookupResultFx(WideQueryResponse result, JFXPanel panel) {
        result.getDocumentation("mdn").showPopup(panel);
    }

    private void showHtmlAttributeLookupResult(WideQueryResponse parentResult, WideQueryResponse result, Editor editor) {
        // Show appropriate content of an HTML attribute
        JFXPanel panel = new JFXPanel();
        showPopup(panel, new Dimension(300, 200), result.getKey(), editor);

        // run in JavaFX Thread
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                createHtlAttributeLookupResultFx(result, panel);
            }
        });

        WideMouseEventListenerHelper.getInstance().registerMouseEventListener(parentResult, result, editor);
    }

    @AsynchronousExecution
    private void createHtlAttributeLookupResultFx(WideQueryResponse result, JFXPanel panel) {
        result.getDocumentation("mdn").showPopup(panel);
    }
}
