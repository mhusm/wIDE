package ch.ethz.inf.globis.wide.io.query;

import ch.ethz.inf.globis.wide.registry.WideSourceRegistry;
import ch.ethz.inf.globis.wide.ui.components.panel.WideResizablePaneBox;
import ch.ethz.inf.globis.wide.ui.components.window.WideDefaultWindowFactory;
import com.intellij.openapi.progress.AsynchronousExecution;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.TabPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by fabian on 11.03.16.
 */
public class WideQueryResponse {
    private String lang;
    private String type;
    private String key;
    private String value;
    private int level;
    private List<WideQueryResponse> subResults = new ArrayList<WideQueryResponse>();

    private Map<String, AbstractWideSourceResult> sources = new HashMap();

    public WideQueryResponse(String response) {
        this(response, 0);
    }
    public WideQueryResponse(String response, int level) {
        try {
            JSONObject res = new JSONObject(response);
            setLang(res.optString("lang"));
            setType(res.optString("type"));
            setKey(res.optString("key"));
            setValue(res.optString("value"));
            setLevel(level);

            // create mdn result
            if (res.optString("documentation") != null && res.optString("documentation").length() > 2) {
                resolveDocumentation(new JSONObject(res.optString("documentation")));
            }

            JSONArray children = res.optJSONArray("children");

            if (children != null) {
                for (int i = 0; i < children.length(); i++) {
                    addSubResult(new WideQueryResponse(children.getString(i), level + 1));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void resolveDocumentation(JSONObject documentation) throws JSONException {

        WideSourceRegistry sourceRegistry = WideSourceRegistry.getInstance();

        for (String source : sourceRegistry.getSources()) {
            if (documentation.optString(source) != null && documentation.optString(source).length() > 2) {
                JSONObject sourceContent = new JSONObject(documentation.optString(source));
                setDocumentation(source, sourceRegistry.instantiateResult(source, sourceContent));
            }
        }
    }

    public void showDocumentation(WideResizablePaneBox paneBox) {
        for (String source : sources.keySet()) {
            if (sources.get(source) != null) {
                sources.get(source).showContent(paneBox);
            }
        }
    }

    public double calculateCompatibility() {
        double minCompatibility = 1.0;
        for (String source : sources.keySet()) {
            if (sources.get(source) != null) {
                minCompatibility = Math.min(sources.get(source).calculateCompatibility(), minCompatibility);
            }
        }

        return minCompatibility;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }


    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public AbstractWideSourceResult getDocumentation(String source) {
        return sources.get(source);
    }

    protected void setDocumentation(String source, AbstractWideSourceResult sourceContent) {
        sources.put(source, sourceContent);
    }

    public List<WideQueryResponse> getSubResults() {
        return subResults;
    }

    public void addSubResult(WideQueryResponse result) {
        result.setLevel(getLevel() + 1);
        subResults.add(result);
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
