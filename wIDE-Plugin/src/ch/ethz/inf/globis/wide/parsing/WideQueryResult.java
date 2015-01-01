package ch.ethz.inf.globis.wide.parsing;

/**
 * Created by fabian on 11.03.16.
 */
public class WideQueryResult {
    private String response;
    private String lookupName;
    private String fileName;

    public WideQueryResult(String response) {
        this.response = response;
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
}
