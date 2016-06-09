package ch.ethz.inf.globis.wide.compatibility;

import ch.ethz.inf.globis.wide.communication.WideHttpCommunicator;
import ch.ethz.inf.globis.wide.io.query.WideQueryRequest;
import ch.ethz.inf.globis.wide.io.query.WideQueryResponse;
import ch.ethz.inf.globis.wide.language.IWideLanguageHandler;
import ch.ethz.inf.globis.wide.registry.WideLanguageRegistry;
import com.intellij.codeInsight.highlighting.HighlightManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.markup.EffectType;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import io.netty.util.internal.ConcurrentSet;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by fabian on 06.06.16.
 */
public class WideCompatibilityTraverser {

    private Map<String, WidePsiElementRequestEntry> registeredObjects;
    private Editor editor;
    private PsiFile file;

    public WideCompatibilityTraverser() {
        this.registeredObjects = new HashMap();
    }

    public void traverseFile(Editor editor, PsiFile file) {
        // reset Objects all the time
        this.registeredObjects = new ConcurrentHashMap();
        this.editor = editor;
        traverseFileRec(file);

        WideQueryRequest request = buildRequest();
        WideQueryResponse response = WideHttpCommunicator.sendCompatibilityRequest(request);
        handleResponse(response);
    }

    private void traverseFileRec(PsiElement element) {
        IWideLanguageHandler handler = WideLanguageRegistry.getInstance().getLanguageHandler(element.getClass());
        if (handler != null) {
            WideQueryRequest request = handler.getLanguageParser().buildDocumentationQuery(editor, file, element, element);
            if (request != null) {
                String key = request.getKey() + "/" + handler.getLanguageAbbreviation();

                PsiElement actualElement = handler.getLanguageParser().getRelevantElement(element);
                if (actualElement != null) {
                    registeredObjects.putIfAbsent(key, new WidePsiElementRequestEntry(new ConcurrentSet(), request));
                    registeredObjects.get(key).addElement(actualElement);
                }
            }
        }

        for (PsiElement child : element.getChildren()) {
            traverseFileRec(child);
        }
    }

    private WideQueryRequest buildRequest() {
        WideQueryRequest request = new WideQueryRequest();

        for (WidePsiElementRequestEntry entry : registeredObjects.values()) {
            request.addChild(entry.getRequest());
        }

        return request;
    }

    private void handleResponse(WideQueryResponse response) {
        for (WideQueryResponse childResponse : response.getSubResults()) {
            String key = childResponse.getKey() + "/" + childResponse.getLang();

            for (PsiElement element : registeredObjects.get(key).getElements()) {
                editor.getDocument().createRangeMarker(element.getTextRange());

                // TODO: first remove all other highlights

                double compatibility = childResponse.calculateCompatibility();

                //if (compatibility < 2.0) {
                    int red = (int) Math.floor(150 * (1-compatibility));
                    Color highlightColor = new Color(red, 150-red, 0);

                    HighlightManager.getInstance(editor.getProject()).addOccurrenceHighlight(editor, element.getTextRange().getStartOffset(), element.getTextRange().getEndOffset(), new TextAttributes(Color.WHITE, highlightColor, Color.BLUE, EffectType.BOXED, 0), HighlightManager.HIDE_BY_ESCAPE, null, highlightColor);
                //}
            }
        }
    }

    private class WidePsiElementRequestEntry {
        private ConcurrentSet<PsiElement> elements;
        private WideQueryRequest request;

        public WidePsiElementRequestEntry(@NotNull ConcurrentSet<PsiElement> elements, @NotNull WideQueryRequest request) {
            this.elements = elements;
            this.request = request;
        }

        public ConcurrentSet<PsiElement> getElements() {
            return elements;
        }

        public void addElement(PsiElement element) {
            this.elements.add(element);
        }

        public WideQueryRequest getRequest() {
            return request;
        }

        public void setRequest(WideQueryRequest request) {
            this.request = request;
        }
    }
}
