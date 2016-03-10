package ch.ethz.inf.globis.wide;

import com.intellij.codeInspection.LocalInspectionToolSession;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.ide.PsiActionSupportFactory;
import com.intellij.lang.FileASTNode;
import com.intellij.lang.javascript.buildTools.JSPsiUtil;
import com.intellij.lang.javascript.inspections.JSInspection;
import com.intellij.lang.javascript.psi.*;
import com.intellij.lang.javascript.psi.impl.JSExpressionCodeFragmentImpl;
import com.intellij.lang.javascript.psi.impl.JSPsiImplUtils;
import com.intellij.lang.javascript.psi.util.JSPsiTreeUtil;
import com.intellij.lang.javascript.refactoring.HighlightingUtils;
import com.intellij.lang.javascript.refactoring.ui.JSReferenceEditor;
import com.intellij.lang.javascript.search.JSFunctionsSearch;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.editor.actionSystem.EditorAction;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.editor.actionSystem.EditorActionManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.*;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.util.Query;
import com.sixrr.inspectjs.JavaScriptInspection;
import org.jetbrains.annotations.NotNull;

import java.lang.ref.Reference;
import java.util.Collection;
import java.util.List;

/**
 * Created by fabian on 09.03.16.
 */
public class QueryAction extends EditorAction {

    protected QueryAction() {
        super(new QueryHandler());
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


    private static class QueryHandler extends EditorActionHandler {
        @Override
        public void doExecute(final Editor editor, final Caret caret, final DataContext dataContext) {
            ApplicationManager.getApplication().runWriteAction(new Runnable() {
                @Override
                public void run() {
                    SelectionModel selectionModel = editor.getSelectionModel();
                    //editor.getDocument().replaceString(selectionModel.getSelectionStart(), selectionModel.getSelectionEnd(), "fuuuu");

                    @NotNull
                    PsiFile psiFile = PsiDocumentManager.getInstance(editor.getProject()).getPsiFile(editor.getDocument());
                    int start = selectionModel.getSelectionStart();
                    int end = selectionModel.getSelectionEnd() - 1;
                    int offset = editor.getCaretModel().getOffset();
                    PsiElement startElement = psiFile.findElementAt(start);
                    PsiElement endElement = psiFile.findElementAt(end);

                    PsiElement startParentElement = startElement.getParent();
                    PsiElement endParentElement = endElement.getParent();

                    System.out.println("start: " + startElement.getText());
                    System.out.println("end. " + endElement.getText());
                    System.out.println("start parent: " + startParentElement.getText());
                    System.out.println("end parent " + endParentElement.getText());
                    System.out.println();
                }

            });
        }
    }
}
