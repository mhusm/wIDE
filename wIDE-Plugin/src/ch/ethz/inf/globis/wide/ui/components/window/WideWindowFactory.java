package ch.ethz.inf.globis.wide.ui.components.window;

import ch.ethz.inf.globis.wide.compatibility.WideCompatibilityTraverser;
import ch.ethz.inf.globis.wide.io.query.WideQueryResponse;
import ch.ethz.inf.globis.wide.ui.components.WideContentBuilder;
import ch.ethz.inf.globis.wide.ui.components.panel.WideJFXPanel;
import com.intellij.ide.IdeEventQueue;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.openapi.editor.VisualPosition;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.progress.AsynchronousExecution;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.psi.PsiElement;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.awt.Dimension;
import java.util.*;


/**
 * Created by fabian on 01.04.16.
 */
public abstract class WideWindowFactory extends WideContentBuilder implements ToolWindowFactory {

    private JFXPanel myToolWindowContent;

    protected WideJFXPanel getJFXPanel(ToolWindow window) {
        for (Content content : window.getContentManager().getContents()) {
            if (content.getComponent() instanceof WideJFXPanel) {
                return (WideJFXPanel) content.getComponent();
            }
        }

        window.getContentManager().removeAllContents(true);
        return addNewJFXPanelToWindow("wIDE", window);
    }

    public void showErrorWindow(String error, ToolWindow toolWindow) {
        WideJFXPanel panel = getJFXPanel(toolWindow);

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

    public void showCompatibilityIssues(TreeMap<Double, Map> issues, ToolWindow toolWindow, Editor editor) {
        WideJFXPanel panel = getJFXPanel(toolWindow);

        // wait for JavaFX to be ready
        Platform.runLater(new Runnable() {
            public void run() {
                createCompatibilityIssuesContentFx(issues, panel.getEmptyContentPane(), editor);
            }
        });
    }

    @AsynchronousExecution
    private void createCompatibilityIssuesContentFx(TreeMap<Double, Map> issues, Pane pane, Editor editor) {
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        VBox box = new VBox();
        box.setStyle("-fx-padding: 10px;");
        scrollPane.setContent(box);

        for (Double compatibility : issues.navigableKeySet()) {
            // ORDER BY COMPATIBILITY
            Map<String, Set<PsiElement>> subIssues = issues.get(compatibility);
            for (String key : subIssues.keySet()) {
                // ORDER BY ISSUE
                java.awt.Color highlightColor = WideCompatibilityTraverser.getHighlightColor(compatibility);

                StackPane stackPane = new StackPane();
                stackPane.setStyle("-fx-padding: 10px; " +
                        "-fx-background-color: white; " +
                        "-fx-border-color: #EEEEEE #EEEEEE #EEEEEE " + String.format("#%02X%02X%02X", highlightColor.getRed(), highlightColor.getGreen(), highlightColor.getBlue()) + "; " +
                        "-fx-border-width: 1px 1px 1px 5px; " +
                        "-fx-start-margin: 10px;");
                Text text = new Text(key);
                stackPane.getChildren().add(text);
                StackPane.setAlignment(text, Pos.CENTER_LEFT);
                stackPane.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                            if (mouseEvent.getClickCount() == 2) {
                                IdeEventQueue.getInstance().doWhenReady(new Runnable() {
                                    @Override
                                    public void run() {
                                        //TODO: DO DOCUMENTATION LOOKUP.
                                    }
                                });
                            }
                        }
                    }
                });
                box.getChildren().add(stackPane);

                for (PsiElement element : ((WideCompatibilityTraverser.WidePsiElementRequestEntry) subIssues.get(key)).getElements()) {
                    // ORDER BY OCCURRE

                    java.awt.Color subHighlightColor = WideCompatibilityTraverser.getHighlightColor(compatibility);

                    StackPane subStackPane = new StackPane();
                    subStackPane.setStyle("-fx-padding: 3px 10px 3px 20px; " +
                            "-fx-background-color: #f3f3f3; " +
                            "-fx-border-color: #EEEEEE #EEEEEE #EEEEEE " + String.format("#%02X%02X%02X", subHighlightColor.getRed(), subHighlightColor.getGreen(), subHighlightColor.getBlue()) + "; " +
                            "-fx-border-width: 1px 1px 1px 5px;");
                    Text subText = new Text(element.getText() + " (" + element.getContainingFile().getName() + ")");
                    subStackPane.getChildren().add(subText);
                    subStackPane.setAlignment(subText, Pos.CENTER_LEFT);
                    subStackPane.setOnMouseClicked(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent mouseEvent) {
                            if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                                if (mouseEvent.getClickCount() == 2) {
                                    IdeEventQueue.getInstance().doWhenReady(new Runnable() {
                                        @Override
                                        public void run() {
                                            FileEditorManager.getInstance(element.getProject()).openFile(element.getContainingFile().getVirtualFile(), true);
                                            Editor ed = FileEditorManager.getInstance(element.getProject()).getSelectedTextEditor();
                                            ed.getSelectionModel().removeSelection();
                                            ed.getSelectionModel().setSelection(element.getTextRange().getStartOffset(), element.getTextRange().getEndOffset());
                                            VisualPosition posStart = ed.getSelectionModel().getLeadSelectionPosition();
                                            ed.getCaretModel().moveToVisualPosition(posStart);
                                            ed.getScrollingModel().scrollToCaret(ScrollType.MAKE_VISIBLE); //.scrollTo(element.getTextRange().);

                                        }
                                    });
                                }
                            }
                        }
                    });

                    box.getChildren().add(subStackPane);
                }
            }
        }


        pane.getChildren().add(scrollPane);
    }

    public void showWaitingWindow(ToolWindow toolWindow) {
        WideJFXPanel panel = getJFXPanel(toolWindow);

        // wait for JavaFX to be ready
        Platform.runLater(new Runnable() {
            public void run() {
                createWaitingWindowContentFX(panel.getEmptyContentPane());
            }
        });
    }

    @AsynchronousExecution
    private void createWaitingWindowContentFX(Pane pane) {
        // add the waiting image
        Image image = new Image(this.getClass().getResource("/images/loading.gif").toString());
        ImageView imageView = new ImageView();
        imageView.setImage(image);
        pane.getChildren().add(imageView);
        StackPane.setAlignment(imageView, Pos.CENTER);
    }

    @AsynchronousExecution
    protected void initWideGUI() {
        myToolWindowContent = new WideJFXPanel();
        Platform.setImplicitExit(false);

    }

    public abstract void showLookupWindow(ToolWindow toolWindow, WideQueryResponse result);

    public abstract void showSuggestionWindow(WideQueryResponse suggestion, ToolWindow toolWindow, PsiElement element, Editor editor);

}
