package ch.ethz.inf.globis.wide.language.javascript;

import ch.ethz.inf.globis.wide.io.query.WideQueryResponse;
import ch.ethz.inf.globis.wide.ui.components.popup.WidePopupFactory;
import com.intellij.openapi.editor.Editor;

/**
 * Created by fabian on 12.05.16.
 */
public class WideJSPopupFactory extends WidePopupFactory {

    private static final WideJSPopupFactory INSTANCE = new WideJSPopupFactory();

    private WideJSPopupFactory() {

    }

    public static WideJSPopupFactory getInstance() {
        return INSTANCE;
    }

    @Override
    public void showLookupResults(WideQueryResponse parentResult, WideQueryResponse subResult, Editor editor) {
    }
}
