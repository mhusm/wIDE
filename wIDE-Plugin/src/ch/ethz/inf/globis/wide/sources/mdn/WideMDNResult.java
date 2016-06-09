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
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by fabian on 04.04.16.
 */
public class WideMDNResult extends AbstractWideSourceResult {
    private String summary;
    private String examples;
    private String compatibility;
    private String notes;
    private String seeAlso;
    private String attributes;
    private String syntax;

    public WideMDNResult(JSONObject res) {
        super(res);

        setSummary(res.optString("summary"));
        setCompatibility(res.optString("compatibility"));
        setNotes(res.optString("notes"));
        setSeeAlso(res.optString("seeAlso"));
        setAttributes(res.optString("attributes"));
        setSyntax(res.optString("syntax"));
        setExamples(res.optString("examples"));

//        // add examples
//        try {
//            if (res.optString("examples") != null) {
//                JSONArray jsonExamples = new JSONArray(res.optString("examples"));
//                int i = 0;
//                while (i < jsonExamples.length()) {
//                    addExample(new WideMDNExample(jsonExamples.getJSONObject(i)));
//                    i++;
//                }
//            }
//        } catch (JSONException e) {
//            //e.printStackTrace();
//            // dont' do anything -> no examples found.
//        }
    }

    public void showContent(WideResizablePaneBox paneBox) {
        if (getSummary() != null && getSummary() != "") {
            paneBox.addPane(getResizablePane(getSummary()));
        }
        if (getSyntax() != null && getSyntax() != "") {
            paneBox.addPane(getResizablePane(getSyntax()));
        }
        if (getAttributes() != null && getAttributes() != "") {
            paneBox.addPane(getResizablePane(getAttributes()));
        }
        if (getExamples() != null && getExamples() != "") {
            paneBox.addPane(getResizablePane(getExamples()));
        }
        if (getNotes() != null && getNotes() != "") {
            paneBox.addPane(getResizablePane(getNotes()));
        }
//        if (getCompatibility() != null && getCompatibility() != "") {
//            paneBox.addPane(getResizablePane(getCompatibility()));
//        }
    }

    private WideResizablePane getResizablePane(String content) {
        WideResizablePane pane = new WideResizablePane(getBrushWebView(content));
        return pane;

    }

    public void showPopup(JFXPanel panel) {
        WebView webView = getWebView(getSummary());
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

    public String getSyntax() {
        return syntax;
    }

    public void setSyntax(String syntax) {
        this.syntax = syntax;
    }

    public String getAttributes() {
        return attributes;
    }

    public void setAttributes(String attributes) {
        this.attributes = attributes;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getSeeAlso() {
        return seeAlso;
    }

    public void setSeeAlso(String seeAlso) {
        this.seeAlso = seeAlso;
    }

    public String getNotes() {

        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getCompatibility() {

        return compatibility;
    }

    public void setCompatibility(String compatibility) {
        this.compatibility = compatibility;
    }

    public String getExamples() {

        return examples;
    }

    public void setExamples(String examples) {
        this.examples = examples;
    }

//    public void addExample(WideMDNExample example) {
//        this.examples.add(example);
//    }
}
