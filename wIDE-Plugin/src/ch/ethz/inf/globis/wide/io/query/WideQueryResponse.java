package ch.ethz.inf.globis.wide.io.query;

import ch.ethz.inf.globis.wide.io.query.mdn.WideMDNResult;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fabian on 11.03.16.
 */
public class WideQueryResponse {
    private String lang;
    private String type;
    private String key;
    private String value;
    private int level;
    private String caniuse;
    private WideMDNResult mdn;
    private List<WideQueryResponse> subResults = new ArrayList<WideQueryResponse>();

    public WideQueryResponse(String response) {
        this(response, 0);
    }

    public void setSubResults(List<WideQueryResponse> subResults) {
        this.subResults = subResults;
    }

    public WideQueryResponse(String response, int level) {
        try {
            JSONObject res = new JSONObject(response);
            setLang(res.optString("lang"));
            setType(res.optString("type"));
            setKey(res.optString("key"));
            setValue(res.optString("value"));
            setCaniuse(res.optString("caniuse"));
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
        // create mdn result
        if (documentation.optString("mdn") != null && documentation.optString("mdn").length() > 2) {
            setMdn(new WideMDNResult(new JSONObject(documentation.optString("mdn"))));
        }
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

    public List<WideQueryResponse> getSubResults() {
        return subResults;
    }

    public void addSubResult(WideQueryResponse result) {
        result.setLevel(getLevel() + 1);
        subResults.add(result);
    }

    public void addAllSubResults(List<WideQueryResponse> results) {
        for (WideQueryResponse res : results) {
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
        result[2] = StringUtils.repeat("    ", this.getLevel());
        return result;
    }
}
