package ch.ethz.inf.globis.wide.ui.action;

import ch.ethz.inf.globis.wide.logging.WideLogger;
import ch.ethz.inf.globis.wide.lookup.WideDocumentationHandler;
import ch.ethz.inf.globis.wide.lookup.WideSuggestionHandler;
import ch.ethz.inf.globis.wide.ui.listener.WideInputListener;
import ch.ethz.inf.globis.wide.ui.popup.WidePopupHelper;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.application.AccessToken;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.editor.actionSystem.EditorAction;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import javafx.application.Platform;
import org.jetbrains.annotations.NotNull;

/**
 * Created by fabian on 10.05.16.
 */
public class WideSuggestAction extends EditorAction {

    private final static WideLogger LOGGER = new WideLogger(WideSuggestAction.class.getName());

    public WideSuggestAction() {
        super(new WideSuggestActionHandler());
    }

    public static class WideSuggestActionHandler extends EditorActionHandler {
        @Override
        public void doExecute(final Editor editor, final Caret caret, final DataContext dataContext) {
            PsiFile psiFile = PsiDocumentManager.getInstance(editor.getProject()).getPsiFile(editor.getDocument());
            PsiElement startElement = psiFile.findElementAt((int) dataContext.getData("position"));

            WideSuggestionHandler.differentiateElements(editor, startElement, (String) dataContext.getData("text"));
        }
    }
}
