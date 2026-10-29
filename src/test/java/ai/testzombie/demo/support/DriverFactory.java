package ai.testzombie.demo.support;

import com.testzombie.driver.SourceUpdateMode;
import com.testzombie.driver.TestZombieDriver;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.time.Duration;
import java.util.Locale;

/** Creates TestZombie-wrapped WebDrivers with a scenario-specific source update mode. */
public final class DriverFactory {
    private DriverFactory() {}

    public static WebDriver create() {
        return create(SourceUpdateMode.OFF, false);
    }

    public static WebDriver create(SourceUpdateMode sourceUpdateMode) {
        return create(sourceUpdateMode, false);
    }

    /** Configure features BEFORE initializing the wrapped Selenium session. */
    public static WebDriver create(SourceUpdateMode sourceUpdateMode, boolean discoveryEnabled) {
        String browser = setting("browser", "BROWSER", "chrome").toLowerCase(Locale.ROOT);
        boolean headless = Boolean.parseBoolean(setting("headless", "HEADLESS", "false"));
        String apiKey = setting("testzombie.apikey", "TESTZOMBIE_API_KEY", null);
        String email = setting("testzombie.email", "TESTZOMBIE_EMAIL", null);

        if (apiKey == null || email == null) {
            throw new IllegalStateException("TestZombie credentials missing. Set TESTZOMBIE_API_KEY "
                    + "and TESTZOMBIE_EMAIL, or pass -Dtestzombie.apikey=... "
                    + "-Dtestzombie.email=... .");
        }

        TestZombieDriver.setCredentials(apiKey, email);
        // This explicit override keeps the scenarios independent of any global env setting.
        TestZombieDriver.setSourceUpdateMode(sourceUpdateMode);
        NativeDiscovery.configure(discoveryEnabled);
        DemoLogger.info("HEALING", "Source update mode: " + sourceUpdateMode);

        WebDriver driver = switch (browser) {
            case "chrome" -> chrome(headless);
            case "firefox" -> firefox(headless);
            case "edge" -> edge(headless);
            default -> throw new IllegalArgumentException("Unsupported browser: " + browser);
        };
        driver.manage().timeouts().implicitlyWait(Duration.ZERO);
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(20));
        driver.manage().window().setSize(new org.openqa.selenium.Dimension(1440, 1100));
        return driver;
    }

    public static String setting(String property, String env, String fallback) {
        String fromProperty = System.getProperty(property);
        if (fromProperty != null && !fromProperty.isBlank()) return fromProperty;
        String fromEnv = System.getenv(env);
        if (fromEnv != null && !fromEnv.isBlank()) return fromEnv;
        return fallback;
    }

    private static WebDriver chrome(boolean headless) {
        ChromeOptions options = new ChromeOptions();
        if (headless) options.addArguments("--headless=new");
        options.addArguments("--window-size=1440,1100", "--disable-dev-shm-usage");
        return TestZombieDriver.createChrome(options);
    }

    private static WebDriver firefox(boolean headless) {
        FirefoxOptions options = new FirefoxOptions();
        if (headless) options.addArguments("-headless");
        return TestZombieDriver.createFirefox(options);
    }

    private static WebDriver edge(boolean headless) {
        EdgeOptions options = new EdgeOptions();
        if (headless) options.addArguments("--headless=new");
        options.addArguments("--window-size=1440,1100");
        return TestZombieDriver.createEdge(options);
    }
}
