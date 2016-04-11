package ch.ethz.inf.globis.wide.parsing;

import org.codehaus.jettison.json.JSONObject;

/**
 * Created by fabian on 04.04.16.
 */
public class WideMDNResult {
    private String summary;
    private String examples;
    private String compatibility;
    private String notes;
    private String seeAlso;
    private String attributes;
    private String syntax;

    public WideMDNResult(JSONObject res) {
        setSummary(res.optString("summary"));
        setExamples(res.optString("examples"));
        setCompatibility(res.optString("compatibility"));
        setNotes(res.optString("notes"));
        setSeeAlso(res.optString("seeAlso"));
        setAttributes(res.optString("attributes"));
        setSyntax(res.optString("syntax"));
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
}
