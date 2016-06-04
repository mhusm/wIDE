package ch.ethz.inf.globis.wide.sources.caniuse;

import ch.ethz.inf.globis.wide.io.preferences.WideCompatibilityPreferences;
import ch.ethz.inf.globis.wide.io.query.AbstractWideSourceResult;
import ch.ethz.inf.globis.wide.logging.WideLogger;
import ch.ethz.inf.globis.wide.ui.components.WideContentBuilder;
import ch.ethz.inf.globis.wide.ui.components.panel.WideResizablePane;
import ch.ethz.inf.globis.wide.ui.components.panel.WideResizablePaneBox;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.web.WebView;
import org.codehaus.jettison.json.JSONObject;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by fabian on 29.05.16.
 */
public class WideCaniuseResult extends AbstractWideSourceResult {

    private final static WideLogger LOGGER = new WideLogger(WideCaniuseResult.class.getName());
    private final WideCompatibilityPreferences prefs = new WideCompatibilityPreferences();

    private Map<String, WideFeatureSupport> support = new HashMap();

    public WideCaniuseResult(JSONObject res) {
        super(res);

        if (res != null) {
            for (WideCompatibilityPreferences.browserTypes browser : WideCompatibilityPreferences.browserTypes.values()) {
                JSONObject browserSupport = res.optJSONObject(browser.name().toLowerCase());

                if (browserSupport != null) {

                    // load values from preferences
                    Object min = prefs.getData("min", WideCompatibilityPreferences.prefTypes.BROWSER_VERSION.name(), browser.name().toLowerCase());
                    if (min instanceof Integer) {
                        min = 1.0 * (Integer) min;
                    }

                    Object max = prefs.getData("max", WideCompatibilityPreferences.prefTypes.BROWSER_VERSION.name(), browser.name().toLowerCase());
                    if (max instanceof Integer) {
                        max = 1.0 * (Integer) max;
                    }

                    // workaround: if start or end are integer, the conversion to double needs to be made exlicitely
                    Number start = (Number) prefs.getData("start", WideCompatibilityPreferences.prefTypes.BROWSER_VERSION.name(), browser.name().toLowerCase());
                    if (start instanceof Integer) {
                        start = 1.0 * (Integer) start;
                    } else if (start == null) {
                        start = (Double) min;
                    }
                    Number end = (Number) prefs.getData("end", WideCompatibilityPreferences.prefTypes.BROWSER_VERSION.name(), browser.name().toLowerCase());
                    if (end instanceof Integer) {
                        end = 1.0 * (Integer) end;
                    } else if (end == null) {
                        end = (Double) max;
                    }

                    support.put(browser.name(), new WideFeatureSupport(browserSupport, (double) min, (double) max, (double) start, (double) end));

                } else {
                    LOGGER.warning("No compatibility result for " + browser.name());
                }
            }
        } else {
            LOGGER.info("No compatibility information available.");
        }
    }

    public void showContent(WideResizablePaneBox paneBox) {
        WebView webView = new WebView();
        webView.getEngine().setUserStyleSheetLocation(WideContentBuilder.class.getResource("/stylesheets/CaniuseSheet.css").toString());
        webView.getEngine().loadContent(buildContent());
        WideResizablePane pane = new WideResizablePane(webView);
        paneBox.addPane(pane);
    }

