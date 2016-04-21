package ch.ethz.inf.globis.wide.listener;


import ch.ethz.inf.globis.wide.action.WideQueryAction;
import ch.ethz.inf.globis.wide.lookup.WideSuggestionHandler;
import ch.ethz.inf.globis.wide.ui.popup.WidePopupHelper;
import com.intellij.lang.javascript.psi.JSCallExpression;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;

/**
 * Created by fabian on 20.04.16.
 */
public class WideInputListener implements DocumentListener {

    private Editor editor;

    public WideInputListener(Editor editor) {
        this.editor = editor;
    }

    @Override
    public void beforeDocumentChange(com.intellij.openapi.editor.event.DocumentEvent documentEvent) {
    }

    @Override
    public void documentChanged(com.intellij.openapi.editor.event.DocumentEvent documentEvent) {
        WidePopupHelper.getInstance().hidePopup();
        ApplicationManager.getApplication().acquireReadActionLock();
        PsiFile psiFile = PsiDocumentManager.getInstance(editor.getProject()).getPsiFile(editor.getDocument());
        PsiElement startElement = psiFile.findElementAt(documentEvent.getOffset() - 1);

        if (startElement.getParent().getParent() instanceof JSCallExpression) {
            WideQueryAction.WideQueryHandler queryHandler = new WideQueryAction.WideQueryHandler();
            new WideSuggestionHandler().differentiateLanguages(editor, psiFile, startElement, startElement);
        }
    }

}
