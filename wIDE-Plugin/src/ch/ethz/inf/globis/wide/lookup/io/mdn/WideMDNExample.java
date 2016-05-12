package ch.ethz.inf.globis.wide.lookup.io.mdn;

import org.codehaus.jettison.json.JSONObject;

/**
 * Created by fabian on 13.04.16.
 */
public class WideMDNExample {
    private String title;
    private String code;
    private String result;

    public WideMDNExample(JSONObject res) {
        setTitle(res.optString("title"));
        setCode(res.optString("code"));
        setResult(res.optString("text"));
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getCode() {

        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTitle() {

        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
