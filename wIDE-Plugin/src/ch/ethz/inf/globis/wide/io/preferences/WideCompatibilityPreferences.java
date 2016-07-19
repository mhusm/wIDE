package ch.ethz.inf.globis.wide.io.preferences;

import ch.ethz.inf.globis.wide.logging.WideLogger;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.util.prefs.Preferences;

/**
 * Created by fabian on 24.05.16.
 */
public class WideCompatibilityPreferences {

    private final static WideLogger LOGGER = new WideLogger(WideCompatibilityPreferences.class.getName());
    public static final String PREF_NAME = "wide_compatibility_preferences";

    public enum prefTypes {
        BROWSER_VERSION,
        BROWSER_YEAR,
        USER_REACH
    }

    public enum browserTypes {
        Firefox,
        Chrome,
        IE,
        Safari,
        Opera
    }

    private JSONObject prefs;

    public WideCompatibilityPreferences() {
        // Retrieve the user preference node
        Preferences preferences = Preferences.userNodeForPackage(WideCompatibilityPreferences.class);
        String prefString = preferences.get(PREF_NAME, null);

//        buildDefaultPreferences();
        if (prefString != null) {
            buildPreferenceFromString(prefString);
        } else {
            buildDefaultPreferences();
        }
    }

    public void buildPreferenceFromString(String prefString) {
        try {
            prefs = new JSONObject(prefString);
        } catch (JSONException e) {
            LOGGER.warning("Invalid compatibility data loaded.");
            e.printStackTrace();
            buildDefaultPreferences();
        }

    }

    public void buildDefaultPreferences() {
        try {
            prefs = new JSONObject();
            prefs.put(prefTypes.BROWSER_VERSION.name(), buildBrowserVersionPreferences());
            prefs.put(prefTypes.BROWSER_YEAR.name(), buildBrowserYearPreferences());
            prefs.put(prefTypes.USER_REACH.name(), buildUserReachPreferences());

        } catch (JSONException e) {
            LOGGER.warning("Error whild building compatibility storage object.");
            e.printStackTrace();
        }
    }

    private JSONObject buildBrowserVersionPreferences() throws JSONException {
        JSONObject browserVersionPrefs = new JSONObject();

        // Firefox
        JSONObject fireFoxVersion = new JSONObject();
        fireFoxVersion.put("start", 1);
        fireFoxVersion.put("end", 100);
        fireFoxVersion.put("current", 100);
        fireFoxVersion.put("isPreviewIncluded", false);
        fireFoxVersion.put("isUntilLatest", true);
        browserVersionPrefs.put(browserTypes.Firefox.name().toLowerCase(), fireFoxVersion);

        // Chrome
        JSONObject chromeVersion = new JSONObject();
        chromeVersion.put("start", 1);
        chromeVersion.put("end", 100);
        chromeVersion.put("current", 100);
        chromeVersion.put("isPreviewIncluded", false);
        chromeVersion.put("isUntilLatest", true);
        browserVersionPrefs.put(browserTypes.Chrome.name().toLowerCase(), chromeVersion);

        // IE
        JSONObject ieVersion = new JSONObject();
        ieVersion.put("start", 1);
        ieVersion.put("end", 100);
        ieVersion.put("current", 100);
        ieVersion.put("isPreviewIncluded", false);
        ieVersion.put("isUntilLatest", true);
        browserVersionPrefs.put(browserTypes.IE.name().toLowerCase(), ieVersion);

        // Safari
        JSONObject safariVersion = new JSONObject();
        safariVersion.put("start", 1);
        safariVersion.put("end", 100);
        safariVersion.put("current", 100);
        safariVersion.put("isPreviewIncluded", false);
        safariVersion.put("isUntilLatest", true);
        browserVersionPrefs.put(browserTypes.Safari.name().toLowerCase(), safariVersion);

        // Opera
        JSONObject operaVersion = new JSONObject();
        operaVersion.put("start", 1);
        operaVersion.put("end", 100);
        operaVersion.put("current", 100);
        operaVersion.put("isPreviewIncluded", false);
        operaVersion.put("isUntilLatest", true);
        browserVersionPrefs.put(browserTypes.Opera.name().toLowerCase(), operaVersion);

        return browserVersionPrefs;
    }

    private JSONObject buildBrowserYearPreferences() throws JSONException {
        JSONObject browserYearPrefs = new JSONObject();

        // Firefox
        JSONObject fireFoxYear = new JSONObject();
        fireFoxYear.put("start", 2008);
        fireFoxYear.put("end", 2016);
        browserYearPrefs.put(browserTypes.Firefox.name().toLowerCase(), fireFoxYear);

        // Chrome
        JSONObject chromeYear = new JSONObject();
        chromeYear.put("start", 2008);
        chromeYear.put("end", 2016);
        browserYearPrefs.put(browserTypes.Chrome.name().toLowerCase(), chromeYear);

        // IE
        JSONObject ieYear = new JSONObject();
        ieYear.put("start", 2008);
        ieYear.put("end", 2016);
        browserYearPrefs.put(browserTypes.IE.name().toLowerCase(), ieYear);

        // Safari
        JSONObject safariYear = new JSONObject();
        safariYear.put("start", 2008);
        safariYear.put("end", 2016);
        browserYearPrefs.put(browserTypes.Safari.name().toLowerCase(), safariYear);

        // Opera
        JSONObject operaYear = new JSONObject();
        operaYear.put("start", 2008);
        operaYear.put("end", 2016);
        browserYearPrefs.put(browserTypes.Opera.name().toLowerCase(), operaYear);

        return browserYearPrefs;
    }

    private JSONObject buildUserReachPreferences() throws JSONException {
        JSONObject userReachPrefs = new JSONObject();
        userReachPrefs.put("percentage", 80);
        return userReachPrefs;
    }

    public void putData(Object data, String key, String... path) {
        try {
            JSONObject currentObject = prefs;

            for (String node : path) {
                currentObject = currentObject.getJSONObject(node);
            }

            currentObject.put(key, data);

        } catch (JSONException e) {
            String pathString = "";
            for (String p : path) {
                pathString += p + ">";
            }
            LOGGER.warning("Illegal path to compatibility preference node: [path] " + pathString + key + " [data] " + data.toString() + " [type] store");
        }
    }

    public Object getData(String key, String... path) {
        try {
            JSONObject currentObject = prefs;

            for (String node : path) {
                currentObject = currentObject.getJSONObject(node);
            }

            return currentObject.get(key);

        } catch (JSONException e) {
            String pathString = "";
            for (String p : path) {
                pathString += p + ">";
            }
            LOGGER.warning("Illegal path to compatibility preference node: [path] " + pathString + key + " [type] load");
            return null;
        }
    }

    public void store() {
        // Retrieve the user preference node for the package com.mycompany
        Preferences preferences = Preferences.userNodeForPackage(WideCompatibilityPreferences.class);
        preferences.put(PREF_NAME, prefs.toString());
    }
}
