package ch.ethz.inf.globis.wide.language.css;

import ch.ethz.inf.globis.wide.io.query.WideQueryResponse;
import ch.ethz.inf.globis.wide.ui.components.popup.WidePopupFactory;
import com.intellij.openapi.editor.Editor;

/**
 * Created by fabian on 12.05.16.
 */
public class WideCssPopupFactory extends WidePopupFactory {

    private static final WideCssPopupFactory INSTANCE = new WideCssPopupFactory();

    private WideCssPopupFactory() {

    }

    public static WideCssPopupFactory getInstance() {
        return INSTANCE;
    }


    @Override
    public void showLookupResults(WideQueryResponse parentResult, WideQueryResponse subResult, Editor editor) {
    }

}
