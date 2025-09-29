package sk.is.urso.util;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class LoggerUtils {

    @Value("${logging.enabled}")
    boolean loggingEnabled;

    public enum LogType {
        INFO,
        ERROR,
        WARNING
    }

    public void log(LogType logType, Logger logger, String message) {
        if (loggingEnabled) {
            switch (logType) {
                case INFO:
                    logger.info(message);
                    break;
                case ERROR:
                    logger.error(message);
                    break;
                case WARNING:
                    logger.warn(message);
                    break;
            }
        }
    }
}
