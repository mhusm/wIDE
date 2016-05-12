package ch.ethz.inf.globis.wide.ui.components.list.cell;

import ch.ethz.inf.globis.wide.lookup.io.WideQueryResponse;
import ch.ethz.inf.globis.wide.ui.action.WideReplaceAction;
import ch.ethz.inf.globis.wide.ui.components.WideDataContext;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ListCell;
import javafx.scene.layout.FlowPane;
import javafx.scene.text.Text;

/**
 * Created by fabian on 12.05.16.
 */
public abstract class WideSuggestionCell extends ListCell<WideQueryResponse> {

    private JFXPanel panel;

    public WideSuggestionCell(JFXPanel panel) {
        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        this.panel = panel;
    }

    @Override
    protected void updateItem(WideQueryResponse suggestion, boolean empty) {
        super.updateItem(suggestion, empty);
        if (suggestion != null) {
            setGraphic(createTextFlow(suggestion));
        } else {
            setGraphic(null);
        }
    }

    public abstract Node createTextFlow(WideQueryResponse suggestion);

    public abstract void insertSuggestion(WideQueryResponse suggestion, Editor editor);

    protected void insertSuggestionText(String text, int start, int end, Editor editor) {

        WideDataContext context = new WideDataContext();
        context.setData("startPosition", start);
        context.setData("endPosition", end);
        context.setData("text", text);


        ApplicationManager.getApplication().runWriteAction(new Runnable() {
            @Override
            public void run() {
                WideReplaceAction action = new WideReplaceAction();
                action.actionPerformed(editor, context);
            }
        });
    }
}

