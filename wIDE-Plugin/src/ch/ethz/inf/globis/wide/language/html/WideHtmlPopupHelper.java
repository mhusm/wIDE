package ch.ethz.inf.globis.wide.language.html;

import ch.ethz.inf.globis.wide.lookup.io.WideQueryResponse;
import ch.ethz.inf.globis.wide.ui.components.list.WideSuggestionListView;
import ch.ethz.inf.globis.wide.ui.components.panel.WideSuggestionJFXPanel;
import ch.ethz.inf.globis.wide.ui.listener.WideMouseEventListenerHelper;
import ch.ethz.inf.globis.wide.ui.components.popup.WidePopupHelper;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.progress.AsynchronousExecution;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.psi.PsiElement;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebView;

import javax.swing.*;
import java.awt.*;

/**
 * Created by fabian on 12.05.16.
 */
public class WideHtmlPopupHelper extends WidePopupHelper {

    private static final WideHtmlPopupHelper INSTANCE = new WideHtmlPopupHelper();

    private WideHtmlPopupHelper() {

    }

    public static WideHtmlPopupHelper getInstance() {
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
        WebView webView = createWebView();
        webView.getEngine().loadContent("<html><body>" + result.getMdn().getSummary() + "</body></html>");

        Scene scene = new Scene(webView);
        panel.setScene(scene);
    }

//    private void showHtmlNewTagLookupResults(WideQueryResponse result, Editor editor) {
//        // Show appropriate content of a new HTML Tag
//        JEditorPane editorPane = createNewEditorPane("<html><body>" + result.getMdn().getExamples() + "</body></html>", buildHtmlEdiorKit());
//        JScrollPane scrollPane = createNewScrollPane(editorPane);
//        showPopup(scrollPane, new Dimension(600, 200), "Lookup Results", editor);
//    }

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
        WebView webView = createWebView();
        webView.getEngine().loadContent("<html><body>" + result.getMdn().getSummary() + "</body></html>");

        Scene scene = new Scene(webView);
        panel.setScene(scene);
    }

    public void showSuggestions(java.util.List<WideQueryResponse> suggestions, ToolWindow toolWindow, PsiElement element, Editor editor) {
        toolWindow.getContentManager().removeAllContents(true);
        WideSuggestionJFXPanel panel = new WideSuggestionJFXPanel();

        // run in JavaFX Thread
        Platform.runLater(new Runnable() {
            public void run() {
                createHtmlSuggestionContentFx(suggestions, panel, element, editor, toolWindow);
            }
        });

        if (suggestions != null && suggestions.size() > 0) {
            showPopup(panel, new Dimension(600, 200), null, editor);
            panel.requestFocus();

        } else {
            hidePopup(editor);
        }

    }

    @AsynchronousExecution
    private void createHtmlSuggestionContentFx(java.util.List<WideQueryResponse> suggestions, WideSuggestionJFXPanel panel, PsiElement element, Editor editor, ToolWindow window) {
        WideSuggestionListView list = new WideSuggestionListView(suggestions, panel, element, editor, window);
        panel.setList(list, editor);
    }
}
