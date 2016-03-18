package ch.ethz.inf.globis.wide.parsing;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fabian on 11.03.16.
 */
public class WideQueryResult {
    private String response;
    private String lookupName;
    private String fileName;
    private String lookupType;
    private int level;
    private List<WideQueryResult> subResults = new ArrayList<WideQueryResult>();

    public WideQueryResult(String response) {
        this(response, 0);
    }

    public WideQueryResult(String response, int level) {
        this.response = response;
        this.level = level;
    }

    public String getResponse() {
        return response;
    }

//    public void setResponse(String response) {
//        this.response = response;
//    }

    public String getLookupName() {
        return lookupName;
    }

    public void setLookupName(String lookupName) {
        this.lookupName = lookupName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getLookupType() {
        return lookupType;
    }

    public void setLookupType(String lookupType) {
        this.lookupType = lookupType;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
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
}
