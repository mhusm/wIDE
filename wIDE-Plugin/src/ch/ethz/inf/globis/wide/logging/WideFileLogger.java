package ch.ethz.inf.globis.wide.logging;

import java.io.IOException;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

/**
 * Created by fabian on 21.07.16.
 */
public class WideFileLogger {
    private static final WideFileLogger INSTANCE = new WideFileLogger();
    private final Logger LOGGER;

    private WideFileLogger() {
        LOGGER = Logger.getLogger(WideFileLogger.class.getName());

        try {
            // This block configure the logger with handler and formatter
            FileHandler fh = new FileHandler("/Users/fabian/Documents/Studium/Masterarbeit/user_study/log.log");
            LOGGER.addHandler(fh);

        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public static WideFileLogger getInstance() {
        return INSTANCE;
    }

}
