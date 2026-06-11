package dev.sim0n.caesium.util;

import java.util.Properties;

public final class VersionUtil {

    private VersionUtil() {}

    public static String getVersion() {
        Properties properties = new Properties();
        try {
            properties.load(VersionUtil.class.getResourceAsStream("caesium.properties"));
            return properties.getProperty("version");
        } catch (Exception e) {
            return "DEBUG";
        }
    }
}
