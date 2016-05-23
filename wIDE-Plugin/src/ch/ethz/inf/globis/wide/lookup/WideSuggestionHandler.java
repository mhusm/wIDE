package ch.ethz.inf.globis.wide.lookup;

import ch.ethz.inf.globis.wide.language.IWideLanguageHandler;
import ch.ethz.inf.globis.wide.logging.WideLogger;
import ch.ethz.inf.globis.wide.registry.WideLanguageRegistry;
import ch.ethz.inf.globis.wide.ui.components.popup.WidePopupHelper;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.css.CssElement;

/**
 * Created by fabian on 20.04.16.
 */
public class WideSuggestionHandler {
    private final static WideLogger LOGGER = new WideLogger(WideDocumentationHandler.class.getName());

    public static void differentiateElements(Editor editor, PsiElement element, String newChar) {
//
//        IWideLanguageHandler handler = WideLanguageRegistry.getInstance().getLanguageHandler(element.getParent().getClass());
//
//        if (handler != null) {
//            handler.lookupSuggestions(editor, element, newChar);
//        } else {
//            LOGGER.info("Tried to lookup suggestions for unknown Language.");
//        }
    }
}
