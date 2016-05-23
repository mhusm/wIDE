package ch.ethz.inf.globis.wide.completion;

import ch.ethz.inf.globis.wide.language.IWideLanguageHandler;
import ch.ethz.inf.globis.wide.logging.WideLogger;
import ch.ethz.inf.globis.wide.registry.WideLanguageRegistry;
import com.intellij.codeInsight.lookup.Lookup;
import com.intellij.codeInsight.lookup.LookupActionProvider;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementAction;
import com.intellij.ide.IdeEventQueue;
import com.intellij.lang.javascript.psi.JSElement;
import com.intellij.openapi.editor.Editor;
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
}
