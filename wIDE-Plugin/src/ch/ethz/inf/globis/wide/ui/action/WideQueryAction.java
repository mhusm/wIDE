package ch.ethz.inf.globis.wide.ui.action;

import ch.ethz.inf.globis.wide.ui.listener.WideInputListener;
import ch.ethz.inf.globis.wide.logging.WideLogger;
import ch.ethz.inf.globis.wide.lookup.WideDocumentationHandler;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.application.ApplicationListener;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.editor.actionSystem.EditorAction;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
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

    private Editor currentEditor;

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

    @Override
    public void updateForKeyboardAccess(Editor editor, Presentation presentation, DataContext dataContext) {
        this.update(editor, presentation, dataContext);
    }

    @Override
    public void update(Editor editor, Presentation presentation, DataContext dataContext) {
        presentation.setEnabled(this.getHandler().isEnabled(editor, (Caret)null, dataContext));
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

                        Project project = editor.getProject();
                        ToolWindow window = ToolWindowManager.getInstance(project).getToolWindow("wIDE");

                        new WideDocumentationHandler().differentiateLanguages(editor, psiFile, startElement, endElement);
                    } else {
                        LOGGER.info("Invalid place to start a query.");
                    }

                    //TODO: maybe move to another location
                    //FIXME: adds multiple listeners!
                    editor.getDocument().addDocumentListener(new WideInputListener(editor));
                    editor.getSettings().setShowIntentionBulb(false);
                }

            });
        }


    }
}
