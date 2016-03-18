package ch.ethz.inf.globis.wide.parsing.css;

import ch.ethz.inf.globis.wide.parsing.WideJSParser;
import com.intellij.lang.javascript.psi.JSCallExpression;
import com.intellij.lang.javascript.psi.impl.JSCallExpressionImpl;
import com.intellij.psi.PsiElement;

/**
 * Created by fabian on 11.03.16.
 */
public class WideCSSParser {

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
        if (leftElement.isEquivalentTo(leftParent) && rightElement.isEquivalentTo(rightParent)) {
            currentCall = WideJSParser.findLowestCallExpression(leftElement);
        }

        return currentCall;
    }

    /*
    Finds the nearest Call Expression above the selected Element.
     */
    public static JSCallExpression findLowestCallExpression(PsiElement element) {
        JSCallExpression currentCall = null;

        while (currentCall == null && !(element == null)) {
            element = element.getParent();
            if (element instanceof JSCallExpression) {
                JSCallExpression expr = (JSCallExpressionImpl) element;
                currentCall = expr;
            }
        }

        return currentCall;
    }
}
