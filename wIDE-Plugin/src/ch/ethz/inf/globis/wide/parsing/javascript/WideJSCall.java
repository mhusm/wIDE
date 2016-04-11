package ch.ethz.inf.globis.wide.parsing.javascript;

import com.intellij.lang.javascript.JSElementType;
import com.intellij.lang.javascript.JavascriptLanguage;
import com.intellij.lang.javascript.psi.*;
import com.intellij.lang.javascript.psi.resolve.JSResolveUtil;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReferenceService;
import com.intellij.psi.impl.PsiManagerImpl;
import com.intellij.psi.search.PsiReferenceProcessor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fabian on 11.03.16.
 */
public class WideJSCall {

    final private PsiElement methodName;
    final private PsiElement methodReceiver;
    final private JSCallExpression callExpression;

    public WideJSCall(JSCallExpression callExpression) {
        this.callExpression = callExpression;
        this.methodName = callExpression.getMethodExpression().getLastChild();
        this.methodReceiver = callExpression.getMethodExpression().getFirstChild();

        System.out.println("Call: " + callExpression.getText());
        System.out.println("      Method Expression: " + callExpression.getMethodExpression().getText());
        System.out.println("      Method Name: " + this.methodName.getText() + " [" + this.methodName.getClass() + "]");
        System.out.println("      Method Receiver: " + this.methodReceiver.getText() + " [" + this.methodReceiver.getClass() + "]");
    }

    public PsiElement getMethodName() {
        return methodName;
    }

    public String getMethodNameText() {
        return methodName.getText();
    }

    public PsiElement getMethodReceiver() {
        return methodReceiver;
    }

    public String getMethodReceiverText() {
        return methodReceiver.getText();
    }

    public JSCallExpression getCallExpression() {
        return callExpression;
    }

    /**
     * @param editor The editor of the current project
     * @return A list of functions matching this call expression.
     */
    public List<PsiElement> getMatchingFunctions(Editor editor) {
        // SEARCH THROUGH PROJECT FILES
        // Load JavaScript Files of Project
        List<PsiFile> files = ((PsiManagerImpl) callExpression.getContainingFile().getManager()).getFileManager().getAllCachedFiles();
        List<PsiElement> matchingCalls = new ArrayList<PsiElement>();
        for (PsiFile file : files) {
            if (!file.getLanguage().equals(JavascriptLanguage.INSTANCE)) {
                continue;
            }

            // SEARCH FOR NORMAL FUNCTIONS
            PsiElement searchResult = JSResolveUtil.findFileLocalElement(this.getMethodNameText(), file);
            if (searchResult != null && searchResult instanceof JSFunction) {
                System.out.println("Potential function found in file: " + file.getName());
                matchingCalls.add(JSResolveUtil.findFileLocalElement(this.getMethodNameText(), file).getFirstChild().getNextSibling().getNextSibling());

            } else {

                // SEARCH FOR HIDDEN FUNCTIONS
                // DOM FUNCTIONS AND SIMILAR
                // Load document of file and search for occurrence(s)
                Document doc = PsiDocumentManager.getInstance(editor.getProject()).getDocument(file);
                int occurrence = 0;
                while (occurrence >= 0) {
                    occurrence = doc.getText().indexOf(this.getMethodNameText(), occurrence + 4);
                    PsiElement el = file.findElementAt(occurrence);

                    // Does the occurence really have the same name?
                    if (this.getMethodNameText().equals(el.getText())
                            && el.getParent().getChildren() != null
                            && el.getParent().getChildren().length > 0) {

                        // Does the result match the function signature?
                        if (el.getParent().getChildren()[0] instanceof JSFunctionExpression) {
                            System.out.println("Potential function found in file: " + el.getContainingFile().getName());
                            matchingCalls.add(el);

                        } else if (getMethodReceiver() instanceof JSReferenceExpression
                                && el.getParent().getParent() instanceof JSDefinitionExpression) {
                            // This is a definition of a variable
                            // Potentially a DOM Function
                            System.out.println("Potential definition found in file: " + el.getContainingFile().getName());
                            matchingCalls.add(el.getParent());
                        }
                    }
                }
            }
        }

        return matchingCalls;
    }
}
