package ch.ethz.inf.globis.wide.parsing.javascript;

import ch.ethz.inf.globis.wide.communication.WideHttpCommunicator;
import ch.ethz.inf.globis.wide.lookup.io.WideQueryRequest;
import ch.ethz.inf.globis.wide.parsing.WideAbstractLanguageHandler;
import ch.ethz.inf.globis.wide.lookup.io.WideQueryResponse;
import com.intellij.lang.javascript.psi.JSCallExpression;
import com.intellij.lang.javascript.psi.JSDefinitionExpression;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.codehaus.jettison.json.JSONObject;

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
        WideQueryRequest request = buildRequest(editor, file, startElement, endElement);

        if (request != null) {
            WideQueryResponse response = WideHttpCommunicator.sendRequest(request);
            results.add(response);
        } else {
            //results.add(new WideQueryResponse("No JS function:"));
        }

        return results;
    }

    public WideQueryRequest buildRequest(Editor editor, PsiFile file, PsiElement startElement, PsiElement endElement) {
        //TODO: distinguish file

        //TODO: multiple calls?

        JSCallExpression currentCall = WideJSParser.findLowestCommonCallExpression(startElement, endElement);

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
}
