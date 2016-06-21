package ch.ethz.inf.globis.wide.communication;

import ch.ethz.inf.globis.wide.io.compatibility.WideBrowserVersionResponse;
import ch.ethz.inf.globis.wide.logging.WideLogger;
import ch.ethz.inf.globis.wide.io.query.WideQueryRequest;
import ch.ethz.inf.globis.wide.io.query.WideQueryResponse;
import ch.ethz.inf.globis.wide.ui.components.window.WideDefaultWindowFactory;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by fabian on 16.03.16.
 */
public class WideHttpCommunicator {

    private static final WideLogger LOGGER = new WideLogger(WideHttpCommunicator.class.getName());

    private static final String SERVER_HOST = "localhost";
    private static final String SERVER_PORT = "3000";
    private static final String SERVER_PROTOCOL = "http://";

    public static WideQueryResponse sendRequest(WideQueryRequest request) {
        if (request != null) {
            synchronized (LOGGER) {
                try {
                    URL url = new URL(SERVER_PROTOCOL + SERVER_HOST + ":" + SERVER_PORT + "/query");
                    LOGGER.info("SEND REQUEST: " + request);
                    return new WideQueryResponse(sendQuery(url, request.toString()));
                } catch (MalformedURLException e) {
                    LOGGER.severe("Sending request to " + SERVER_PROTOCOL + SERVER_HOST + ":" + SERVER_PORT + "/query failed.");
                    e.printStackTrace();
                    return null;
                }
            }
        }

        return null;
    }

    public static WideQueryResponse sendCompatibilityRequest(WideQueryRequest request) {
        synchronized (LOGGER) {
            try {
                URL url = new URL(SERVER_PROTOCOL + SERVER_HOST + ":" + SERVER_PORT + "/compatibility/support");
                LOGGER.info("SEND REQUEST: " + request);
                return new WideQueryResponse(sendQuery(url, request.toString()));
            } catch (MalformedURLException e) {
                LOGGER.severe("Sending request to " + SERVER_PROTOCOL + SERVER_HOST + ":" + SERVER_PORT + "/compatibility/support failed.");
                e.printStackTrace();
                return null;
            }
        }
    }

    public static WideBrowserVersionResponse sendBrowserVersionRequest() {
        synchronized (LOGGER) {
            try {
                URL url = new URL(SERVER_PROTOCOL + SERVER_HOST + ":" + SERVER_PORT + "/compatibility/browser_versions");
                LOGGER.info("SEND REQUEST: Get Compatibility Browser Versions.");
                return new WideBrowserVersionResponse(sendQuery(url, ""));
            } catch (MalformedURLException e) {
                LOGGER.severe("Sending request to " + SERVER_PROTOCOL + SERVER_HOST + ":" + SERVER_PORT + "/compatibility failed.");
                e.printStackTrace();
                return null;
            }
        }
    }

    @Nullable
    private static String sendQuery(URL url, String parameters) {
        HttpURLConnection connection = null;
        try {
            //Create connection
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");

            connection.setRequestProperty("Content-Length",
                    Integer.toString(("parameters= " + parameters).getBytes().length));
            connection.setRequestProperty("Content-Language", "en-US");

            connection.setUseCaches(false);
            connection.setDoOutput(true);

            //Send request
            DataOutputStream wr = new DataOutputStream(
                    connection.getOutputStream());
            wr.writeBytes("parameters=" + parameters);
            wr.close();

            //Get Response
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            StringBuilder response = new StringBuilder(); // or StringBuffer if not Java 5+
            String line;
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();

            LOGGER.info("RESPONSE RECEIVED: " + response.toString());
            return response.toString();
        } catch (Exception e) {
            LOGGER.severe("Error while receiving data.");
            e.printStackTrace();
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}
