package ch.ethz.inf.globis.wide.io.compatibility;

import ch.ethz.inf.globis.wide.logging.WideLogger;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.util.*;

/**
 * Created by fabian on 25.05.16.
 */
public class WideBrowserVersionResponse {

    private static final WideLogger LOGGER = new WideLogger(WideBrowserVersionResponse.class.getName());
    private Map<String, WideBrowser> browsers = new HashMap();

    public WideBrowserVersionResponse(String response) {
        try {
            JSONArray array = new JSONArray(response);

            // create an object for every browser
            for (int i = 0; i < array.length(); i++) {
                WideBrowser browser = new WideBrowser(array.getJSONObject(i));
                browsers.put(browser.getBrowserName().toLowerCase(), browser);
            }
        } catch (JSONException e) {
            LOGGER.warning("Invalid WideBrowserVersionResponse received.");
            e.printStackTrace();
        }
    }

    public Map<String, WideBrowser> getBrowsers() {
        return browsers;
    }

    public class WideBrowser {
        private TreeMap<Double, Double> versions = new TreeMap();
        private String browserName;
        private double browserUsage;
        private double currentVersion;

        public WideBrowser(JSONObject browser) throws JSONException {
            browserName = browser.getString("name");

            try {
                currentVersion = Double.parseDouble(browser.getString("current"));
            } catch(NumberFormatException e) {
                currentVersion = 0;
            }

            double totalUsage = 0;

            JSONArray vrs = browser.getJSONArray("versions");
            for (int i = 0; i < vrs.length(); i++) {
                JSONObject version = vrs.getJSONObject(i);

                try {
                    double versionNumber = Double.parseDouble(version.getString("version"));
                    double versionUsage = Double.parseDouble(version.getString("usage"));

                    totalUsage += versionUsage;

                    versions.put(versionNumber, totalUsage);

                } catch (NumberFormatException e) {
                    LOGGER.warning("Invalid compatibility browser version data received.");
                    e.printStackTrace();
                }
            }

            browserUsage = totalUsage;
        }

        public String getBrowserName() {
            return browserName;
        }

        public Double getMinVersion() {
            return versions.descendingKeySet().last();
        }

        public Double getMaxVersion() {
            return versions.descendingKeySet().first();
        }

        public NavigableSet<Double> getVersions() {
            return versions.navigableKeySet();
        }

        public double getCumulativePercentageUntilVersion(double version) {
            return versions.get(version);
        }

        public double getCurrentVersion() {
            return currentVersion;
        }

  }
}
