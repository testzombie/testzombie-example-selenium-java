package ai.testzombie.demo.tests;

import ai.testzombie.demo.support.DemoLogger;
import ai.testzombie.demo.support.DriverFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.extension.TestWatcher;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

public abstract class BaseSeleniumTest {
    protected WebDriver driver;

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
        String baseUrl = System.getProperty("baseUrl", "http://demoweb2.testzombie.ai").replaceAll("/$", "");
        String browser = System.getProperty("browser", "firefox");
        boolean headless = Boolean.parseBoolean(System.getProperty("headless", "false"));

        DemoLogger.startTest(testInfo.getDisplayName(), baseUrl, browser, headless);
        DemoLogger.step("Browser starten und Demo öffnen");

        driver = DriverFactory.create();
        DemoLogger.navigation(baseUrl + "/");
        driver.get(baseUrl + "/");
        ((JavascriptExecutor) driver).executeScript("localStorage.clear();");
        driver.navigate().refresh();
        DemoLogger.pass("Demo geladen und lokaler Zustand zurückgesetzt");
    }

    @AfterEach
    void stopBrowser() {
        if (driver != null) {
            DemoLogger.info("BROWSER", "Chrome session schließen");
            driver.quit();
        }
    }
}
