package ch.ethz.inf.globis.wide.logging;


import org.apache.log4j.Level;

import java.util.Date;
import java.util.logging.Logger;

/**
 * Created by fabian on 18.04.16.
 */
public class WideLogger {
    private final Logger LOGGER;

    public WideLogger(String name) {
        LOGGER = Logger.getLogger(name);
    }

    public void info(String msg) {
        LOGGER.info(buildFullString(msg));
    }

    public void config(String msg) {
        LOGGER.config(buildFullString(msg));
    }

    public void severe(String msg) {
        LOGGER.severe(buildFullString(msg));
    }

    public void fine(String msg) {
        LOGGER.fine(buildFullString(msg));
    }

    public void warning(String msg) {
        LOGGER.warning(buildFullString(msg));
    }

    private String buildFullString(String msg) {
        String message = "[wIDE]";
        message += " " + new Date(System.currentTimeMillis());
        message += " " + LOGGER.getName();
        message += "\n\t";
        message += msg;

        return message;
    }
}
