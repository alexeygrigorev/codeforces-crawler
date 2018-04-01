package codeforcescrawl;

import java.util.Map;

public class Env {

    private static final Map<String, String> ENV = System.getenv();

    public static void printEnv() {
        System.out.println("ENV:");
        for (Map.Entry<String, String> e : ENV.entrySet()) {
            System.out.printf("%s = \"%s\"%n", e.getKey(), e.getValue());
        }
        System.out.println();
    }

    public static String getString(String name, String defaultValue) {
        if (!ENV.containsKey(name)) {
            System.out.printf("%s is not set, returning the default value %s%n", name, defaultValue);
            return defaultValue;
        }
        String value = ENV.get(name);
        System.out.printf("%s is set to %s%n", name, value);
        return value;
    }

    public static int getInt(String name, int defaultValue) {
        if (!ENV.containsKey(name)) {
            System.out.printf("%s is not set, returning the default value %s%n", name, defaultValue);
            return defaultValue;
        }
        String value = ENV.get(name);
        System.out.printf("%s is set to %s%n", name, value);
        return Integer.parseInt(value);
    }
}
