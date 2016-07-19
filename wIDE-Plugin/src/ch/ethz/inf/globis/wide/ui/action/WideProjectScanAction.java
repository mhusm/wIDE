package ch.ethz.inf.globis.wide.ui.action;

import ch.ethz.inf.globis.wide.compatibility.WideCompatibilityTraverser;
import ch.ethz.inf.globis.wide.logging.WideLogger;
import ch.ethz.inf.globis.wide.ui.components.window.WideDefaultWindowFactory;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.pom.Navigatable;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.impl.PsiManagerImpl;
import com.intellij.psi.impl.file.PsiJavaDirectoryImpl;
import com.intellij.psi.impl.file.impl.FileManager;
import com.intellij.refactoring.typeCook.deductive.builder.SystemBuilder;

import java.util.List;

/**
 * Created by fabian on 22.06.16.
 */
public class WideProjectScanAction extends AnAction {
    private final static WideLogger LOGGER = new WideLogger(WideCompatibilityAction.class.getName());

    private Editor currentEditor;

    protected WideProjectScanAction() {
        super();
    }


    @Override
    public void update(AnActionEvent e) {
        // WHEN SHOULD THE MENU BE VISIBLE<?
        //Get required data keys
        final Project project = e.getData(CommonDataKeys.PROJECT);

        //Set visibility only in case of existing project and editor and there is selected text
        e.getPresentation().setVisible(project != null);

    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        Project project = anActionEvent.getProject();

        PsiDirectory directory = (PsiDirectory) anActionEvent.getData(CommonDataKeys.NAVIGATABLE);
        PsiManagerImpl manager = (PsiManagerImpl) directory.getManager();
        FileManager fileManager = manager.getFileManager();

        List<PsiFile> files = fileManager.getAllCachedFiles();

        WideCompatibilityTraverser traverser = new WideCompatibilityTraverser();
        traverser.traverseProject(project, files);

     }
}