    public String buildContent() {
        String content = "<html><body>";
        content += "<h1>Compatibility</h1>";
        Double supportedPercentage = 0.0;
        Double notSupportedPercentage = 0.0;
        Double partiallySupportedPercentage = 0.0;
        Double prefixedPercentage = 0.0;
        Double notHandledPercentage = 0.0;
        Double totalUsage = 0.0;

        for (String browserName : support.keySet()) {

            content += "<div class=\"browser_tile\">";
            content += "<h2>" + browserName + "</h2>";
            content += "<div class=\"compatibility_container\">";

            WideFeatureSupport featureSupport = support.get(browserName);

            double totalHeight = 100;
            double factor = 1 / (featureSupport.getMaxVersion() - featureSupport.getMinVersion());

            totalUsage += getUsagePercentage(browserName, featureSupport.getMinVersion(), featureSupport.getMaxVersion());

            Double minHandledVersion = Double.min(featureSupport.getEndVersion(), featureSupport.getMaxVersion());
            Double maxHandledVersion = Double.max(featureSupport.getStartVersion(), featureSupport.getMinVersion());

            content += "<div class=\"version\">" + featureSupport.getMaxVersion() + "</div>";

            // too new versions
            if (featureSupport.getMaxVersion() > featureSupport.getEndVersion()) {
                double height = totalHeight * factor * (featureSupport.getMaxVersion() - featureSupport.getEndVersion());

                Double usagePercentage = getUsagePercentage(browserName, featureSupport.getEndVersion(), featureSupport.getMaxVersion());
                notHandledPercentage += usagePercentage;

                content += "<div class=\"whatever\" style=\"height: " + height + "\" title=\"Usage: " + formatPercentageForOutput(usagePercentage) + "\"></div>";
                content += "<div class=\"version\">" + featureSupport.getEndVersion() + "</div>";

                minHandledVersion = featureSupport.getEndVersion();
            }

            // supported range
            if (minHandledVersion > featureSupport.getSupportedSince()) {
                double height = totalHeight * (factor * (minHandledVersion - Double.max(featureSupport.getSupportedSince(), maxHandledVersion)));

                Double usagePercentage = getUsagePercentage(browserName, Double.max(featureSupport.getSupportedSince(), maxHandledVersion), minHandledVersion);
                supportedPercentage += usagePercentage;

                content += "<div class=\"supported\" style=\"height: " + height + "\" title=\"Usage: " + formatPercentageForOutput(usagePercentage) + "\"></div>";
                content += "<div class=\"version\">" + Double.max(featureSupport.getSupportedSince(), maxHandledVersion) + "</div>";

                minHandledVersion = featureSupport.getSupportedSince();
            }

            // prefixed range
            if (minHandledVersion > featureSupport.getPrefixedSince()) {
                double height = totalHeight * (factor * (minHandledVersion - Double.max(featureSupport.getPrefixedSince(), maxHandledVersion)));

                Double usagePercentage = getUsagePercentage(browserName, Double.max(featureSupport.getPrefixedSince(), maxHandledVersion), minHandledVersion);
                prefixedPercentage += usagePercentage;

                content += "<div class=\"prefixed\" style=\"height: " + height + "\" title=\"Usage: " + formatPercentageForOutput(usagePercentage) + "\"></div>";
                content += "<div class=\"version\">" + Double.max(featureSupport.getPrefixedSince(), maxHandledVersion) + "</div>";

                minHandledVersion = featureSupport.getPrefixedSince();
            }

            // partially supported range
            if (minHandledVersion > featureSupport.getPartiallySupportedSince()) {
                double height = totalHeight * (factor * (minHandledVersion - Double.max(featureSupport.getPartiallySupportedSince(), maxHandledVersion)));

                Double usagePercentage = getUsagePercentage(browserName, Double.max(featureSupport.getPartiallySupportedSince(), maxHandledVersion), minHandledVersion);
                partiallySupportedPercentage += usagePercentage;

                content += "<div class=\"partially_supported\" style=\"height: " + height + "\" title=\"Usage: " + formatPercentageForOutput(usagePercentage) + "\"></div>";
                content += "<div class=\"version\">" + Double.max(featureSupport.getPartiallySupportedSince(), maxHandledVersion) + "</div>";

                minHandledVersion = featureSupport.getPartiallySupportedSince();
            }

            // not supported range
            if (Double.max(featureSupport.getNotSupportedUntil(), minHandledVersion) > maxHandledVersion) {
                double height = totalHeight * factor * (Double.max(featureSupport.getNotSupportedUntil(), minHandledVersion) - maxHandledVersion);

                Double usagePercentage = getUsagePercentage(browserName, maxHandledVersion, Double.max(featureSupport.getNotSupportedUntil(), minHandledVersion));
                notSupportedPercentage += usagePercentage;

                content += "<div class=\"not_supported\" style=\"height: " + height + "\" title=\"Usage: " + formatPercentageForOutput(usagePercentage) + "\"></div>";
                content += "<div class=\"version\">" + featureSupport.getStartVersion() + "</div>";
            }

            // too old versions
            if (featureSupport.getStartVersion() > featureSupport.getMinVersion()) {
                double height = totalHeight * (factor * (featureSupport.getStartVersion() - featureSupport.getMinVersion()));

                Double usagePercentage = getUsagePercentage(browserName, featureSupport.getMinVersion(), featureSupport.getStartVersion());
                notHandledPercentage += usagePercentage;

                content += "<div class=\"whatever\" style=\"height: " + height + "\" title=\"Usage: " + formatPercentageForOutput(usagePercentage) + "\"></div>";
            }

            content += "<div class=\"version\">" + featureSupport.getMinVersion() + "</div>";
            content += "</div></div>";
        }

        content += "<br />";
        content += "<h3>Supported Users</h3>";
        content += "<div class=\"usage_container\">";
        if (supportedPercentage > 0.0) {
            content += "<div class=\"usage_supported\" style=\"width: " + (supportedPercentage / totalUsage * 100) + "%\"><div class=\"usage_tag\">" + formatPercentageForOutput(supportedPercentage / totalUsage) + "</div></div>";
        }

        if (partiallySupportedPercentage > 0.0) {
            content += "<div class=\"usage_partially_supported\" style=\"width: " + (partiallySupportedPercentage / totalUsage * 100) + "%\"><div class=\"usage_tag\">" + formatPercentageForOutput(partiallySupportedPercentage / totalUsage) + "</div></div>";
        }

        if (prefixedPercentage > 0.0) {
            content += "<div class=\"usage_prefixed\" style=\"width: " + (prefixedPercentage / totalUsage * 100) + "%\"><div class=\"usage_tag\">" + formatPercentageForOutput(prefixedPercentage / totalUsage) + "</div></div>";
        }

        if (notSupportedPercentage > 0.0) {
            content += "<div class=\"usage_not_supported\" style=\"width: " + (notSupportedPercentage / totalUsage * 100) + "%\"><div class=\"usage_tag\">" + formatPercentageForOutput(notSupportedPercentage / totalUsage) + "</div></div>";
        }

        if (notHandledPercentage > 0.0) {
            content += "<div class=\"usage_whatever\" style=\"width: " + (notHandledPercentage / totalUsage * 100) + "%\"><div class=\"usage_tag\">" + formatPercentageForOutput(notHandledPercentage / totalUsage) + "</div></div>";
        }
        content += "</div>";

        content += "</body></html>";
        return content;
    }

