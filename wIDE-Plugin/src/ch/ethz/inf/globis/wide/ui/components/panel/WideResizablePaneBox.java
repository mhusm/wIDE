package ch.ethz.inf.globis.wide.ui.components.panel;

import ch.ethz.inf.globis.wide.ui.components.WideContentBuilder;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;

/**
 * Created by fabian on 02.06.16.
 */
public class WideResizablePaneBox extends ScrollPane {

    private VBox box;

    public WideResizablePaneBox() {
        box = new VBox(10);
        box.setPadding(new Insets(10, 10, 10, 10));

        this.setContent(box);
        this.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        this.setFitToWidth(true);
    }

    @Deprecated
    public ObservableList<Node> getChildren() {
        return super.getChildren();
    }

    public void addPane(WideResizablePane pane) {
        box.getChildren().add(pane);
    }

    public int getContentCount() {
        return super.getChildren().size();
    }

}
