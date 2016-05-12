package ch.ethz.inf.globis.wide.ui.components.panel;

import com.intellij.openapi.wm.ToolWindow;

import javax.swing.*;
import java.awt.*;

/**
 * Created by fabian on 16.04.16.
 */
public class WideFullWidthPanel extends JPanel {
    public WideFullWidthPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.white);
        setOpaque(true);
    }

    @Override
    public Dimension getPreferredSize() {
        int height = (int) super.getPreferredSize().getHeight();
        return new Dimension(getParent().getWidth(), height);
    }
}
