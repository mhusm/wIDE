package ch.ethz.inf.globis.wide.compatibility;

import ch.ethz.inf.globis.wide.communication.WideHttpCommunicator;
import ch.ethz.inf.globis.wide.io.query.WideQueryRequest;
import ch.ethz.inf.globis.wide.io.query.WideQueryResponse;
import ch.ethz.inf.globis.wide.language.IWideLanguageHandler;
import ch.ethz.inf.globis.wide.logging.WideLogger;
import ch.ethz.inf.globis.wide.registry.WideLanguageRegistry;
import ch.ethz.inf.globis.wide.ui.components.window.WideDefaultWindowFactory;
import com.intellij.codeInsight.highlighting.HighlightManager;
import com.intellij.ide.IdeEventQueue;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.markup.EffectType;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import io.netty.util.internal.ConcurrentSet;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by fabian on 06.06.16.
 */
public class WideCompatibilityTraverser {

    private final static WideLogger LOGGER = new WideLogger(WideCompatibilityTraverser.class.getName());

    private ConcurrentHashMap<String, WidePsiElementRequestEntry> registeredObjects;
    private Map<String, WideQueryRequest> keysToBeHandled;
    private Map<String, Double> keyToCompatibiltiyMap;
    private TreeMap<Double, Map> registeredIssues;

    private Editor editor;

    private PsiFile file;

    public WideCompatibilityTraverser() {
        this.registeredObjects = new ConcurrentHashMap();
        this.keyToCompatibiltiyMap = new HashMap();
        this.keysToBeHandled = new HashMap();
        this.registeredIssues = new TreeMap();
    }

    public void traverseProject(Project project, List<PsiFile> files) {
        // reset Objects all the time
        this.registeredObjects = new ConcurrentHashMap();
        this.keyToCompatibiltiyMap = new HashMap();
        this.keysToBeHandled = new HashMap();
        this.registeredIssues = new TreeMap();

        this.editor = null;

        for (PsiFile psiFile : files) {
            LOGGER.info("Scan compatibiltiy for: " + psiFile.getName());

            // Show waiting window
            ToolWindow window = ToolWindowManager.getInstance(project).getToolWindow("wIDE");
            WideDefaultWindowFactory windowFactory = new WideDefaultWindowFactory();
            windowFactory.showWaitingWindow(window);

            ApplicationManager.getApplication().runReadAction(new Runnable() {
                @Override
                public void run() {
                    file = psiFile;
                    traverseFileRec(psiFile);
                }
            });

            WideQueryRequest request = buildRequest();
            WideQueryResponse response = WideHttpCommunicator.sendCompatibilityRequest(request);

            IdeEventQueue.getInstance().doWhenReady(new Runnable() {
                @Override
                public void run() {
                    handleResponse(response);

                    ToolWindow window = ToolWindowManager.getInstance(project).getToolWindow("wIDE");
                    new WideDefaultWindowFactory().showCompatibilityIssues(registeredIssues, window, editor);
                }
            });
        }
    }

    public void traverseFile(Editor editor) {
        // reset Objects all the time
        this.registeredObjects = new ConcurrentHashMap();
        this.keyToCompatibiltiyMap = new HashMap();
        this.keysToBeHandled = new HashMap();
        this.registeredIssues = new TreeMap();

        this.editor = editor;

        ApplicationManager.getApplication().runReadAction(new Runnable() {
            @Override
            public void run() {
                file = PsiDocumentManager.getInstance(editor.getProject()).getPsiFile(editor.getDocument());
                traverseFileRec(file);
            }
        });

        WideQueryRequest request = buildRequest();
        WideQueryResponse response = WideHttpCommunicator.sendCompatibilityRequest(request);

        IdeEventQueue.getInstance().doWhenReady(new Runnable() {
            @Override
            public void run() {
                handleResponse(response);

                ToolWindow window = ToolWindowManager.getInstance(editor.getProject()).getToolWindow("wIDE");
                new WideDefaultWindowFactory().showCompatibilityIssues(registeredIssues, window, editor);
            }
        });
    }

