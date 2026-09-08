package ai.testzombie.demo.support;

import com.testzombie.driver.TestZombieDriver;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.time.Duration;

public final class DriverFactory {
    private DriverFactory() {

    }

    public static WebDriver create() {
        String browser = System.getProperty("browser", "chrome").toLowerCase();
        boolean headless = Boolean.parseBoolean(System.getProperty("headless", "false"));
        // TestZombie credentials:
        // - local quickstart: -Dtestzombie.apikey=... -Dtestzombie.email=...
        // - CI/CD: TESTZOMBIE_API_KEY and TESTZOMBIE_EMAIL environment variables
        String apiKey = firstNonBlank(
                System.getProperty("testzombie.apikey"),
                System.getenv("TESTZOMBIE_API_KEY")
        );
        String email = firstNonBlank(
                System.getProperty("testzombie.email"),
                System.getenv("TESTZOMBIE_EMAIL")
        );

        if (apiKey == null || email == null) {
            System.out.println("Missing TestZombie credentials. Set testzombie.apikey/testzombie.email "
                            + "or TESTZOMBIE_API_KEY/TESTZOMBIE_EMAIL. to use TestZombieAI Healing"
            );
        }

        //Or You can copy your Credentials directly from onboarding screen after registration on testzombie.ai
        TestZombieDriver.setCredentials(apiKey, email);


        WebDriver driver = switch (browser) {
            case "firefox" -> firefox(headless);
            case "edge" -> edge(headless);
            case "chrome" -> chrome(headless);
            default -> throw new IllegalArgumentException("Unsupported browser: " + browser);
        };
        driver.manage().timeouts().implicitlyWait(Duration.ZERO);
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(20));
        driver.manage().window().maximize();
        //driver.manage().window().setSize(new org.openqa.selenium.Dimension(1440, 1100));
        return driver;
    }

    private static String firstNonBlank(String first, String second) {
        if (first != null && !first.isBlank()) return first;
        if (second != null && !second.isBlank()) return second;
        return null;
    }

    private static WebDriver chrome(boolean headless) {
        ChromeOptions options = new ChromeOptions();
        if (headless) options.addArguments("--headless=new");
        options.addArguments("--start-maximized");
        options.addArguments("--disable-dev-shm-usage", "--no-sandbox");
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
        options.addArguments("--start-maximized");
        return TestZombieDriver.createEdge(options);
    }
}
