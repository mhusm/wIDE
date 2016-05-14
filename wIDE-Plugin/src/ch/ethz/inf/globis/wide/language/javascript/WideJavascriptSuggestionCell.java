package ch.ethz.inf.globis.wide.language.javascript;

import ch.ethz.inf.globis.wide.lookup.io.WideQueryResponse;
import ch.ethz.inf.globis.wide.ui.components.list.WideSuggestionCell;
import com.intellij.openapi.editor.Editor;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Node;

/**
 * Created by fabian on 12.05.16.
 */
public class WideJavascriptSuggestionCell extends WideSuggestionCell {

    public WideJavascriptSuggestionCell(JFXPanel panel) {
        super(panel);
    }

    @Override
    public Node createTextFlow(WideQueryResponse suggestion) {
        return null;
    }

    @Override
    public void insertSuggestion(WideQueryResponse suggestion, Editor editor) {

    }
}
