package ch.ethz.inf.globis.wide.logging;


import org.apache.log4j.Level;

import java.io.IOException;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * Created by fabian on 18.04.16.
 */
public class WideLogger {
    private final Logger LOGGER;
    private final WideFileLogger FILELOGGER;

    public WideLogger(String name) {
        LOGGER = Logger.getLogger(name);
        FILELOGGER = WideFileLogger.getInstance();
    }

    public void info(String msg) {
        LOGGER.info(buildFullString(msg));
        FILELOGGER.info(buildFullString(msg));
    }

    public void config(String msg) {
        LOGGER.config(buildFullString(msg));
        FILELOGGER.config(buildFullString(msg));
    }

    public void severe(String msg) {
        LOGGER.severe(buildFullString(msg));
        FILELOGGER.severe(buildFullString(msg));
    }

    public void fine(String msg) {
        LOGGER.fine(buildFullString(msg));
        FILELOGGER.fine(buildFullString(msg));
    }

    public void warning(String msg) {
        LOGGER.warning(buildFullString(msg));
        FILELOGGER.warning(buildFullString(msg));
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
