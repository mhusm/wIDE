package ch.ethz.inf.globis.wide.parsing;

import com.intellij.lang.javascript.JavascriptLanguage;
import com.intellij.lang.javascript.psi.JSCallExpression;
import com.intellij.lang.javascript.psi.JSFunctionExpression;
import com.intellij.lang.javascript.psi.JSParameterList;
import com.intellij.lang.javascript.psi.resolve.JSResolveUtil;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.PsiManagerImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fabian on 11.03.16.
 */
public class WideFunction {

    final private PsiElement methodName;
    final private PsiElement methodReceiver;
    final private JSCallExpression callExpression;

    public WideFunction(JSCallExpression callExpression) {
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
            if (JSResolveUtil.findFileLocalElement(this.getMethodNameText(), file) != null) {
                System.out.println("Potential function found in file: " + file.getName());
                matchingCalls.add(JSResolveUtil.findFileLocalElement(this.getMethodNameText(), file));
            }

            // SEARCH FOR HIDDEN FUNCTIONS
            // Load document of file and search for occurrence(s)
            Document doc = PsiDocumentManager.getInstance(editor.getProject()).getDocument(file);
            int occurrence = 0;
            while (occurrence >= 0) {
                occurrence = doc.getText().indexOf(this.getMethodNameText(), occurrence + 4);
                PsiElement el = file.findElementAt(occurrence);

                // Does the occurrence match the function signature?
                if (el.getParent().getChildren() != null
                        && el.getParent().getChildren().length > 0
                        && el.getParent().getChildren()[0] instanceof JSFunctionExpression
                        && this.getMethodNameText().equals(el.getText())) {

                    System.out.println("Potential function found in file: " + el.getContainingFile().getName());
                    matchingCalls.add(el.getParent());

                    // Does the function have the same amount of parameters?
                    if (((JSParameterList) el.getParent().getChildren()[0].getFirstChild().getNextSibling()).getParameters().length == callExpression.getArguments().length) {
                        System.out.println("Matching function found in file: " + el.getContainingFile().getName());
                    }
                }
            }
        }

        return matchingCalls;
    }
}
