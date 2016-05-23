package ch.ethz.inf.globis.wide.language.html;

import ch.ethz.inf.globis.wide.lookup.io.WideQueryResponse;
import ch.ethz.inf.globis.wide.ui.components.list.WideSuggestionCell;
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
 * Created by fabian on 10.05.16.
 */
public class WideHtmlSuggestionCell extends WideSuggestionCell {

    public WideHtmlSuggestionCell(JFXPanel panel) {
        super(panel);
    }

    public Node createTextFlow(WideQueryResponse suggestion) {
        FlowPane title = new FlowPane();

        if (suggestion.getValue().equals("")) {
            // Tag lookup, only show tag (in brackets)
            Text startTag = new Text();
            startTag.setText("<");
            startTag.getStyleClass().add("suggestion_receiver");
            title.getChildren().add(startTag);

            Text sugg = new Text();
            sugg.setText(suggestion.getKey());
            sugg.getStyleClass().add("suggestion_function");
            title.getChildren().add(sugg);

            Text endTag = new Text();
            endTag.setText(">");
            endTag.getStyleClass().add("suggestion_receiver");
            title.getChildren().add(endTag);

        } else {
            // Tag lookup, only show tag (in brackets)
            Text startTag = new Text();
            startTag.setText("<" + suggestion.getValue() + " ");
            startTag.getStyleClass().add("suggestion_receiver");
            title.getChildren().add(startTag);

            Text sugg = new Text();
            sugg.setText(suggestion.getKey());
            sugg.getStyleClass().add("suggestion_function");
            title.getChildren().add(sugg);

            Text endTag = new Text();
            endTag.setText(">");
            endTag.getStyleClass().add("suggestion_receiver");
            title.getChildren().add(endTag);
        }

        return title;
    }

    @Override
    public void insertSuggestion(WideQueryResponse suggestion, Editor editor) {
        PsiFile psiFile = PsiDocumentManager.getInstance(editor.getProject()).getPsiFile(editor.getDocument());
        PsiElement element = psiFile.findElementAt(editor.getCaretModel().getOffset()-1);

        String text = suggestion.getKey();
        int start;
        int end;

        if (suggestion.getType().equals("tag")) {
            if (element.getText().equals("<")) {
                // nothing typed yet
                start = editor.getCaretModel().getOffset();
                end = start;
            } else {
                // started writing tag
                start = element.getTextOffset();
                end = start + element.getTextLength();
            }

            text += "></" + suggestion.getKey() + ">";

        } else {
            text += "=\"\"";

            if (element instanceof XmlToken) {
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
