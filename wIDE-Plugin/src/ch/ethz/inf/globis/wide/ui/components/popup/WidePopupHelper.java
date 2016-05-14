package ch.ethz.inf.globis.wide.ui.components.popup;

import ch.ethz.inf.globis.wide.lookup.io.WideQueryResponse;
import ch.ethz.inf.globis.wide.ui.components.WideContentBuilder;
import ch.ethz.inf.globis.wide.ui.listener.WideMouseEventListenerHelper;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.ui.popup.*;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.psi.PsiElement;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Created by fabian on 06.04.16.
 */
public abstract class WidePopupHelper extends WideContentBuilder {

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

        //popup.setRequestFocus(true);
        popup.show(JBPopupFactory.getInstance().guessBestPopupLocation(editor));
//        popup.showInBestPositionFor(editor);

        this.popup = popup;
    }

    public void showError(String error, Editor editor) {
        //TODO: implementation
        showPopup(new JLabel(error), new Dimension(300, 50), "Error", editor);
    }

    protected void addResultsRecursive(DefaultTableModel tableModel, WideQueryResponse response) {
        for (WideQueryResponse result : response.getSubResults()) {
            if (result != null) {
                tableModel.addRow(result.getTableRow());
                addResultsRecursive(tableModel, result);
            }
        }
    }

    public abstract void showLookupResults(WideQueryResponse parentResult, WideQueryResponse subResult, Editor editor);
    public abstract void showSuggestions(java.util.List<WideQueryResponse> suggestions, ToolWindow toolWindow, PsiElement element, Editor editor);
}
