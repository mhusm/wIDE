package ch.ethz.inf.globis.wide.ui.components.dialog;

import ch.ethz.inf.globis.wide.communication.WideHttpCommunicator;
import ch.ethz.inf.globis.wide.io.compatibility.WideBrowserVersionResponse;
import ch.ethz.inf.globis.wide.io.preferences.WideCompatibilityPreferences;
import ch.ethz.inf.globis.wide.logging.WideLogger;
import com.intellij.openapi.progress.AsynchronousExecution;
import com.intellij.openapi.ui.DialogWrapper;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.JFXPanel;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.StringConverter;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.controlsfx.control.RangeSlider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.NavigableSet;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Created by fabian on 23.05.16.
 */
public class WideCompatibilityPreferenceDialog extends DialogWrapper {

    private final static WideLogger LOGGER = new WideLogger(WideCompatibilityPreferenceDialog.class.getName());

    private final WideCompatibilityPreferences prefs = new WideCompatibilityPreferences();
    private WideBrowserVersionResponse response;

    public WideCompatibilityPreferenceDialog(@NotNull Component parent, boolean canBeParent) {
        super(parent, canBeParent);
        response = WideHttpCommunicator.sendBrowserVersionRequest();
        init();
        setSize(1000, 500);
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JFXPanel panel = new JFXPanel();
        panel.setMinimumSize(new Dimension(650, 300));

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                createCenterPanelFx(panel);
            }
        });

        return panel;
    }

    @Override
    protected void doOKAction() {
        prefs.store();
        super.doOKAction();
        LOGGER.info("Stored Browser Compatibility Preferences.");
    }

    @AsynchronousExecution
    private void createCenterPanelFx(JFXPanel panel) {

        TabPane pane = new TabPane();

        try {
            createBrowserTabFx(pane);
            createBrowserYearTabFx(pane);
            createUserTabFx(pane);

        } catch (ClassCastException e) {
            // Some preferences have been stored wrong -> rebuild preferences to default and restart
            LOGGER.warning("Browser Compatibility Preferences have been invalid. Reset them.");
            e.printStackTrace();

            prefs.buildDefaultPreferences();
            createCenterPanelFx(panel);

        } catch (JSONException e) {
            // Some preferences have been stored wrong -> rebuild preferences to default and restart
            LOGGER.warning("Browser Compatibility Preferences have been invalid. Reset them.");
            e.printStackTrace();

            prefs.buildDefaultPreferences();
            createCenterPanelFx(panel);
        }

        Scene scene = new Scene(pane);
        panel.setScene(scene);
    }

    /**
     * Loads Browser Version Compatibility preferences and displays a menu to adjust them.
     *
     * @param pane a TabPane in which the browser version compatibility dialog will be put
     * @throws ClassCastException
     */
    private void createBrowserTabFx(TabPane pane) throws ClassCastException, JSONException {
        VBox vBox = new VBox(5);
        vBox.setMinHeight(300);
        vBox.setPadding(new Insets(10, 10, 10, 10));

        Text text = new Text("Browser Version Compatibility");
        text.setStyle("-fx-font-size: 16px");
        vBox.getChildren().add(text);

        HBox box = new HBox(5);
        for (WideCompatibilityPreferences.browserTypes browser : WideCompatibilityPreferences.browserTypes.values()) {
            VBox browserVersionBox = new VBox(5);
            browserVersionBox.setPadding(new Insets(10, 10, 10, 10));

            Text browserTitle = new Text(browser.name());

            // load min version value from response and update in preferences
            double min = 1.0 * response.getBrowsers().get(browser.name().toLowerCase()).getMinVersion(); //0; //(double) prefs.getData("min", WideCompatibilityPreferences.prefTypes.BROWSER_VERSION.name(), browser.name());
            prefs.putData(min, "min", WideCompatibilityPreferences.prefTypes.BROWSER_VERSION.name(), browser.name().toLowerCase());

            // load max version value from response and update in preferences
            double max = 1.0 * response.getBrowsers().get(browser.name().toLowerCase()).getMaxVersion(); //(double) prefs.getData("max", WideCompatibilityPreferences.prefTypes.BROWSER_VERSION.name(), browser.name());
            prefs.putData(max, "max", WideCompatibilityPreferences.prefTypes.BROWSER_VERSION.name(), browser.name().toLowerCase());

            // load current stable version value from response and update in preferences
            double current = 1.0 * response.getBrowsers().get(browser.name().toLowerCase()).getCurrentVersion();
            prefs.putData(current, "current", WideCompatibilityPreferences.prefTypes.BROWSER_VERSION.name(), browser.name().toLowerCase());

            // workaround: if start or end are integer, the conversion to double needs to be made exlicitely
            Object start = prefs.getData("start", WideCompatibilityPreferences.prefTypes.BROWSER_VERSION.name(), browser.name().toLowerCase());
            if (start instanceof Integer) {
                start = 1.0 * (Integer) start;
            } else if (start == null) {
                start = min;
                prefs.putData(start, "start", WideCompatibilityPreferences.prefTypes.BROWSER_VERSION.name(), browser.name().toLowerCase());

            }

            Number end = (Number) prefs.getData("end", WideCompatibilityPreferences.prefTypes.BROWSER_VERSION.name(), browser.name().toLowerCase());
            if ((boolean) prefs.getData("isPreviewIncluded", WideCompatibilityPreferences.prefTypes.BROWSER_VERSION.name(), browser.name().toLowerCase())) {
                // developer is also interested in preview Versions
                end = max;
            } else if ((boolean) prefs.getData("isUntilLatest", WideCompatibilityPreferences.prefTypes.BROWSER_VERSION.name(), browser.name().toLowerCase())) {
                // developer is interested until latest stable version
                end = current;
            } else if (end instanceof Integer) {
                end = 1.0 * (Integer) end;
            } else if (end == null) {
                end = max;
                prefs.putData(end, "end", WideCompatibilityPreferences.prefTypes.BROWSER_VERSION.name(), browser.name().toLowerCase());
            }

            // get versions of current browser
            NavigableSet<Double> versions = response.getBrowsers().get(browser.name().toLowerCase()).getVersions();
            JSONObject jsonVersions = new JSONObject();
            for (Double version : versions) {
                jsonVersions.put(version.toString(), response.getBrowsers().get(browser.name().toLowerCase()).getCumulativePercentageUntilVersion(version));
                prefs.putData(jsonVersions, "versions", WideCompatibilityPreferences.prefTypes.BROWSER_VERSION.name(), browser.name().toLowerCase());
            }

            RangeSlider browserVersionSlider = new RangeSlider(min, max, (Double) start, (Double) end);

            // Adjust range slider appearance
            browserVersionSlider.setOrientation(Orientation.VERTICAL);
            browserVersionSlider.setShowTickMarks(true);
            browserVersionSlider.setShowTickLabels(true);
            browserVersionSlider.setMinorTickCount((int) Math.floor(max-min));
            browserVersionSlider.setMajorTickUnit((max-min)/versions.size());
            browserVersionSlider.setSnapToTicks(true);


            // Set reasonable labels
            browserVersionSlider.setLabelFormatter(new StringConverter<Number>() {
                @Override
                public String toString(Number n) {
                    String label = String.valueOf(versions.floor((Double) n));
                    if (label == "null" ) {
                        label = String.valueOf(versions.ceiling((Double) n));
                    }

                    return label;
                }

                @Override
                public Number fromString(String s) {
                    return Integer.valueOf(s);
                }
            });

            // include previews?
            CheckBox untilLatest = new CheckBox("Previews");
            untilLatest.setSelected((boolean) prefs.getData("isPreviewIncluded", WideCompatibilityPreferences.prefTypes.BROWSER_VERSION.name(), browser.name().toLowerCase()));
            untilLatest.selectedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    if (newValue) {
                        browserVersionSlider.highValueProperty().setValue(max);
                    } else {
                        browserVersionSlider.highValueProperty().setValue(response.getBrowsers().get(browser.name().toLowerCase()).getCurrentVersion());
                    }

                    // update preferences
                    prefs.putData(newValue, "isPreviewIncluded", WideCompatibilityPreferences.prefTypes.BROWSER_VERSION.name(), browser.name().toLowerCase());
                }
            });

            // include all until latest version?
            CheckBox latestStable = new CheckBox("Latest (" + response.getBrowsers().get(browser.name().toLowerCase()).getCurrentVersion() + ")");
            latestStable.setSelected((boolean) prefs.getData("isUntilLatest", WideCompatibilityPreferences.prefTypes.BROWSER_VERSION.name(), browser.name().toLowerCase()));
            latestStable.selectedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    if (newValue) {
                        browserVersionSlider.highValueProperty().setValue(response.getBrowsers().get(browser.name().toLowerCase()).getCurrentVersion());
                    }

                    // update preferences
                    prefs.putData(newValue, "isUntilLatest", WideCompatibilityPreferences.prefTypes.BROWSER_VERSION.name(), browser.name().toLowerCase());
                }
            });

            // Add listeners to changing values -> and adjust preferences
            browserVersionSlider.highValueProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                    prefs.putData(versions.ceiling((Double) newValue), "end", WideCompatibilityPreferences.prefTypes.BROWSER_VERSION.name(), browser.name().toLowerCase());

                    untilLatest.setSelected(newValue.equals(max));
                    latestStable.setSelected(newValue.equals(current));
                }
            });
            browserVersionSlider.lowValueProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                    prefs.putData(versions.floor((Double) newValue), "start", WideCompatibilityPreferences.prefTypes.BROWSER_VERSION.name(), browser.name().toLowerCase());
                }
            });

            browserVersionBox.getChildren().addAll(browserTitle, untilLatest, latestStable, browserVersionSlider);
            box.getChildren().add(browserVersionBox);
        }

        vBox.getChildren().add(box);

        Tab tab = new Tab("Browser Version", vBox);
        tab.setClosable(false);
        pane.getTabs().add(tab);
    }

    /**
     * Loads Browser Release Year Compatibility preferences and displays a menu to adjust them.
     *
     * @param pane a {@Code TabPane} in which the browser release year compatibility dialog will be put
     */
    private void createBrowserYearTabFx(TabPane pane) throws ClassCastException {
        VBox vBox = new VBox(5);
        vBox.setMinHeight(300);
        vBox.setPadding(new Insets(10, 10, 10, 10));

        Text text = new Text("Browser Release Year Compatibility");
        text.setStyle("-fx-font-size: 16px");
        vBox.getChildren().add(text);

        HBox box = new HBox(5);

        for (WideCompatibilityPreferences.browserTypes browser : WideCompatibilityPreferences.browserTypes.values()) {
            VBox browserYearBox = new VBox(5);
            browserYearBox.setPadding(new Insets(10, 10, 10, 10));

            Text browserTitle = new Text(browser.name());

            // load values from preferences
            double min = 0; //(double) prefs.getData("min", WideCompatibilityPreferences.prefTypes.BROWSER_YEAR.name(), browser.name());
            double max = 100; //(double) prefs.getData("max", WideCompatibilityPreferences.prefTypes.BROWSER_YEAR.name(), browser.name());
            Integer start = (Integer) prefs.getData("start", WideCompatibilityPreferences.prefTypes.BROWSER_YEAR.name(), browser.name().toLowerCase());
            Integer end = (Integer) prefs.getData("end", WideCompatibilityPreferences.prefTypes.BROWSER_YEAR.name(), browser.name().toLowerCase());
            RangeSlider browserYearSlider = new RangeSlider(2000, 2016, start, end);

            // bug, rangeslider does not set the low-value properly
            browserYearSlider.setLowValue(start);

            // Adjust range slider appearance
            browserYearSlider.setOrientation(Orientation.VERTICAL);
            browserYearSlider.setShowTickMarks(true);
            browserYearSlider.setShowTickLabels(true);
            browserYearSlider.setMinorTickCount(3);
            browserYearSlider.setMajorTickUnit(4);
            browserYearSlider.setSnapToTicks(true);

            // add custom StringConverter to allow correctly formatted years
            browserYearSlider.setLabelFormatter(new StringConverter<Number>() {
                @Override
                public String toString(Number n) {
                    return String.valueOf(Math.round((Double) n));
                }

                @Override
                public Number fromString(String s) {
                    return Integer.valueOf(s);
                }
            });

            // Add listeners to changing values -> and adjust preferences
            browserYearSlider.highValueProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                    prefs.putData(Math.round((Double) newValue), "end", WideCompatibilityPreferences.prefTypes.BROWSER_YEAR.name(), browser.name().toLowerCase());
                }
            });
            browserYearSlider.lowValueProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                    prefs.putData(Math.round((Double) newValue), "start", WideCompatibilityPreferences.prefTypes.BROWSER_YEAR.name(), browser.name().toLowerCase());
                }
            });

            browserYearBox.getChildren().addAll(browserTitle, browserYearSlider);
            box.getChildren().add(browserYearBox);
        }

        vBox.getChildren().add(box);

        Tab tab = new Tab("Browser Year", vBox);
        tab.setClosable(false);
        pane.getTabs().add(tab);
    }

    private void createUserTabFx(TabPane pane) {
        VBox vBox = new VBox(5);
        vBox.setMinHeight(300);
        vBox.setPadding(new Insets(10, 10, 10, 10));

        Text text = new Text("User Reach Compatibility");
        text.setStyle("-fx-font-size: 16px");
        vBox.getChildren().add(text);

        Slider userSlider = new Slider(0, 100, 90);
        userSlider.setShowTickMarks(true);
        userSlider.setShowTickLabels(true);
        userSlider.setPadding(new Insets(10, 0, 0, 0));

        vBox.getChildren().add(userSlider);


        Tab tab = new Tab("User Reach", vBox);
        tab.setClosable(false);
        pane.getTabs().add(tab);
    }
}
