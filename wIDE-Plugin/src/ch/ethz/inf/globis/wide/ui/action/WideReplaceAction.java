package ch.ethz.inf.globis.wide.ui.action;

import ch.ethz.inf.globis.wide.logging.WideLogger;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.editor.actionSystem.EditorAction;
import com.intellij.openapi.editor.actionSystem.EditorWriteActionHandler;
import org.jetbrains.annotations.Nullable;

/**
 * Created by fabian on 09.05.16.
 */
public class WideReplaceAction extends EditorAction {
    private final static WideLogger LOGGER = new WideLogger(WideReplaceAction.class.getName());

    public WideReplaceAction() {
        super(new WideReplaceActionHandler());
    }

    public static class WideReplaceActionHandler extends EditorWriteActionHandler {

        @Override
        public void doExecute(final Editor editor, @Nullable final Caret caret, final DataContext dataContext) {
                if (dataContext.getData("text") != null
                        && dataContext.getData("text") instanceof String
                        && dataContext.getData("startPosition") != null
                        && dataContext.getData("startPosition") instanceof Integer
                        && dataContext.getData("endPosition") != null
                        && dataContext.getData("endPosition") instanceof Integer) {

                    editor.getDocument().replaceString((int) dataContext.getData("startPosition"), (int) dataContext.getData("endPosition"), (String) dataContext.getData("text"));
                    editor.getCaretModel().moveToOffset((int) dataContext.getData("startPosition") + ((String) dataContext.getData("text")).length());

                } else {
                    LOGGER.warning("Tried to invoke WideReplaceAction with invalid arguments.");
                }
        }
    }
}
