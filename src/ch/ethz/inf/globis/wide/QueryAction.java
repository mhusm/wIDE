package ch.ethz.inf.globis.wide;

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
import com.intellij.psi.*;
import com.intellij.psi.impl.PsiManagerImpl;
import com.intellij.psi.impl.source.resolve.PsiResolveHelperImpl;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.PsiFileReferenceHelper;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiSearchHelper;
import com.intellij.psi.tree.TokenSet;
import com.intellij.util.xml.Documentation;
import org.jetbrains.annotations.NotNull;

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

                    //traverse tree from start to end
                    PsiElement leftParent = startElement;
                    PsiElement rightParent = endElement;

                    JSCallExpression currentCall = null;

                    // traverse on left side to parent (as long as the offset stays the same)
                    while (leftParent.getStartOffsetInParent() == 0) {
                        leftParent = leftParent.getParent();
                        //System.out.println("current left parent type: " +leftParent.getClass());

                        if (leftParent instanceof JSCallExpression) {
                            JSCallExpression expr = (JSCallExpressionImpl) leftParent;
                            //System.out.println("Call: [expression] " + expr.getMethodExpression().getText() + " [argumentList] " + expr.getArgumentList().getText());
                            currentCall = expr;
                        }
                    }


                    // navigate the tree up -> find common ancestor
                    while (!rightParent.isEquivalentTo(leftParent)) {
                        if (rightParent.getTextOffset() <= leftParent.getTextOffset()) {
                            leftParent = leftParent.getParent();
                            //System.out.println("current left parent type: " + leftParent.getClass());
                        } else {
                            rightParent = rightParent.getParent();
                            //System.out.println("current right parent type: " + rightParent.getClass());
                        }

                        if (leftParent instanceof JSCallExpression) {
                            JSCallExpression expr = (JSCallExpressionImpl) leftParent;
                            //System.out.println("CALL: [expression] " + expr.getMethodExpression().getText() + " [argumentList] " + expr.getArgumentList().getText());
                            currentCall = expr;
                        }

                        if (rightParent instanceof JSCallExpression) {
                            JSCallExpression expr = (JSCallExpressionImpl) rightParent;
                            //System.out.println("CALL: [expression] " + expr.getMethodExpression().getText() + " [argumentList] " + expr.getArgumentList().getText());
                            currentCall = expr;
                        }
                    }

                    // only one element clicked. Find out if it there is a call involved
                    if (startElement.isEquivalentTo(leftParent) && endElement.isEquivalentTo(rightParent)) {
                        while (currentCall == null && !(leftParent == null)) {
                            leftParent = leftParent.getParent();
                            if (leftParent instanceof JSCallExpression) {
                                JSCallExpression expr = (JSCallExpressionImpl) leftParent;
                                //System.out.println("CALL: [expression] " + expr.getMethodExpression().getText() + " [argumentList] " + expr.getArgumentList().getText());
                                currentCall = expr;
                            }
                        }
                    }
                    if (currentCall != null) {
                        System.out.println("Call: " + currentCall.getText());
                        System.out.println("      Method Expression: " + currentCall.getMethodExpression().getText());
                        System.out.println("      Method Name: " + currentCall.getMethodExpression().getLastChild().getText());
                        System.out.println("      Method Receiver: " + currentCall.getMethodExpression().getFirstChild().getText() + " [" + currentCall.getMethodExpression().getFirstChild().getClass() + "]");

                        //TODO: GET REFERENCED CALLEE, IF CALLEXPRESSION OR REFERENCEEXPRESSION


                        //SEARCH IN OTHER FILES
                        // Load JavaScript Files of Project
                        List<PsiFile> files = ((PsiManagerImpl) currentCall.getContainingFile().getManager()).getFileManager().getAllCachedFiles();
                        List<PsiElement> matchingCalls = new ArrayList<PsiElement>();
                        for (PsiFile file : files) {
                            if (!file.getLanguage().equals(JavascriptLanguage.INSTANCE)) {
                                continue;
                            }

                            // SEARCH FOR NORMAL FUNCTIONS
                            if (JSResolveUtil.findFileLocalElement(currentCall.getMethodExpression().getLastChild().getText(), file) != null) {
                                System.out.println("Potential function found in file: " + file.getName());
                                matchingCalls.add(JSResolveUtil.findFileLocalElement(currentCall.getMethodExpression().getLastChild().getText(), file));
                            }

                            // SEARCH FOR HIDDEN FUNCTIONS
                            // Load document of file and search for occurrence(s)
                            Document doc = PsiDocumentManager.getInstance(editor.getProject()).getDocument(file);
                            int occurrence = 0;
                            while (occurrence >= 0) {
                                occurrence = doc.getText().indexOf(currentCall.getMethodExpression().getLastChild().getText(), occurrence + 4);
                                PsiElement el = file.findElementAt(occurrence);

                                // Does the occurrence match the function signature?
                                if (el.getParent().getChildren() != null
                                        && el.getParent().getChildren().length > 0
                                        && el.getParent().getChildren()[0] instanceof JSFunctionExpression
                                        && currentCall.getMethodExpression().getLastChild().getText().equals(el.getText())) {

                                    System.out.println("Potential function found in file: " + el.getContainingFile().getName());
                                    matchingCalls.add(el.getParent().getChildren()[0]);

                                    // Does the function have the same amount of parameters?
                                    if (((JSParameterList) el.getParent().getChildren()[0].getFirstChild().getNextSibling()).getParameters().length == currentCall.getArguments().length) {
                                        System.out.println("Matching function found in file: " + el.getContainingFile().getName());
                                    }
                                }
                            }
                        }
                    } else {
                        // THE SELECTED TEXT IS NO CALL
                        System.out.println("The selection is no function call.");
                    }
                }

            });
        }
    }
}
