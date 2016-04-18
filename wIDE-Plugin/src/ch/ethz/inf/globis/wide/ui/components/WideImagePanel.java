package ch.ethz.inf.globis.wide.ui.components;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * Created by fabian on 16.04.16.
 */
public class WideImagePanel extends JPanel {
    private BufferedImage m_image;

    public WideImagePanel(URL imageUrl) {
        setOpaque(false);
        try {
            m_image = ImageIO.read(imageUrl);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(getImageWidth(), getImageHeight());
    }

    @Override
    public Dimension getMaximumSize() {
        return new Dimension(getImageWidth(), getImageHeight());
    }

    @Override
    public Dimension getMinimumSize() {
        return new Dimension(getImageWidth(), getImageHeight());
    }

    public int getImageHeight() {
        return m_image.getHeight();
    }

    public int getImageWidth() {
        return m_image.getWidth();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int x_coord = (getWidth() - getImageWidth()) / 2;
        g.drawImage(m_image, x_coord, 0, null); // see javadoc for more info on the parameters
    }
}
