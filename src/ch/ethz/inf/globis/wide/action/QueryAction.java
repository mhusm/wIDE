package ch.ethz.inf.globis.wide.action;

import ch.ethz.inf.globis.wide.parsing.WideFunction;
import ch.ethz.inf.globis.wide.parsing.WideFunctionParser;
import com.intellij.lang.StdLanguages;
import com.intellij.lang.javascript.JavascriptLanguage;
import com.intellij.lang.javascript.buildTools.JSPsiUtil;
import com.intellij.lang.javascript.flex.JSResolveHelper;
import com.intellij.lang.javascript.hierarchy.call.JSCallHierarchyBrowser;
import com.intellij.lang.javascript.hierarchy.call.JSCallHierarchyProvider;
import com.intellij.lang.javascript.inspections.JSReferencingMutableVariableFromClosureInspection;
import com.intellij.lang.javascript.psi.*;
import com.intellij.lang.javascript.psi.ecmal4.JSQualifiedNamedElement;
import com.intellij.lang.javascript.psi.impl.JSCallExpressionImpl;
import com.intellij.lang.javascript.psi.impl.JSPsiImplUtils;
import com.intellij.lang.javascript.psi.impl.JSReferenceExpressionImpl;
import com.intellij.lang.javascript.psi.resolve.*;
import com.intellij.lang.javascript.psi.util.JSPsiTreeUtil;
import com.intellij.lang.javascript.search.JSFunctionsSearch;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.editor.actionSystem.EditorAction;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.PopupChooserBuilder;
import com.intellij.psi.*;
import com.intellij.psi.impl.PsiManagerImpl;
import com.intellij.psi.impl.source.resolve.PsiResolveHelperImpl;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.PsiFileReferenceHelper;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiSearchHelper;
import com.intellij.psi.tree.TokenSet;
import com.intellij.util.xml.Documentation;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * Created by Fabian Stutz on 09.03.16.
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
                    PsiElement startElement = psiFile.findElementAt(start);
                    PsiElement endElement = psiFile.findElementAt(end);

                    JSCallExpression currentCall = WideFunctionParser.findLowestCommonCallExpression(startElement, endElement);

                    if (currentCall != null) {

                        WideFunction function = new WideFunction(currentCall);

                        //TODO: GET REFERENCED CALLEE, IF CALLEXPRESSION OR REFERENCEEXPRESSION

                        List<PsiElement> matchingCalls = function.getMatchingFunctions(editor);

                        showLookupResults(matchingCalls, editor);

                    } else {
                        // THE SELECTED TEXT IS NO CALL
                        System.out.println("The selection is no function call.");
                        List<String> popupText = new ArrayList<String>();
                        popupText.add("The selection is no function call.");
                        showLookupResults(popupText, editor);
                    }
                }

            });
        }
    }

    private static void showLookupResults(List<?> results, Editor editor) {
        DefaultListModel functionModel = new DefaultListModel();
        for(Object e : results) {
            if (e instanceof PsiElement) {
                functionModel.addElement(((PsiElement)e).getText());
            } else if (e instanceof String) {
                functionModel.addElement(e);
            }

        }
        JList popupList = new JList(functionModel);

        PopupChooserBuilder popupBuilder = JBPopupFactory.getInstance().createListPopupBuilder(popupList);
        JBPopup popup = popupBuilder.createPopup();
        popup.setSize(new Dimension(400, 100));
        popup.show(JBPopupFactory.getInstance().guessBestPopupLocation(editor));
    }
}
