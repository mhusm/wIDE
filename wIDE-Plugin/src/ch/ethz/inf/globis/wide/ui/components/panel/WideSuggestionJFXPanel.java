package ch.ethz.inf.globis.wide.ui.components.panel;

import ch.ethz.inf.globis.wide.ui.action.WideReplaceAction;
import ch.ethz.inf.globis.wide.ui.components.WideDataContext;
import ch.ethz.inf.globis.wide.ui.components.list.WideSuggestionListView;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Created by fabian on 11.05.16.
 */
public class WideSuggestionJFXPanel extends JFXPanel {

    public WideSuggestionJFXPanel() {
        super();
    }

    public void setList(WideSuggestionListView list, Editor editor) {
        Scene scene = new Scene(list);
        scene.getStylesheets().add(WideSuggestionJFXPanel.class.getResource("/WideStyleSheet.css").toExternalForm());

        addKeyListenerToSuggestionPanel(editor, list);
        super.setScene(scene);
    }

    private void addKeyListenerToSuggestionPanel(Editor editor, WideSuggestionListView list) {
        this.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                // Popup is selected -> pass KeyTyped events down to editor
                for (KeyListener listener : editor.getContentComponent().getKeyListeners()) {
                    listener.keyTyped(e);
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {
                // Popup is selected -> pass KeyPressed events down to editor
                for (KeyListener listener : editor.getContentComponent().getKeyListeners()) {
                    listener.keyPressed(e);
                }

                // Bugfix: Backspace does not get redirected. So delete last letter anyways
                if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                    WideDataContext context = new WideDataContext();
                    context.setData("text", "");

                    context.setData("startPosition", editor.getCaretModel().getOffset() - 1);
                    context.setData("endPosition", editor.getCaretModel().getOffset());

                    ApplicationManager.getApplication().runWriteAction(new Runnable() {
                        @Override
                        public void run() {
                            WideReplaceAction action = new WideReplaceAction();
                            action.actionPerformed(editor, context);
                        }
                    });
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    // Move selector down
                    if (list.getSelectionModel().getSelectedIndex() == list.getItems().size() - 1) {
                        list.getSelectionModel().selectFirst();
                        list.scrollTo(0);
                    } else {
                        list.getSelectionModel().select(list.getSelectionModel().getSelectedIndex() + 1);
                        list.scrollTo(list.getSelectionModel().getSelectedIndex());
                    }

                } else if (e.getKeyCode() == KeyEvent.VK_UP) {
                    // Move selector up
                    if (list.getSelectionModel().getSelectedIndex() == 0) {
                        list.getSelectionModel().selectLast();
                        list.scrollTo(list.getItems().size() - 1);
                    } else {
                        list.getSelectionModel().select(list.getSelectionModel().getSelectedIndex() - 1);
                        list.scrollTo(list.getSelectionModel().getSelectedIndex());
                    }

                } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    // Insert selected suggestion
                    list.insertSuggestionAtIndex(list.getSelectionModel().getSelectedIndex(), editor);

                } else {
                    // KeyEvent that we do not need to care about -> pass to editor
                    for (KeyListener listener : editor.getContentComponent().getKeyListeners()) {
                        listener.keyReleased(e);
                    }
                }
            }
        });
    }
}
