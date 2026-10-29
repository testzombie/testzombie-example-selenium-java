package ai.testzombie.demo.testbase;

import ai.testzombie.demo.support.DemoLogger;
import ai.testzombie.demo.support.DriverFactory;
import ai.testzombie.demo.support.NativeDiscovery;
import com.testzombie.driver.SourceUpdateMode;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.extension.TestWatcher;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

/** Common lifecycle. Each test type selects the feature at the factory boundary. */
public abstract class BaseSeleniumTest {
    protected WebDriver driver;

    protected SourceUpdateMode sourceUpdateMode() {
        return SourceUpdateMode.OFF;
    }

    protected boolean discoveryEnabled() {
        return false;
    }

    @RegisterExtension
    final TestWatcher resultLogger = new TestWatcher() {
        @Override
        public void testSuccessful(ExtensionContext context) {
            DemoLogger.finish(true);
        }

        @Override
        public void testFailed(ExtensionContext context, Throwable cause) {
            DemoLogger.failure("Test failed", cause);
            DemoLogger.finish(false);
        }
    };

    @BeforeEach
    void startBrowser(TestInfo testInfo) {
        String baseUrl = DriverFactory.setting("baseUrl", "BASE_URL", "http://demoweb2.testzombie.ai")
                .replaceAll("/$", "");
        String browser = DriverFactory.setting("browser", "BROWSER", "chrome");
        boolean headless = Boolean.parseBoolean(DriverFactory.setting("headless", "HEADLESS", "false"));

        DemoLogger.startTest(testInfo.getDisplayName(), baseUrl, browser, headless);
        DemoLogger.step("Browser starten und Demo öffnen");

        driver = DriverFactory.create(sourceUpdateMode(), discoveryEnabled());
        resetDemoState();
    }

    /** Reset demo state between repeated flows without restarting the TestZombie browser session. */
    protected void resetDemoState() {
        String baseUrl = DriverFactory.setting("baseUrl", "BASE_URL", "http://demoweb2.testzombie.ai")
                .replaceAll("/$", "");
        DemoLogger.navigation(baseUrl + "/");
        driver.get(baseUrl + "/");
        ((JavascriptExecutor) driver).executeScript("localStorage.clear();");
        driver.navigate().refresh();
        DemoLogger.pass("Demo geladen und lokaler Zustand zurückgesetzt");
    }

    @AfterEach
    void stopBrowser() {
        try {
            if (driver != null) {
                DemoLogger.info("BROWSER", "Session schließen");
                driver.quit();
            }
        } finally {
            driver = null;
            // SDK configuration is process-wide: restore the default between scenarios.
            NativeDiscovery.configure(false);
        }
    }
}