    private Double getUsagePercentage(String browserName, Double lowerBound, Double upperBound) {
        JSONObject versions = (JSONObject) prefs.getData("versions", WideCompatibilityPreferences.prefTypes.BROWSER_VERSION.name(), browserName.toLowerCase());
        Double usageUpperBound = versions.optDouble(upperBound.toString());
        Double usageLowerBound = versions.optDouble(lowerBound.toString());
        Double usage = usageUpperBound - usageLowerBound;

        return usage * 0.01;
    }

    private String formatPercentageForOutput(Double percentage) {
        return new DecimalFormat("#.##%").format(percentage);
    }

    public class WideFeatureSupport {
        private double supportedSince;
        private double notSupportedUntil;
        private double partiallySupportedSince;
        private double prefixedSince;
        private double minVersion;
        private double maxVersion;
        private double startVersion;
        private double endVersion;

        public WideFeatureSupport(JSONObject support, double minVersion, double maxVersion, double startVersion, double endVersion) {
            this.supportedSince = support.optDouble("y");
            this.notSupportedUntil = support.optDouble("n");;
            this.partiallySupportedSince = support.optDouble("a");;
            this.prefixedSince = support.optDouble("x");
            this.minVersion = minVersion;
            this.maxVersion = maxVersion;
            this.startVersion = startVersion;
            this.endVersion = endVersion;
        }

        public double getMaxVersion() {
            return maxVersion;
        }

        public void setMaxVersion(double maxVersion) {
            this.maxVersion = maxVersion;
        }

        public double getMinVersion() {
            return minVersion;
        }

        public void setMinVersion(double minVersion) {
            this.minVersion = minVersion;
        }

        public double getPartiallySupportedSince() {
            return partiallySupportedSince;
        }

        public void setPartiallySupportedSince(double partiallySupportedSince) {
            this.partiallySupportedSince = partiallySupportedSince;
        }

        public double getNotSupportedUntil() {
            return notSupportedUntil;
        }

        public void setNotSupportedUntil(double notSupportedUntil) {
            this.notSupportedUntil = notSupportedUntil;
        }

        public double getSupportedSince() {
            return supportedSince;
        }

        public void setSupportedSince(double supportedSince) {
            this.supportedSince = supportedSince;
        }

        public double getPrefixedSince() {
            return prefixedSince;
        }

        public void setPrefixedSince(double prefixedSince) {
            this.prefixedSince = prefixedSince;
        }

        public double getStartVersion() {
            return startVersion;
        }

        public void setStartVersion(double startVersion) {
            this.startVersion = startVersion;
        }

        public double getEndVersion() {
            return endVersion;
        }

        public void setEndVersion(double endVersion) {
            this.endVersion = endVersion;
        }
    }
}