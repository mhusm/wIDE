package ch.ethz.inf.globis.wide.ui.components.panel;

import ch.ethz.inf.globis.wide.ui.components.dialog.WideCompatibilityPreferenceDialog;
import com.intellij.ide.IdeEventQueue;
import com.intellij.util.containers.Stack;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import javax.swing.*;

/**
 * Created by fabian on 26.05.16.
 */
public class WideJFXPanel extends JFXPanel {

    private StackPane content = new StackPane();

    public WideJFXPanel() {
        WideJFXPanel panel = this;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                initializePanelFx(panel);
            }
        });
    }

    public StackPane getEmptyContentPane() {
        this.content.getChildren().clear();
        return this.content;
    }

    private void initializePanelFx(WideJFXPanel panel) {
        StackPane stackPane = new StackPane();
        stackPane.setPrefSize(700, 700);

        stackPane.getChildren().add(content);
        StackPane.setAlignment(content, Pos.TOP_CENTER);
        StackPane.setMargin(content, new Insets(48, 0, 35, 0));

        Text text = new Text("Search documentation with ⌥F");
        text.setStyle("-fx-fill: darkslategray");
        text.setTextAlignment(TextAlignment.CENTER);
        stackPane.getChildren().add(text);
        StackPane.setMargin(text, new Insets(0, 0, 10, 0));
        StackPane.setAlignment(text, Pos.BOTTOM_CENTER);

        StackPane menu = new StackPane();
        menu.setStyle("-fx-background-color: darkslategray");
        menu.setMaxHeight(20);
        menu.setPadding(new Insets(10, 10, 10, 10));
        menu.setAlignment(Pos.TOP_RIGHT);

        Image headerLogo = new Image(WideJFXPanel.class.getResource("/images/logo_white.png").toString());
        ImageView headerLogoImageView = new ImageView(headerLogo);
        menu.getChildren().add(headerLogoImageView);
        StackPane.setAlignment(headerLogoImageView, Pos.CENTER_LEFT);

        Image settingsImage = new Image(WideJFXPanel.class.getResource("/images/settings_button.png").toString());//create an image
        ImageView settingsImageView = new ImageView(settingsImage);//create an imageView and pass the image

        javafx.scene.control.Button pref = new Button();
        pref.setGraphic(settingsImageView);
        pref.setText("Compatibility Preferences");
        pref.setGraphicTextGap(10);
        pref.setStyle("-fx-background-color: #777777; -fx-text-fill: white");
        pref.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(javafx.scene.input.MouseEvent event) {
                if (event.getClickCount() == 1) {
                    IdeEventQueue.getInstance().doWhenReady(new Runnable() {
                        @Override
                        public void run() {
                            WideCompatibilityPreferenceDialog wrapper = new WideCompatibilityPreferenceDialog(panel, true);
                            wrapper.setTitle("Preferences");
                            wrapper.show();
                        }
                    });
                }
            }
        });

        menu.getChildren().add(pref);
        StackPane.setAlignment(pref, Pos.CENTER_RIGHT);

        stackPane.getChildren().add(menu);
        StackPane.setAlignment(menu, Pos.TOP_CENTER);

        Scene scene = new Scene(stackPane);
        this.setScene(scene);
    }
}
