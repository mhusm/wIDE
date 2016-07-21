package ch.ethz.inf.globis.wide.ui.action;

import ch.ethz.inf.globis.wide.language.IWideLanguageHandler;
import ch.ethz.inf.globis.wide.logging.WideLogger;
import ch.ethz.inf.globis.wide.registry.WideLanguageRegistry;
import ch.ethz.inf.globis.wide.ui.components.window.WideDefaultWindowFactory;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.Presentation;
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
import org.jetbrains.annotations.NotNull;


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

    public static class WideQueryHandler extends EditorActionHandler {
        @Override
        public void doExecute(final Editor editor, final Caret caret, final DataContext dataContext) {

            LOGGER.config("DOCUMENTATION LOOKUP INVOKED.");

            // Show waiting window
            Project project = editor.getProject();
            ToolWindow window = ToolWindowManager.getInstance(project).getToolWindow("wIDE");
            WideDefaultWindowFactory windowFactory = new WideDefaultWindowFactory();
            windowFactory.showWaitingWindow(window);

            ApplicationManager.getApplication().runWriteAction(new Runnable() {
                @Override
                public void run() {
                    SelectionModel selectionModel = editor.getSelectionModel();

                    // The project should be set -> otherwise not properly initialized
                    if (editor.getProject() != null) {

                        // find psiElements of selected text
                        PsiFile psiFile = PsiDocumentManager.getInstance(editor.getProject()).getPsiFile(editor.getDocument());
                        int start = selectionModel.getSelectionStart();
                        int end = selectionModel.getSelectionEnd() - 1;
                        PsiElement startElement = psiFile.findElementAt(start);
                        PsiElement endElement = psiFile.findElementAt(end);

                        // Is the start and the end of the selection in the same language?
                        if (startElement.getParent().getClass().equals(endElement.getParent().getClass())) {

                            // Get language handler of this language
                            IWideLanguageHandler languageHandler = WideLanguageRegistry.getInstance().getLanguageHandler(startElement.getParent().getClass());

                            if (languageHandler == null) {
                                // Not supported language: Show message.
                                handleError(editor, "This language is not supported.");
                            } else {
                                // language is supported -> do lookup
                                languageHandler.lookupDocumentation(editor, psiFile, startElement, endElement);
                            }

                        } else {
                            // Mix of various Languages: Show message.
                            handleError(editor, "Please do not mix different languages.");
                        }

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