    private void traverseFileRec(PsiElement element) {
        IWideLanguageHandler handler = WideLanguageRegistry.getInstance().getLanguageHandler(element.getClass());
        if (handler != null) {
            WideQueryRequest request = handler.getLanguageParser().buildDocumentationQuery(file, element, element);
            if (request != null) {
                String key = request.getKey() + "/" + handler.getLanguageAbbreviation();

                PsiElement actualElement = handler.getLanguageParser().getRelevantElement(element);
                if (actualElement != null) {
                    if (!registeredObjects.containsKey(key)) {
                        // mark element as (potentially) new issue
                        registeredObjects.putIfAbsent(key, new WidePsiElementRequestEntry(new ConcurrentSet(), request));
                        registeredObjects.get(key).addElement(actualElement);
                        keysToBeHandled.put(key, request);

                    } else if (!keysToBeHandled.keySet().contains(key)) {
                        // register element for compatibiltiy query
                        registeredObjects.get(key).addElement(actualElement);

                        IdeEventQueue.getInstance().doWhenReady(new Runnable() {
                            @Override
                            public void run() {
                                registeredIssues.get(keyToCompatibiltiyMap.get(key)).putIfAbsent(key, registeredObjects.get(key)); //add(actualElement);
                                Color highlightColor = getHighlightColor(keyToCompatibiltiyMap.get(key));
                                highlightElement(actualElement, highlightColor);
                            }
                        });

                    } else {
                        registeredObjects.get(key).addElement(actualElement);
                        keysToBeHandled.put(key, request);
                    }
                }
            }
        }

        if (keysToBeHandled.size() > 10) {
            WideQueryRequest compatibilityQuery = buildRequest();
            keysToBeHandled = new HashMap();

            LOGGER.info("COMPATIBILITY REQUEST WILL BE SENT.");
            WideQueryResponse response = WideHttpCommunicator.sendCompatibilityRequest(compatibilityQuery);
            updateKeyToCompatibilityMap(response);

            IdeEventQueue.getInstance().doWhenReady(new Runnable() {
                @Override
                public void run() {
                    handleResponse(response);
                }
            });
        }

        for (PsiElement child : element.getChildren()) {
            traverseFileRec(child);
        }
    }

    private WideQueryRequest buildRequest() {
        WideQueryRequest request = new WideQueryRequest();

        for (WideQueryRequest subRequest : keysToBeHandled.values()) {
            request.addChild(subRequest);
        }

        return request;
    }

    private void updateKeyToCompatibilityMap(WideQueryResponse response) {
        for (WideQueryResponse childResponse : response.getSubResults()) {
            String key = childResponse.getKey() + "/" + childResponse.getLang();

            double compatibility = childResponse.calculateCompatibility();

            keyToCompatibiltiyMap.put(key, compatibility);
        }
    }

    private void highlightElement(PsiElement element, Color highlightColor) {
        if (editor != null) {
            HighlightManager.getInstance(editor.getProject()).addOccurrenceHighlight(
                    editor,
                    element.getTextRange().getStartOffset(),
                    element.getTextRange().getEndOffset(),
                    new TextAttributes(Color.WHITE, highlightColor, null, EffectType.BOXED, 0),
                    HighlightManager.HIDE_BY_ESCAPE,
                    null,
                    highlightColor);
        }
    }

    private void handleResponse(WideQueryResponse response) {

        for (WideQueryResponse childResponse : response.getSubResults()) {
            String key = childResponse.getKey() + "/" + childResponse.getLang();

            if (keyToCompatibiltiyMap.get(key) == null) {
                updateKeyToCompatibilityMap(response);
            }

            double compatibility = keyToCompatibiltiyMap.get(key);
            Color highlightColor = getHighlightColor(compatibility);

            for (PsiElement element : registeredObjects.get(key).getElements()) {

                highlightElement(element, highlightColor);

                if (registeredIssues.get(compatibility) == null) {
                    registeredIssues.put(compatibility, new HashMap());
                }
                registeredIssues.get(compatibility).putIfAbsent(key, registeredObjects.get(key)); //add(element);
            }
        }
    }

    public static Color getHighlightColor(double compatibility) {
        int red = 0;
        int green = 0;
        int blue = 0;

        if (compatibility >= 0.5) {
            red = (int) Math.floor(255 + 255 * 2 * (0.5 - compatibility));
            green = (int) Math.floor(255 + 255 * (0.5 - compatibility));
            blue = (int) Math.floor(51 + 51 * 2 * (0.5 - compatibility));
        } else {
            red = (int) Math.floor(255 - 255 * (0.5 - compatibility));
            green = (int) Math.floor(255 - 255 * 2 * (0.5 - compatibility));
            blue = (int) Math.floor(51 - 51 * 2 * (0.5 - compatibility));
        }

        return new Color(red, green, blue);
    }

    public class WidePsiElementRequestEntry {
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
    }
}
