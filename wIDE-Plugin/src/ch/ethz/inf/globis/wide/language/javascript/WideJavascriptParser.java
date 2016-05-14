package ch.ethz.inf.globis.wide.language.javascript;

import ch.ethz.inf.globis.wide.communication.WideHttpCommunicator;
import ch.ethz.inf.globis.wide.lookup.io.WideQueryRequest;
import ch.ethz.inf.globis.wide.parsing.IWideLanguageParser;
import ch.ethz.inf.globis.wide.lookup.io.WideQueryResponse;
import com.intellij.lang.javascript.psi.JSCallExpression;
import com.intellij.lang.javascript.psi.JSDefinitionExpression;
import com.intellij.lang.javascript.psi.impl.JSCallExpressionImpl;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fabian on 17.03.16.
 */
public class WideJavascriptParser implements IWideLanguageParser {
//    public List<WideQueryResponse> handle(Editor editor, PsiFile file, PsiElement startElement, PsiElement endElement, boolean isFinished) {
//        //TODO: distinguish file
//
//        //TODO: multiple calls?
//
//        List<WideQueryResponse> results = new ArrayList<WideQueryResponse>();
//        WideQueryRequest request = buildRequest(editor, file, startElement, endElement);
//
//        if (request != null) {
//            WideQueryResponse response = WideHttpCommunicator.sendRequest(request);
//            results.add(response);
//        } else {
//            //results.add(new WideQueryResponse("No JS function:"));
//        }
//
//        return results;
//    }

    public WideQueryRequest buildDocumentationQuery(Editor editor, PsiFile file, PsiElement startElement, PsiElement endElement) {
        //TODO: distinguish file

        //TODO: multiple calls?

        JSCallExpression currentCall = findLowestCommonCallExpression(startElement, endElement);

        if (currentCall != null) {

            WideJSCall function = new WideJSCall(currentCall);

            //TODO: GET REFERENCED CALLEE, IF CALLEXPRESSION OR REFERENCEEXPRESSION

            List<PsiElement> matchingCalls = function.getMatchingFunctions(editor);

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
            currentCall = findLowestCallExpression(leftElement);
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
