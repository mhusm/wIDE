package ch.ethz.inf.globis.wide.sources.mdn;

import ch.ethz.inf.globis.wide.io.query.AbstractWideSourceResult;
import ch.ethz.inf.globis.wide.ui.components.WideContentBuilder;
import ch.ethz.inf.globis.wide.ui.components.panel.WideResizablePane;
import ch.ethz.inf.globis.wide.ui.components.panel.WideResizablePaneBox;
import javafx.scene.control.TabPane;
import javafx.scene.web.WebView;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fabian on 04.04.16.
 */
public class WideMDNResult extends AbstractWideSourceResult {
    private String summary;
    private List<WideMDNExample> examples = new ArrayList<WideMDNExample>();
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

        // add examples
        try {
            if (res.optString("examples") != null) {
                JSONArray jsonExamples = new JSONArray(res.optString("examples"));
                int i = 0;
                while (i < jsonExamples.length()) {
                    addExample(new WideMDNExample(jsonExamples.getJSONObject(i)));
                    i++;
                }
            }
        } catch (JSONException e) {
            //e.printStackTrace();
            // dont' do anything -> no examples found.
        }
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
//      if (mdn.getExamples() != null) {
//          paneBox.addMdnPane(mdn.getExamples());
//      }
        if (getNotes() != null && getNotes() != "") {
            paneBox.addPane(getResizablePane(getNotes()));
        }
//        if (getCompatibility() != null && getCompatibility() != "") {
//            paneBox.addPane(getResizablePane(getCompatibility()));
//        }
    }

    private WideResizablePane getResizablePane(String content) {
        WebView webView = new WebView();
        webView.getEngine().setUserStyleSheetLocation(WideContentBuilder.class.getResource("/stylesheets/MDNStyleSheet.css").toString());
        webView.getEngine().loadContent(content);

        WideResizablePane pane = new WideResizablePane(webView);
        return pane;

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

    public List<WideMDNExample> getExamples() {

        return examples;
    }

    public void setExamples(List<WideMDNExample> examples) {
        this.examples = examples;
    }

    public void addExample(WideMDNExample example) {
        this.examples.add(example);
    }
}
