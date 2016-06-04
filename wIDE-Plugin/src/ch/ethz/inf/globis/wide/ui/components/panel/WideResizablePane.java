package ch.ethz.inf.globis.wide.ui.components.panel;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.web.WebView;
import javafx.util.Duration;

import java.util.Set;

/**
 * Created by fabian on 02.06.16.
 */
public class WideResizablePane extends VBox {

    private Rectangle clipRect;
    private WebView child;

    private final static double DEFAULT_HEIGHT = 200.0;

    public WideResizablePane(WebView child) {

        this.child = child;

        // hide webview scrollbars whenever they appear.
        child.getChildrenUnmodifiable().addListener(new ListChangeListener<Node>() {
            @Override public void onChanged(Change<? extends Node> change) {
                Set<Node> deadSeaScrolls = child.lookupAll(".scroll-bar");
                for (Node scroll : deadSeaScrolls) {
                    scroll.setVisible(false);
                }
            }
        });

        StackPane.setAlignment(this, Pos.TOP_CENTER);
        StackPane.setMargin(this, new Insets(10, 10, 10, 10));

        initialize();
        initializeStyle();

        Button button = new Button("↓ show more");

        // Set WebView on StackPane (For Layout reasons)
        StackPane header = new StackPane();
        header.getChildren().add(child);
        StackPane.setAlignment(child, Pos.TOP_LEFT);

        // include a footer with the "show more"-button
        StackPane footer = new StackPane();
        footer.getChildren().add(button);
        StackPane.setAlignment(button, Pos.BOTTOM_CENTER);
        StackPane.setMargin(button, new Insets(10, 0, 10, 0));

        this.getChildren().addAll(header, footer);
        VBox.setMargin(child, new Insets(-20, 0, 0, 0));

        // pass scrolling from webView to parent scrollview
        child.setOnScroll(new EventHandler<ScrollEvent>() {
            @Override
            public void handle(ScrollEvent event) {
                ScrollPane pane = (ScrollPane) ((WebView) event.getSource()).getParent().getParent().getParent().getParent().getParent().getParent();
                double position = pane.vvalueProperty().doubleValue();
                double height = ((VBox) pane.getContent()).getHeight();
                pane.setVvalue((position * height - event.getDeltaY()) / height);
                event.consume();
            }
        });

        // Toggle Layout & change button label
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                if (toggleExtendablePane()) {
                    ((Button) e.getSource()).setText("\u2191 show less");
                } else {
                    ((Button) e.getSource()).setText("↓ show more");
                }
            }
        });
    }

    private void initialize() {
        clipRect = new Rectangle();

        // Make WebView & clipRect responsive to resizing of toolWindow
        this.parentProperty().addListener(new ChangeListener<Parent>() {
            @Override
            public void changed(ObservableValue<? extends Parent> observable, Parent oldValue, Parent newValue) {
                clipRect.setWidth(((VBox) newValue).getWidth());
                ((VBox) newValue).widthProperty().addListener(new ChangeListener<Number>() {
                    @Override
                    public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                        if (newValue instanceof Double) {
                            clipRect.setWidth((Double) newValue);
                        }
                    }
                });
            }
        });

        clipRect.setHeight(DEFAULT_HEIGHT);
        clipRect.heightProperty().set(DEFAULT_HEIGHT);
        clipRect.translateYProperty().set(0);

        child.setClip(clipRect);
        child.translateYProperty().set(0);
        child.prefHeightProperty().set(DEFAULT_HEIGHT);
        child.setMinHeight(100);
    }

    private void initializeStyle() {
        this.setStyle("-fx-border-color: #bbbbbb; " +
                "-fx-border-width: 1px; " +
                "-fx-background-color: white;");
    }


    private boolean toggleExtendablePane() {

        int getHeight = (int) child.getEngine().executeScript("Math.max(\n" +
                "        window.innerHeight,\n" +
                "        document.body.offsetHeight,\n" +
                "        document.documentElement.clientHeight\n" +
                "    )");

        clipRect.setWidth(((VBox) getParent()).getWidth());

        if (clipRect.heightProperty().get() > DEFAULT_HEIGHT) {

            // Animation for scroll up.
            Timeline timelineUp = new Timeline();

            // Animation of sliding the search pane up, implemented via
            // clipping.
            final KeyValue kvUp1 = new KeyValue(clipRect.heightProperty(), DEFAULT_HEIGHT);
            final KeyValue kvUp2 = new KeyValue(clipRect.translateYProperty(), 0);

            // The actual movement of the search pane. This makes the table
            // grow.
            final KeyValue kvUp4 = new KeyValue(child.prefHeightProperty(), DEFAULT_HEIGHT);
            final KeyValue kvUp3 = new KeyValue(child.translateYProperty(), 0);

            final KeyFrame kfUp = new KeyFrame(Duration.millis(200), kvUp1, kvUp2, kvUp4, kvUp3);
            timelineUp.getKeyFrames().add(kfUp);
            timelineUp.play();

            return false;

        } else {

            // Animation for scroll down.
            Timeline timelineDown = new Timeline();

            // Animation for sliding the search pane down. No change in size,
            // just making the visible part of the pane
            // bigger.
            final KeyValue kvDwn1 = new KeyValue(clipRect.heightProperty(), getHeight + 30);
            final KeyValue kvDwn2 = new KeyValue(clipRect.translateYProperty(), 0);

            // Growth of the pane.
            final KeyValue kvDwn4 = new KeyValue(child.prefHeightProperty(), getHeight + 30);
            final KeyValue kvDwn3 = new KeyValue(child.translateYProperty(), 0);

            final KeyFrame kfDwn = new KeyFrame(Duration.millis(200), kvDwn1, kvDwn2, kvDwn4, kvDwn3);
            timelineDown.getKeyFrames().add(kfDwn);

            timelineDown.play();
            return true;
        }
    }
}
