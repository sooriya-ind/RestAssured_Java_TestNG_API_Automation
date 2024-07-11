package utilities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertyUtils {

    private PropertyUtils() {

    }

    private static Properties property = new Properties();
    private static final Logger log = LogManager.getLogger(PropertyUtils.class);

    static {
        try (FileInputStream file = new FileInputStream(System.getProperty("user.dir") + "\\Configuration.properties")) {
            property = new Properties();
            property.load(file);
        } catch (IOException e) {
            log.error("!!! Cannot Load Configuration Properties !!!");
        }
    }

    public static String getValue(String key) {
        return property.getProperty(key);
    }

}
