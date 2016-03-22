package ch.ethz.inf.globis.wide.parsing.javascript;

import ch.ethz.inf.globis.wide.communication.WideHttpCommunicator;
import ch.ethz.inf.globis.wide.parsing.AbstractLanguageHandler;
import ch.ethz.inf.globis.wide.parsing.WideQueryResult;
import com.intellij.lang.javascript.psi.JSCallExpression;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.PsiNonJavaFileReferenceProcessor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fabian on 17.03.16.
 */
public class WideJavascriptHandler implements AbstractLanguageHandler {
    public  List<WideQueryResult> handle(Editor editor, PsiFile file, PsiElement startElement, PsiElement endElement) {
        //TODO: distinguish file

        //TODO: multiple calls?

        List<WideQueryResult> results = new ArrayList<WideQueryResult>();
        String request = buildRequest(editor, file, startElement, endElement);

        if (request != "") {
            String response = WideHttpCommunicator.sendRequest(request);
            System.out.println(response);

            WideQueryResult result = new WideQueryResult(response);
            results.add(result);
        } else {
            results.add(new WideQueryResult("No JS function:"));
        }

        return results;
    }

//    private List<WideQueryResult> queryFunctions(List<PsiElement> matchingCalls) {
//        List<WideQueryResult> results = new ArrayList<WideQueryResult>();
//        for (PsiElement call : matchingCalls) {
//            String request = "function_name=" + call.getFirstChild().getText() + "&file_name=" + call.getContainingFile().getName();
//
//            String response = WideHttpCommunicator.sendJSRequest(request);
//            System.out.println(response);
//
//            WideQueryResult result = new WideQueryResult(response);
//            result.setLookupName(call.getFirstChild().getText());
//            result.setFileName(call.getContainingFile().getName());
//            result.setLookupType("JS-Call");
//            results.add(result);
//        }
//        return results;
//    }

    public String buildRequest(Editor editor, PsiFile file, PsiElement startElement, PsiElement endElement) {
        //TODO: distinguish file

        //TODO: multiple calls?

        JSCallExpression currentCall = WideJSParser.findLowestCommonCallExpression(startElement, endElement);

        String request = "";
        if (currentCall != null) {

            WideJSCall function = new WideJSCall(currentCall);

            //TODO: GET REFERENCED CALLEE, IF CALLEXPRESSION OR REFERENCEEXPRESSION

            List<PsiElement> matchingCalls = function.getMatchingFunctions(editor);

            request += "{" +
                    "    \"lang\": \"JS\", " +
                    "    \"type\": \"call\", " +
                    "    \"key\": \"" + function.getMethodNameText() + "\", " +
                    "    \"value\": \"" + function.getCallExpression().getContainingFile().getName() + "\", " +
                    "    \"children\": [";

            for (PsiElement call : matchingCalls) {
                request += "{" +
                        "    \"lang\": \"JS\", " +
                        "    \"type\": \"callCandidate\", " +
                        "    \"key\": \"" + call.getFirstChild().getText() + "\", " +
                        "    \"value\": \"" + call.getContainingFile().getName() + "\"}, ";
            }

            request += "null]}";

        }

        return request;
    }
}
