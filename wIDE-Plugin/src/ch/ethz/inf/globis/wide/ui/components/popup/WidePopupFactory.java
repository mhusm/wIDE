package ch.ethz.inf.globis.wide.ui.components.popup;

import ch.ethz.inf.globis.wide.io.query.WideQueryResponse;
import ch.ethz.inf.globis.wide.ui.components.WideContentBuilder;
import ch.ethz.inf.globis.wide.ui.listener.WideMouseEventListenerHelper;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.ui.popup.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Created by fabian on 06.04.16.
 */
public abstract class WidePopupFactory extends WideContentBuilder {

    private JBPopup popup;

    public void hidePopup(Editor editor) {
        if (popup != null) {
            popup.cancel();
            this.popup = null;
        }
    }

    protected void showPopup(JComponent content, Dimension size, String title, Editor editor) {
        hidePopup(editor);

            ComponentPopupBuilder popupBuilder = JBPopupFactory.getInstance().createComponentPopupBuilder(content, editor.getComponent());
            if (title != null) {
                popupBuilder.setTitle(title);
            }
            JBPopup popup = popupBuilder.createPopup();
            popup.setSize(size);

            popup.addListener(new JBPopupListener() {
                @Override
                public void beforeShown(LightweightWindowEvent lightweightWindowEvent) {
                }

                @Override
                public void onClosed(LightweightWindowEvent lightweightWindowEvent) {
                    WideMouseEventListenerHelper.getInstance().deregisterMouseEventListener();
                }
            });

            this.popup = popup;

        popup.show(JBPopupFactory.getInstance().guessBestPopupLocation(editor));

        this.popup = popup;
    }

    public abstract void showLookupResults(WideQueryResponse parentResult, WideQueryResponse subResult, Editor editor);
}
