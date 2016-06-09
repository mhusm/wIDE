package ch.ethz.inf.globis.wide.ui.action;

import ch.ethz.inf.globis.wide.compatibility.WideCompatibilityTraverser;
import ch.ethz.inf.globis.wide.language.IWideLanguageHandler;
import ch.ethz.inf.globis.wide.logging.WideLogger;
import ch.ethz.inf.globis.wide.registry.WideLanguageRegistry;
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

/**
 * Created by fabian on 26.05.16.
 */
public class WideCompatibilityAction extends EditorAction {
    private final static WideLogger LOGGER = new WideLogger(WideCompatibilityAction.class.getName());

    private Editor currentEditor;

    protected WideCompatibilityAction() {
        super(new WideCompatibilityHandler());
    }


    @Override
    public void update(AnActionEvent e) {
        // WHEN SHOULD THE MENU BE VISIBLE?
        //Get required data keys
        final Project project = e.getData(CommonDataKeys.PROJECT);
        final Editor editor = e.getData(CommonDataKeys.EDITOR);

        //Set visibility only in case of existing project and editor and there is selected text
        e.getPresentation().setVisible((project != null && editor != null));

    }

    public static class WideCompatibilityHandler extends EditorActionHandler {
        @Override
        public void doExecute(final Editor editor, final Caret caret, final DataContext dataContext) {
            ApplicationManager.getApplication().runWriteAction(new Runnable() {
                @Override
                public void run() {

                    //TODO: IMPLEMENTATION


                    SelectionModel selectionModel = editor.getSelectionModel();

                    // The project should be set -> otherwise not properly initialized
                    if (editor.getProject() != null) {
                        PsiFile psiFile = PsiDocumentManager.getInstance(editor.getProject()).getPsiFile(editor.getDocument());
                        WideCompatibilityTraverser traverser = new WideCompatibilityTraverser();
                        traverser.traverseFile(editor, psiFile);

                    } else {
                        // Project of editor is not set -> not possible to do lookup
                        LOGGER.info("Invalid place to start a query.");
                    }
                }

                private void handleError(Editor editor, String error) {
                    LOGGER.warning(error);
                }

            });
        }

    }
}
