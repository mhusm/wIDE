package ch.ethz.inf.globis.wide.language.javascript;

import ch.ethz.inf.globis.wide.io.query.WideQueryRequest;
import ch.ethz.inf.globis.wide.language.IWideLanguageParser;
import com.intellij.lang.javascript.psi.JSCallExpression;
import com.intellij.lang.javascript.psi.JSDefinitionExpression;
import com.intellij.lang.javascript.psi.JSReferenceExpression;
import com.intellij.lang.javascript.psi.impl.JSCallExpressionImpl;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;

import java.util.List;

/**
 * Created by fabian on 17.03.16.
 */
public class WideJavascriptParser implements IWideLanguageParser {

    public WideQueryRequest buildDocumentationQuery(PsiFile file, PsiElement startElement, PsiElement endElement) {
        //TODO: distinguish file

        //TODO: multiple calls?

        JSCallExpression currentCall = findLowestCommonCallExpression(startElement, endElement);

        if (currentCall != null) {

            WideJSCall function = new WideJSCall(currentCall);

            //TODO: GET REFERENCED CALLEE, IF CALLEXPRESSION OR REFERENCEEXPRESSION

            List<PsiElement> matchingCalls = function.getMatchingFunctions(file.getProject());

            WideQueryRequest request = new WideQueryRequest();
            request.setLang("JS");
            request.setType("call");
            request.setKey(function.getMethodNameText());

            for (PsiElement call : matchingCalls) {
                PsiElement referencedObject = call;
                if (call.getParent() instanceof JSDefinitionExpression) {
                    // References a defined function on an object.
                    // Include the name of the referenced object as well.
                    while (referencedObject.getFirstChild() != null) {
                        referencedObject = referencedObject.getFirstChild();
                    }
                }

                WideQueryRequest childRequest = new WideQueryRequest();
                childRequest.setLang("JS");
                childRequest.setType("callCandidate");
                childRequest.setKey(function.getMethodNameText());
                childRequest.setValue("{" +
                        "     \"receiver\": \"" + referencedObject.getText() + "\", " +
                        "     \"file\": \"" + call.getContainingFile().getName() + "\"}");

                request.addChild(childRequest);
            }

            return request;
        }

        return null;
    }

    @Override
    public PsiElement getRelevantElement(PsiElement element) {
        PsiElement callExpression = findLowestCallExpression(element);
        if (findLowestCallExpression(element) != null) {
            return ((JSCallExpression) callExpression).getMethodExpression().getLastChild();
        }

        return null;
    }

    /*
    Find the lowest Common Call Expression of two PsiElements
    Returns null, if the two PsiElements are not in the same PsiTree
   */
    public static JSCallExpression findLowestCommonCallExpression(PsiElement leftElement, PsiElement rightElement) {
        //traverse tree from start to end
        PsiElement leftParent = leftElement;
        PsiElement rightParent = rightElement;

        JSCallExpression currentCall = null;

        // traverse on left side to parent (as long as the offset stays the same)
        while (leftParent.getStartOffsetInParent() == 0) {
            leftParent = leftParent.getParent();
            //System.out.println("current left parent type: " +leftParent.getClass());

            if (leftParent instanceof JSCallExpression) {
                JSCallExpression expr = (JSCallExpressionImpl) leftParent;
                //System.out.println("Call: [expression] " + expr.getMethodExpression().getText() + " [argumentList] " + expr.getArgumentList().getText());
                currentCall = expr;
            } else if (leftParent instanceof JSReferenceExpression) {
                System.out.println("REFERENCE EXPRESSION");
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
                JSCallExpression expr = (JSCallExpression) leftParent;
                //System.out.println("CALL: [expression] " + expr.getMethodExpression().getText() + " [argumentList] " + expr.getArgumentList().getText());
                currentCall = expr;
            }

            if (rightParent instanceof JSCallExpression) {
                JSCallExpression expr = (JSCallExpression) rightParent;
                //System.out.println("CALL: [expression] " + expr.getMethodExpression().getText() + " [argumentList] " + expr.getArgumentList().getText());
                currentCall = expr;
            }
        }

        // only one element clicked. Find out if it there is a call involved
        if (leftElement.isEquivalentTo(leftParent) && rightElement.isEquivalentTo(rightParent)) {
            currentCall = findLowestCallExpression(leftElement);
        }

        if (!(currentCall == null)
                && !(currentCall.getMethodExpression().getFirstChild().equals(currentCall.getMethodExpression().getLastChild()))
                && !currentCall.getMethodExpression().getLastChild().getText().contains("(")
                && !currentCall.getMethodExpression().getLastChild().getText().contains(")")
                && !currentCall.getMethodExpression().getLastChild().getText().contains(".")
                && !currentCall.getMethodExpression().getLastChild().getText().contains("\"")
                && !currentCall.getMethodExpression().getLastChild().getText().contains("\'")) {
            return currentCall;
        } else {
            return null;
        }
    }

    /*
    Finds the nearest Call Expression above the selected Element.
     */
    public static JSCallExpression findLowestCallExpression(PsiElement element) {
        JSCallExpression currentCall = null;

        while (currentCall == null && !(element == null)) {
            element = element.getParent();
            if (element instanceof JSCallExpressionImpl) {
                JSCallExpression expr = (JSCallExpressionImpl) element;
                currentCall = expr;
            }
        }

        return currentCall;
    }

}
