package ch.ethz.inf.globis.wide.ui.listener;


import ch.ethz.inf.globis.wide.ui.action.WideSuggestAction;
import ch.ethz.inf.globis.wide.ui.components.WideDataContext;
import ch.ethz.inf.globis.wide.ui.components.popup.WidePopupHelper;
import com.intellij.ide.IdeEventQueue;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.event.DocumentListener;

/**
 * Created by fabian on 20.04.16.
 */
public class WideInputListener implements DocumentListener {

    private Editor editor;

    public WideInputListener(Editor editor) {
        this.editor = editor;
    }

    @Override
    public final void beforeDocumentChange(com.intellij.openapi.editor.event.DocumentEvent documentEvent) {
    }

    @Override
    public final void documentChanged(com.intellij.openapi.editor.event.DocumentEvent documentEvent) {
        WideDataContext context = new WideDataContext();
        context.setData("position", documentEvent.getOffset() - 1);
        context.setData("text", documentEvent.getNewFragment().toString());

        IdeEventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                WideSuggestAction action = new WideSuggestAction();
                action.actionPerformed(editor, context);
            }
        });
    }

}
