package ch.ethz.inf.globis.wide.ui.action;

import ch.ethz.inf.globis.wide.ui.listener.WideInputListener;
import ch.ethz.inf.globis.wide.logging.WideLogger;
import ch.ethz.inf.globis.wide.lookup.WideDocumentationHandler;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
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
import javafx.scene.input.KeyCode;
import org.jetbrains.annotations.NotNull;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;


/**
 * Created by Fabian Stutz on 09.03.16.
 */
public class WideQueryAction extends EditorAction {

    private final static WideLogger LOGGER = new WideLogger(WideQueryAction.class.getName());

    protected WideQueryAction() {
        super(new WideQueryHandler());
    }


    @Override
    public void update(AnActionEvent e) {
        // WHEN SHOULD THE MENU BE VISIBLE?
        //Get required data keys
        final Project project = e.getData(CommonDataKeys.PROJECT);
        final Editor editor = e.getData(CommonDataKeys.EDITOR);

        //Set visibility only in case of existing project and editor and there is selected text
        e.getPresentation().setVisible((project != null && editor != null && editor.getSelectionModel().hasSelection()));
    }


    public static class WideQueryHandler extends EditorActionHandler {
        @Override
        public void doExecute(final Editor editor, final Caret caret, final DataContext dataContext) {
            ApplicationManager.getApplication().runWriteAction(new Runnable() {
                @Override
                public void run() {
                    SelectionModel selectionModel = editor.getSelectionModel();

                    if (editor.getProject() != null) {
                        @NotNull
                        PsiFile psiFile = PsiDocumentManager.getInstance(editor.getProject()).getPsiFile(editor.getDocument());
                        int start = selectionModel.getSelectionStart();
                        int end = selectionModel.getSelectionEnd() - 1;
                        PsiElement startElement = psiFile.findElementAt(start);
                        PsiElement endElement = psiFile.findElementAt(end);

                        new WideDocumentationHandler().differentiateLanguages(editor, psiFile, startElement, endElement);
                    } else {
                        LOGGER.info("Invalid place to start a query.");
                    }

                    //TODO: maybe move to another location
                    editor.getDocument().addDocumentListener(new WideInputListener(editor));
                    editor.getSettings().setShowIntentionBulb(false);
                }

            });
        }


    }
}
