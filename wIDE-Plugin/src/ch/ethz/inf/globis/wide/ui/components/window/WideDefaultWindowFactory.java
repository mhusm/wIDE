package ch.ethz.inf.globis.wide.ui.components.window;

import ch.ethz.inf.globis.wide.io.query.WideQueryResponse;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.psi.PsiElement;

import javax.swing.*;

/**
 * Created by fabian on 12.05.16.
 */
public class WideDefaultWindowFactory extends WideWindowFactory {

    public WideDefaultWindowFactory() {
        // wait for JavaFX to be ready
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                initWideGUI();
            }
        });
    }

    @Override
    public void showLookupWindow(ToolWindow toolWindow, WideQueryResponse result) {
        // noop
    }

    @Override
    public void showSuggestionWindow(WideQueryResponse suggestion, ToolWindow toolWindow, PsiElement element, Editor editor) {
        // noop
    }
}
