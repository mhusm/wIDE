package ch.ethz.inf.globis.wide.lookup.io;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by fabian on 28.04.16.
 */
public class WideQueryRequest {
    private String lang;
    private String type;
    private String key;
    private String value;
    private List<WideQueryRequest> children = new ArrayList();

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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public List<WideQueryRequest> getChildren() {
        return children;
    }

    public void setChildren(List<WideQueryRequest> children) {
        this.children = children;
    }

    public void addChild(WideQueryRequest child) {
        this.children.add(child);
    }

    public String toString() {
        try {
            JSONObject obj = new JSONObject();
            obj.putOpt("lang", getLang());
            obj.putOpt("type", getType());
            obj.putOpt("key", getKey());
            obj.putOpt("value", getValue());

            // add children requests in string representation
            obj.putOpt("children", new JSONArray());
            for (WideQueryRequest childRequest : children) {
                obj.optJSONArray("children").put(childRequest.toString());
            }

            return obj.toString();

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
