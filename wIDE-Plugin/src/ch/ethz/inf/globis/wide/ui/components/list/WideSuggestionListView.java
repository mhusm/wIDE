package ch.ethz.inf.globis.wide.ui.components.list;

import ch.ethz.inf.globis.wide.lookup.io.WideQueryResponse;
import ch.ethz.inf.globis.wide.ui.action.WideReplaceAction;
import ch.ethz.inf.globis.wide.ui.components.WideContentBuilder;
import ch.ethz.inf.globis.wide.ui.components.WideDataContext;
import ch.ethz.inf.globis.wide.language.html.WideHtmlSuggestionCell;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.embed.swing.JFXPanel;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.web.WebView;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * Created by fabian on 08.05.16.
 */
public class WideSuggestionListView extends ListView<WideQueryResponse> {

    public WideSuggestionListView(List<WideQueryResponse> suggestions, Class<? extends WideSuggestionCell> cellClass, JFXPanel panel, Editor editor, ToolWindow window) {
        super(FXCollections.observableArrayList(suggestions));
        this.setCellFactory(listView -> setCellFactory(cellClass, panel));


        if (suggestions != null && suggestions.size() > 0) {
            this.getSelectionModel().select(0);
        }

        // Add selection-listener to display additional info in sidebar
        addSelectionListener(window);

        // add double click listener
        addDoubleClickListener(editor);

    }

    private WideSuggestionCell setCellFactory(Class<? extends WideSuggestionCell> cellClass, JFXPanel panel) {
        // Helper class to allow insertion of callback
        try {
            WideSuggestionCell cell = cellClass.getConstructor(JFXPanel.class).newInstance(panel);
            return cell;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void addSelectionListener(ToolWindow window) {
        // Add selection-listener to display additional info in sidebar
        this.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<WideQueryResponse>() {

            @Override
            public void changed(ObservableValue<? extends WideQueryResponse> observable, WideQueryResponse oldValue, WideQueryResponse newValue) {
                window.getContentManager().removeAllContents(true);

                JFXPanel panel = new JFXPanel();

                // run in JFX Thread
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        WebView webView = WideContentBuilder.createWebView();
                        webView.getEngine().loadContent("<html><body>" + newValue.getMdn().getSummary() + "</body></html>");

                        Scene scene = new Scene(webView);
                        scene.getStylesheets().add(WideSuggestionListView.class.getResource("/WideStyleSheet.css").toExternalForm());
                        panel.setScene(scene);
                    }
                });

                ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
                Content summaryContent = contentFactory.createContent(panel, newValue.getKey(), false);

                window.getContentManager().addContent(summaryContent);
            }
        });
    }

    private void addDoubleClickListener(Editor editor) {
        this.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent click) {

                if (click.getClickCount() == 2) {

                    String name = getSelectionModel().getSelectedItem().getKey();

                    // Run in JFX Thread
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            PsiFile psiFile = PsiDocumentManager.getInstance(editor.getProject()).getPsiFile(editor.getDocument());
                            PsiElement element = psiFile.findElementAt(editor.getCaretModel().getOffset());
                            int start = element.getTextOffset() + 1;
                            int end = element.getTextOffset() + element.getTextLength() - 1;
                            WideDataContext context = new WideDataContext();
                            context.setData("text", name + "=\"\"");
                            context.setData("startPosition", start);
                            context.setData("endPosition", end);

                            // Run from Event Dispatch Thread
                            ApplicationManager.getApplication().runWriteAction(new Runnable() {
                                @Override
                                public void run() {
                                    WideReplaceAction action = new WideReplaceAction();
                                    action.actionPerformed(editor, context);
                                }
                            });
                        }
                    });
                }
            }
        });
    }

    public void insertSuggestionAtIndex(int index, Editor editor) {
        ((WideSuggestionCell) this.getCellFactory().call(this)).insertSuggestion(this.getSelectionModel().getSelectedItem(), editor);
    }
}
