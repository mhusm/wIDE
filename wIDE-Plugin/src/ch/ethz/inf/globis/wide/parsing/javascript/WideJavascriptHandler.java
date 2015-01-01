package ch.ethz.inf.globis.wide.parsing.javascript;

import ch.ethz.inf.globis.wide.communication.WideHttpCommunicator;
import ch.ethz.inf.globis.wide.parsing.AbstractLanguageHandler;
import ch.ethz.inf.globis.wide.parsing.WideQueryResult;
import com.intellij.lang.javascript.psi.JSCallExpression;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fabian on 17.03.16.
 */
public class WideJavascriptHandler implements AbstractLanguageHandler {
    public  List<WideQueryResult> handle(Editor editor, PsiFile file, PsiElement startElement, PsiElement endElement) {
        //TODO: distinguish file
        JSCallExpression currentCall = WideJSParser.findLowestCommonCallExpression(startElement, endElement);

        if (currentCall != null) {

            WideJSCall function = new WideJSCall(currentCall);

            //TODO: GET REFERENCED CALLEE, IF CALLEXPRESSION OR REFERENCEEXPRESSION

            List<PsiElement> matchingCalls = function.getMatchingFunctions(editor);

            return queryFunctions(matchingCalls);

        } else {
            List<WideQueryResult> results = new ArrayList<WideQueryResult>();
            results.add(new WideQueryResult("The selection is no function call."));
            return results;
        }
    }

    private List<WideQueryResult> queryFunctions(List<PsiElement> matchingCalls) {
        List<WideQueryResult> results = new ArrayList<WideQueryResult>();
        for (PsiElement call : matchingCalls) {
            String request = "function_name=" + call.getFirstChild().getText() + "&file_name=" + call.getContainingFile().getName();

            String response = WideHttpCommunicator.sendQuery(request);
            System.out.println(response);

            WideQueryResult result = new WideQueryResult(response);
            result.setLookupName(call.getFirstChild().getText());
            result.setFileName(call.getContainingFile().getName());
            results.add(result);
        }
        return results;
    }
}
