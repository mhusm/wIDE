package ch.ethz.inf.globis.wide.completion;

import ch.ethz.inf.globis.wide.language.IWideLanguageHandler;
import ch.ethz.inf.globis.wide.logging.WideLogger;
import ch.ethz.inf.globis.wide.registry.WideLanguageRegistry;
import ch.ethz.inf.globis.wide.ui.components.window.WideDefaultWindowFactory;
import com.intellij.codeInsight.lookup.Lookup;
import com.intellij.codeInsight.lookup.LookupActionProvider;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementAction;
import com.intellij.ide.IdeEventQueue;
import com.intellij.lang.javascript.psi.JSElement;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.psi.css.CssElement;
import com.intellij.psi.xml.XmlElement;
import com.intellij.util.Consumer;
import javafx.application.Platform;

/**
 * Created by fabian on 19.05.16.
 */
public class WideLookupActionProvider implements LookupActionProvider {

    private final static WideLogger LOGGER = new WideLogger(WideLookupActionProvider.class.getName());

    @Override
    public void fillActions(LookupElement lookupElement, Lookup lookup, Consumer<LookupElementAction> consumer) {
        // Show waiting window
        Project project = lookup.getEditor().getProject();
        ToolWindow window = ToolWindowManager.getInstance(project).getToolWindow("wIDE");
        WideDefaultWindowFactory windowFactory = new WideDefaultWindowFactory();
        windowFactory.showWaitingWindow(window);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                IWideLanguageHandler languageHandler = WideLanguageRegistry.getInstance().getLanguageHandler(lookup.getPsiElement().getParent().getClass());

                if (languageHandler != null) {
                    IdeEventQueue.getInstance().doWhenReady(new Runnable() {
                        @Override
                        public void run() {
                            languageHandler.getSuggestionDocumentation(lookupElement, lookup);
                        }
                    });
                }
            }
        });
        thread.start();
    }
}
