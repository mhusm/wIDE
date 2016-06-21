package ch.ethz.inf.globis.wide.ui.components;

import ch.ethz.inf.globis.wide.ui.components.panel.WideJFXPanel;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import javafx.scene.web.WebView;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;

/**
 * Created by fabian on 18.04.16.
 */
public class WideContentBuilder {

    protected WideJFXPanel addNewJFXPanleToWindow(String title, ToolWindow toolWindow) {
        WideJFXPanel panel = new WideJFXPanel();

        // set panel to toolWindow
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content summaryContent = contentFactory.createContent(panel, title, false);

        toolWindow.getContentManager().removeAllContents(true);
        toolWindow.getContentManager().addContent(summaryContent);
        return panel;
    }
}
