package ch.ethz.inf.globis.wide.language;

import ch.ethz.inf.globis.wide.communication.WideHttpCommunicator;
import ch.ethz.inf.globis.wide.io.query.WideQueryRequest;
import ch.ethz.inf.globis.wide.io.query.WideQueryResponse;
import ch.ethz.inf.globis.wide.language.IWideLanguageHandler;
import ch.ethz.inf.globis.wide.ui.components.window.WideWindowFactory;
import clojure.lang.IFn;
import com.intellij.codeInsight.lookup.Lookup;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.ide.IdeEventQueue;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;

/**
 * Created by fabian on 28/07/16.
 */
public final class WideLookupHandler {

    private volatile int LOOKUP_ID = 0;
    private WideQueryRequest request;
    private WideQueryResponse response;

    private static WideLookupHandler INSTANCE = new WideLookupHandler();

    private WideLookupHandler() {
    }

    public static WideLookupHandler getInstance() {
        return INSTANCE;
    }

    public void doDocumentationLookupInBackground(IWideLanguageHandler handler, Editor editor, PsiFile file, PsiElement startElement, PsiElement endElement) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                int lookupId = ++LOOKUP_ID;

                Project project = editor.getProject();
                ToolWindow window = ToolWindowManager.getInstance(project).getToolWindow("wIDE");


                // SHOW WAITING
                if (lookupId == LOOKUP_ID) {
                    handler.getWindowFactory().showWaitingWindow(window);
                } else {
                    // there is already a new request
                    return;
                }

                // PARSE FILE
                if (lookupId == LOOKUP_ID) {
                    ApplicationManager.getApplication().invokeAndWait(new Runnable() {
                        @Override
                        public void run() {
                            request = handler.getDocumentationRequest(editor, file, startElement, endElement);
                        }
                    }, ModalityState.any());
                } else {
                    // there is already a new request
                    return;
                }

                // SEND REQUEST
                if (lookupId == LOOKUP_ID) {
                    if (request != null) {
                        response = WideHttpCommunicator.sendRequest(request);
                    } else {
                        handler.getWindowFactory().showErrorWindow("Sorry, this element is not supported.", window);
                        return;
                    }
                } else {
                    // there is already a new request
                    return;
                }

                // SHOW RESULT
                if (lookupId == LOOKUP_ID) {
                    if (response != null) {
                        IdeEventQueue.getInstance().doWhenReady(new Runnable() {
                            @Override
                            public void run() {
                                handler.showDocumentationResults(response, startElement, editor, window);
                            }
                        });
                    } else {
                        handler.getWindowFactory().showErrorWindow("Sorry, we did not find any results.", window);
                    }
                } else {
                    // there is already a new request
                    return;
                }
            }
        });

        thread.start();
    }

    public void doSuggestionLookupInBackground(IWideLanguageHandler handler, LookupElement lookupElement, PsiElement psiElement, Lookup lookup) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                int lookupId = ++LOOKUP_ID;

                Project project = lookup.getEditor().getProject();
                ToolWindow window = ToolWindowManager.getInstance(project).getToolWindow("wIDE");


                // SHOW WAITING
                if (lookupId == LOOKUP_ID) {
                    handler.getWindowFactory().showWaitingWindow(window);
                } else {
                    // there is already a new request
                    return;
                }

                // PARSE FILE
                if (lookupId == LOOKUP_ID) {
                    ApplicationManager.getApplication().invokeAndWait(new Runnable() {
                        @Override
                        public void run() {
                            request = handler.getSuggestionRequest(lookupElement, psiElement, lookup);
                        }
                    }, ModalityState.any());
                } else {
                    // there is already a new request
                    return;
                }

                // SEND REQUEST
                if (lookupId == LOOKUP_ID) {
                    if (request != null) {
                        response = WideHttpCommunicator.sendRequest(request);
                    } else {
                        handler.getWindowFactory().showErrorWindow("Sorry, this element is not supported.", window);
                        return;
                    }
                } else {
                    // there is already a new request
                    return;
                }

                // SHOW RESULT
                if (lookupId == LOOKUP_ID) {
                    if (response != null) {
                        IdeEventQueue.getInstance().doWhenReady(new Runnable() {
                            @Override
                            public void run() {
                                handler.showSuggestionResults(response, lookup.getEditor(), window);
                            }
                        });
                    } else {
                        handler.getWindowFactory().showErrorWindow("Sorry, we did not find any results.", window);
                    }
                } else {
                    // there is already a new request
                    return;
                }
            }
        });

        thread.start();
    }

}
