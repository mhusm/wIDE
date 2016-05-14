package ch.ethz.inf.globis.wide.registry;

import ch.ethz.inf.globis.wide.language.IWideLanguageHandler;
import ch.ethz.inf.globis.wide.language.css.WideCssHandler;
import ch.ethz.inf.globis.wide.language.html.WideHtmlHandler;
import ch.ethz.inf.globis.wide.language.javascript.WideJavascriptHandler;
import com.intellij.lang.javascript.psi.JSElement;
import com.intellij.psi.PsiElement;
import com.intellij.psi.css.CssElement;
import com.intellij.psi.xml.XmlElement;

import java.util.HashMap;

/**
 * Created by fabian on 12.05.16.
 */
public class WideLanguageRegistry {

    private static final WideLanguageRegistry INSTANCE = new WideLanguageRegistry();

    private HashMap<Class<? extends PsiElement>, IWideLanguageHandler> handlerRegistry = new HashMap<>();

    private WideLanguageRegistry() {
        handlerRegistry.put(XmlElement.class, new WideHtmlHandler());
        handlerRegistry.put(JSElement.class, new WideJavascriptHandler());
        handlerRegistry.put(CssElement.class, new WideCssHandler());
    }

    public static WideLanguageRegistry getInstance() {
        return INSTANCE;
    }

    public IWideLanguageHandler getLanguageHandler(Class<? extends PsiElement> type) {
        for (Class<? extends PsiElement> clazz : handlerRegistry.keySet()) {
            if (clazz.isAssignableFrom(type)) {
                return handlerRegistry.get(clazz);
            }
        }

        return null;
    }
}
