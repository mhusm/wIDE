package ch.ethz.inf.globis.wide.io.query;

import ch.ethz.inf.globis.wide.ui.components.panel.WideJFXPanel;
import ch.ethz.inf.globis.wide.ui.components.panel.WideResizablePaneBox;
import javafx.embed.swing.JFXPanel;
import javafx.scene.control.TabPane;
import javafx.scene.web.WebView;
import org.codehaus.jettison.json.JSONObject;

/**
 * Created by fabian on 30.05.16.
 */
public abstract class AbstractWideSourceResult {
    public AbstractWideSourceResult(JSONObject obj) {
        // Constructor must be implemented in that style
    }

    public abstract void showContent(WideResizablePaneBox paneBox);

    public abstract void showPopup(JFXPanel panel);

    public double calculateCompatibility() {
        // default value -> override, if source provides compatibility results
        return 2.0;
    };
}
