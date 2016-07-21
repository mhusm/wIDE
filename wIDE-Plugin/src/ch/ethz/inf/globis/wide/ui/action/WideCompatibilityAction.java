package ch.ethz.inf.globis.wide.ui.action;

import ch.ethz.inf.globis.wide.compatibility.WideCompatibilityTraverser;
import ch.ethz.inf.globis.wide.language.IWideLanguageHandler;
import ch.ethz.inf.globis.wide.logging.WideLogger;
import ch.ethz.inf.globis.wide.registry.WideLanguageRegistry;
import ch.ethz.inf.globis.wide.ui.components.window.WideDefaultWindowFactory;
import com.intellij.ide.IdeEventQueue;
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
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import javafx.application.Platform;

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
            LOGGER.config("COMPATIBILITY SCAN INVOKED.");
            ApplicationManager.getApplication().runWriteAction(new Runnable() {
                @Override
                public void run() {
                    // The project should be set -> otherwise not properly initialized
                    if (editor.getProject() != null) {

                        // Show waiting window
                        Project project = editor.getProject();
                        ToolWindow window = ToolWindowManager.getInstance(project).getToolWindow("wIDE");
                        WideDefaultWindowFactory windowFactory = new WideDefaultWindowFactory();
                        windowFactory.showWaitingWindow(window);

                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                WideCompatibilityTraverser traverser = new WideCompatibilityTraverser();
                                traverser.traverseFile(editor);
                            }
                        });

                        thread.start();
                    } else {
                        // Project of editor is not set -> not possible to do lookup
                        LOGGER.info("No file selected for compatibility scan.");
                    }
                }

                private void handleError(Editor editor, String error) {
                    LOGGER.warning(error);
                }

            });
        }

    }
}
