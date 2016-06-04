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

//    @Override
//    public ObservableList<Node> getChildren() {
//        return box.getChildren();
//    }

//    public void addMdnPane(String content) {
//        WebView webView = WideContentBuilder.createWebView();
//        webView.getEngine().loadContent(content);
//        WideResizablePane pane = new WideResizablePane(webView);
//        box.getChildren().add(pane);
//    }
//
//    public void addCaniusePane(String content) {
//        WebView webView = new WebView();
//        webView.getEngine().setUserStyleSheetLocation(WideContentBuilder.class.getResource("/stylesheets/CaniuseSheet.css").toString());
//        webView.getEngine().loadContent(content);
//        WideResizablePane pane = new WideResizablePane(webView);
//        box.getChildren().add(pane);
//    }

    @Deprecated
    public ObservableList<Node> getChildren() {
        return super.getChildren();
    }

    public void addPane(WideResizablePane pane) {
        box.getChildren().add(pane);
    }

}
