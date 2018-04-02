package codeforcescrawl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class Env {

    private static final Logger LOGGER = LoggerFactory.getLogger(Env.class);

    private static final Map<String, String> ENV = System.getenv();

    public static void printEnv() {
        LOGGER.info("ENV:");

        ENV.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .forEach(e -> LOGGER.debug("  {} = \"{}\"", e.getKey(), e.getValue()));
    }

    public static String getString(String name, String defaultValue) {
        if (!ENV.containsKey(name)) {
            LOGGER.info("{} is not set, returning the default value '{}'", name, defaultValue);
            return defaultValue;
        }
        String value = ENV.get(name);
        LOGGER.info("{} is set to '{}'", name, value);
        return value;
    }

    public static int getInt(String name, int defaultValue) {
        if (!ENV.containsKey(name)) {
            LOGGER.info("{} is not set, returning the default value '{}'", name, defaultValue);
            return defaultValue;
        }
        String value = ENV.get(name);
        LOGGER.info("{} is set to '{}'", name, value);
        return Integer.parseInt(value);
    }
}
