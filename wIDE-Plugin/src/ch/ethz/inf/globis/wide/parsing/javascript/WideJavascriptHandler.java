package ch.ethz.inf.globis.wide.parsing.javascript;

import ch.ethz.inf.globis.wide.communication.WideHttpCommunicator;
import ch.ethz.inf.globis.wide.parsing.WideAbstractLanguageHandler;
import ch.ethz.inf.globis.wide.lookup.response.WideQueryResponse;
import com.intellij.lang.javascript.psi.JSCallExpression;
import com.intellij.lang.javascript.psi.JSDefinitionExpression;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fabian on 17.03.16.
 */
public class WideJavascriptHandler implements WideAbstractLanguageHandler {
    public List<WideQueryResponse> handle(Editor editor, PsiFile file, PsiElement startElement, PsiElement endElement, boolean isFinished) {
        //TODO: distinguish file

        //TODO: multiple calls?

        List<WideQueryResponse> results = new ArrayList<WideQueryResponse>();
        String request = buildRequest(editor, file, startElement, endElement);

        if (request != "") {
            String response = WideHttpCommunicator.sendRequest(request);

            WideQueryResponse result = new WideQueryResponse(response);
            results.add(result);
        } else {
            //results.add(new WideQueryResponse("No JS function:"));
        }

        return results;
    }

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
                    "    \"children\": [";

            for (PsiElement call : matchingCalls) {
                PsiElement referencedObject = call;
                if (call.getParent() instanceof JSDefinitionExpression) {
                    // References a defined function on an object.
                    // Include the name of the referenced object as well.
                    while (referencedObject.getFirstChild() != null) {
                        referencedObject = referencedObject.getFirstChild();
                    }
                }
                request += "{" +
                        "    \"lang\": \"JS\", " +
                        "    \"type\": \"callCandidate\", " +
                        "    \"key\": \"" + function.getMethodNameText() + "\", " +
                        "    \"value\": {" +
                        "        \"receiver\": \"" + referencedObject.getText() + "\", " +
                        "        \"file\": \"" + call.getContainingFile().getName() + "\"}}, ";
            }

            request += "null]}";

        }

        return request;
    }
}
