package config;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Properties;

public class ConfigManager {
    private static final Properties properties = new Properties();
    private static ConfigManager configManager;
    private static final String CONFIG_FILE = "src/test/resources/config.properties";

    // Configuration keys
    public static final String ENVIRONMENT = "environment";
    public static final String APP_PATH = "app.path";
    public static final String APPIUM_URL = "appium.url";
    public static final String VALID_EMAIL = "valid.email";
    public static final String VALID_PASSWORD = "valid.password";
    public static final String INVALID_EMAIL = "invalid.email";
    public static final String INVALID_PASSWORD = "invalid.password";
    public static final String IMPLICIT_WAIT = "implicit.wait";
    public static final String EXPLICIT_WAIT = "explicit.wait";
    public static final String PAGE_LOAD_TIMEOUT = "page.load.timeout";
    public static final String SCREENSHOT_ON_PASS = "screenshot.on.pass";
    public static final String SCREENSHOT_ON_FAIL = "screenshot.on.fail";
    public static final String SCREENSHOT_DIRECTORY = "screenshot.directory";
    public static final String LOG_LEVEL = "log.level";
    public static final String LOG_FILE = "log.file";

    private ConfigManager() {
        loadProperties();
    }

    public static synchronized ConfigManager getInstance() {
        if (configManager == null) {
            configManager = new ConfigManager();
        }
        return configManager;
    }

    private void loadProperties() {
        try (FileInputStream fis = new FileInputStream(Paths.get(CONFIG_FILE).toFile())) {
            properties.load(fis);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load configuration file: " + CONFIG_FILE, e);
        }
    }

    // Environment
    public String getEnvironment() {
        return getString(ENVIRONMENT, "test");
    }

    // Application
    public String getAppPath() {
        return getString(APP_PATH);
    }

    public String getAppiumUrl() {
        return getString(APPIUM_URL);
    }

    // Test Data
    public String getValidEmail() {
        return getString(VALID_EMAIL);
    }

    public String getValidPassword() {
        return getString(VALID_PASSWORD);
    }

    public String getInvalidEmail() {
        return getString(INVALID_EMAIL);
    }

    public String getInvalidPassword() {
        return getString(INVALID_PASSWORD);
    }

    // Timeouts
    public int getImplicitWait() {
        return getInt(IMPLICIT_WAIT, 10);
    }

    public int getExplicitWait() {
        return getInt(EXPLICIT_WAIT, 15);
    }

    public int getPageLoadTimeout() {
        return getInt(PAGE_LOAD_TIMEOUT, 30);
    }

    // Reporting
    public boolean isScreenshotOnPass() {
        return getBoolean(SCREENSHOT_ON_PASS, false);
    }

    public boolean isScreenshotOnFail() {
        return getBoolean(SCREENSHOT_ON_FAIL, true);
    }

    public String getScreenshotDirectory() {
        return getString(SCREENSHOT_DIRECTORY, "test-output/screenshots");
    }

    // Logging
    public String getLogLevel() {
        return getString(LOG_LEVEL, "INFO");
    }

    public String getLogFile() {
        return getString(LOG_FILE, "test-output/logs/test.log");
    }

    // Helper methods
    private String getString(String key) {
        return getString(key, null);
    }

    private String getString(String key, String defaultValue) {
        return System.getProperty(key, properties.getProperty(key, defaultValue));
    }

    private int getInt(String key, int defaultValue) {
        try {
            return Integer.parseInt(getString(key, String.valueOf(defaultValue)));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private boolean getBoolean(String key, boolean defaultValue) {
        String value = getString(key);
        return value != null ? Boolean.parseBoolean(value) : defaultValue;
    }
}
