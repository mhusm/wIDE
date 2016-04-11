package ch.ethz.inf.globis.wide.parsing;

import com.intellij.icons.AllIcons;
import com.intellij.json.JsonParser;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fabian on 11.03.16.
 */
public class WideQueryResult {
    private String lang;
    private String type;
    private String key;
    private String value;
    private int level;
    private String caniuse;
    private WideMDNResult mdn;
    private List<WideQueryResult> subResults = new ArrayList<WideQueryResult>();
    private String info;

    public WideQueryResult(String response) {
        this(response, 0);
    }

    public void setSubResults(List<WideQueryResult> subResults) {
        this.subResults = subResults;
    }

    public WideQueryResult(String response, int level) {
        try {

            JSONObject res = new JSONObject(response);
            setLang(res.optString("lang"));
            setType(res.optString("type"));
            setKey(res.optString("key"));
            setValue(res.optString("value"));
            setCaniuse(res.optString("caniuse"));
            setLevel(level);
            setInfo(res.optString("info"));

            // create mdn result
            if (res.optString("mdn") != null && res.optString("mdn").length() > 2) {
                setMdn(new WideMDNResult(new JSONObject(res.optString("mdn"))));
            }

            JSONArray children = res.optJSONArray("children");

            if (children != null) {
                for (int i = 0; i < children.length(); i++) {
                    subResults.add(new WideQueryResult(children.getString(i), level + 1));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
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

    public String getCaniuse() {
        return caniuse;
    }

    public void setCaniuse(String caniuse) {
        this.caniuse = caniuse;
    }

    public WideMDNResult getMdn() {
        return mdn;
    }

    public void setMdn(WideMDNResult mdn) {
        this.mdn = mdn;
    }

    public List<WideQueryResult> getSubResults() {
        return subResults;
    }

    public void addSubResult(WideQueryResult result) {
        result.setLevel(getLevel() + 1);
        subResults.add(result);
    }

    public void addAllSubResults(List<WideQueryResult> results) {
        for (WideQueryResult res : results) {
            addSubResult(res);
        }
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Object[] getTableRow() {
        Object[] result = new Object[3];

        if ("JS".equals(getLang())) {
            if ("call".equals(getType())) {
                result[0] = StringUtils.repeat("    ", this.getLevel()) + this.getKey();
            } else if ("callCandidate".equals(getType())) {
                result[0] = StringUtils.repeat("    ", this.getLevel()) + "@ " + this.getValue();
            }
        }
        else if (getValue() != null && getValue() != "") {
            result[0] = StringUtils.repeat("    ", this.getLevel()) + this.getKey() + " [= " + this.getValue() + "]";
        } else {
            result[0] = StringUtils.repeat("    ", this.getLevel()) + this.getKey();
        }
        result[1] = StringUtils.repeat("    ", this.getLevel()) + this.getLang() + "-" + this.getType();
        result[2] = StringUtils.repeat("    ", this.getLevel()) + this.getInfo();
        return result;
    }
}
