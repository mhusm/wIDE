package ch.ethz.inf.globis.wide.sources.mdn;

import ch.ethz.inf.globis.wide.io.query.AbstractWideSourceResult;
import ch.ethz.inf.globis.wide.ui.components.WideContentBuilder;
import ch.ethz.inf.globis.wide.ui.components.panel.WideResizablePane;
import ch.ethz.inf.globis.wide.ui.components.panel.WideResizablePaneBox;
import com.intellij.openapi.editor.Document;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.apache.commons.io.FileUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

/**
 * Created by fabian on 04.04.16.
 */
public class WideMDNResult extends AbstractWideSourceResult {
    private Map<String, String> content = new LinkedHashMap();

    public WideMDNResult(JSONObject res) {
        super(res);

        Iterator<String> keys = res.keys();

        while(keys.hasNext()) {
            String key = keys.next();
            content.put(key, res.optString(key));
        }
    }

    public void showContent(WideResizablePaneBox paneBox) {
        for (String key : content.keySet()) {
            if (key.equals("compatibility")
                    || key.equals("Specifications")
                    || key.equals("Gecko-specific_notes")) {
            } else {
                paneBox.addPane(getResizablePane(content.get(key)));
            }
        }
    }

    private WideResizablePane getResizablePane(String content) {
        WideResizablePane pane = new WideResizablePane(getBrushWebView(content));
        return pane;

    }

    public void showPopup(JFXPanel panel) {
        WebView webView = getWebView(content.get("summary"));
        Scene scene = new Scene(webView);
        panel.setScene(scene);
    }

    private WebView getWebView(String content) {
        WebView webView = new WebView();
        webView.getEngine().setUserStyleSheetLocation(WideContentBuilder.class.getResource("/stylesheets/MDNStyleSheet.css").toString());
        webView.getEngine().loadContent(content);

        return webView;
    }

    private WebView getBrushWebView(String content) {
        try {
            File highlightFile = new File(WideContentBuilder.class.getResource("/syntaxhighlighter/prism.js").toURI());
            String highlight = FileUtils.readFileToString(highlightFile);

            File highlightCssFile = new File(WideContentBuilder.class.getResource("/syntaxhighlighter/prism.css").toURI());
            String highlightCss = FileUtils.readFileToString(highlightCssFile);

            File mdnCssFile = new File(WideContentBuilder.class.getResource("/stylesheets/mdn.css").toURI());
            String mdnCss = FileUtils.readFileToString(mdnCssFile);

            File mdnWikiCssFile = new File(WideContentBuilder.class.getResource("/stylesheets/mdn_wiki.css").toURI());
            String mdnWikiCss = FileUtils.readFileToString(mdnWikiCssFile);

            String html = "<html><head>";
            html += "<script>" + highlight + "</script>";
            html += "<style>" + highlightCss + "</style>";
            html += "<style>" + mdnCss + "</style>";
            html += "<style>" + mdnWikiCss + "</style>";
            html += "</head><body>";
            html += content;
            html += "</body>";
            html += "</html>";

            WebView webView = getWebView(html);
            return webView;
        } catch (IOException e) {
            e.printStackTrace();
            return getWebView(content);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return getWebView(content);
        }
    }
}
