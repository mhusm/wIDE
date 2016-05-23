package ch.ethz.inf.globis.wide.language.javascript;

import ch.ethz.inf.globis.wide.lookup.io.WideQueryResponse;
import ch.ethz.inf.globis.wide.ui.components.list.WideSuggestionCell;
import com.intellij.lang.javascript.psi.JSCallExpression;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlToken;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Node;
import javafx.scene.layout.FlowPane;
import javafx.scene.text.Text;

/**
 * Created by fabian on 12.05.16.
 */
public class WideJavascriptSuggestionCell extends WideSuggestionCell {

    public WideJavascriptSuggestionCell(JFXPanel panel) {
        super(panel);
    }

    @Override
    public Node createTextFlow(WideQueryResponse suggestion) {
        FlowPane title = new FlowPane();

        if (suggestion.getValue().equals("")) {
            // reference or call (without explicit receiver)

            Text sugg = new Text();
            sugg.setText(suggestion.getKey());
            sugg.getStyleClass().add("suggestion_function");
            title.getChildren().add(sugg);

        } else {
            // function lookup
            Text startTag = new Text();
            startTag.setText(suggestion.getKey() + ".");
            startTag.getStyleClass().add("suggestion_receiver");
            title.getChildren().add(startTag);

            Text sugg = new Text();
            sugg.setText(suggestion.getValue() + "(...)");
            sugg.getStyleClass().add("suggestion_function");
            title.getChildren().add(sugg);
        }

        return title;
    }

    @Override
    public void insertSuggestion(WideQueryResponse suggestion, Editor editor) {
        //TODO
        PsiFile psiFile = PsiDocumentManager.getInstance(editor.getProject()).getPsiFile(editor.getDocument());
        PsiElement element = psiFile.findElementAt(editor.getCaretModel().getOffset()-1);

        String text = "";
        int start;
        int end;

        if (suggestion.getValue().equals("")) {
            text += suggestion.getKey();
            if (element.getText().equals("<")) {
                // nothing typed yet
                start = editor.getCaretModel().getOffset();
                end = start;
            } else {
                // started writing tag
                start = element.getTextOffset();
                end = start + element.getTextLength();
            }

        } else {
            text += suggestion.getValue() + "();";

            if (element instanceof JSCallExpression) {
                // already typed some letters
                start = element.getParent().getTextOffset();
                end = start + element.getParent().getTextLength();
            } else {
                // nothing typed yet
                start = element.getTextOffset() + 1;
                end = start + element.getTextLength() - 1;

                text += element.getText().substring(1);
            }
        }

        this.insertSuggestionText(text, start, end, editor);
    }
}
