package ch.ethz.inf.globis.wide.ui.components.panel;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.Image;
import java.io.IOException;
import java.net.URL;

/**
 * Created by fabian on 16.04.16.
 */
public class WideImagePanel extends ImageView {

    public WideImagePanel(URL imageUrl, int width, int height) {
        try {
            javafx.scene.image.WritableImage image = new WritableImage(width, height);
            SwingFXUtils.toFXImage(ImageIO.read(imageUrl), image);
            setImage(image);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
