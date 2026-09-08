package ai.testzombie.demo.support;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Deliberately simple Selenium actions.
 *
 * Each interaction receives exactly one locator. There are no locator fallbacks
 * and no self-healing mechanisms in this test project. Locator repair is expected
 * to be provided by the TestZombie integration.
 *
 * The interaction helpers do, however, handle normal browser mechanics such as
 * scrolling an element into the visible viewport and retrying a native click if
 * layout movement briefly intercepts it. This is stabilization, not healing.
 */
public class SeleniumActions {
    private static final int CLICK_ATTEMPTS = 3;

    protected final WebDriver driver;
    protected final WebDriverWait wait;

    public SeleniumActions(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(12));
    }

    public WebElement find(By locator) {
        DemoLogger.action("Find", locator);
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        DemoLogger.pass("Element visible: " + locator);
        return element;
    }

    public void click(By locator) {
        DemoLogger.action("Click", locator);

        RuntimeException lastFailure = null;
        for (int attempt = 1; attempt <= CLICK_ATTEMPTS; attempt++) {
            try {
                WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
                element = wait.until(ExpectedConditions.elementToBeClickable(locator));
                element.click();

                DemoLogger.pass("Clicked: " + locator);
                return;
            } catch (ElementClickInterceptedException | StaleElementReferenceException ex) {
                lastFailure = ex;
                DemoLogger.info("RETRY", "Click attempt " + attempt + " failed for " + locator
                        + " (" + ex.getClass().getSimpleName() + ")");
                sleep(Duration.ofMillis(250));
            }
        }

        throw lastFailure != null ? lastFailure
                : new IllegalStateException("Could not click element: " + locator);
    }

    public void type(By locator, String value) {
        DemoLogger.action("Type", locator, value);
        WebElement field = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        field.clear();
        field.sendKeys(value);
        DemoLogger.pass("Value entered: " + locator);
    }

    public void selectByText(By locator, String text) {
        DemoLogger.action("Select text", locator, text);
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        new Select(element).selectByVisibleText(text);
        DemoLogger.pass("Selected text '" + text + "': " + locator);
    }

    public void selectByValue(By locator, String value) {
        DemoLogger.action("Select value", locator, value);
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        new Select(element).selectByValue(value);
        DemoLogger.pass("Selected value '" + value + "': " + locator);
    }

    public void waitForUrlContaining(String value) {
        DemoLogger.info("WAIT", "URL contains '" + value + "'");
        wait.until(ExpectedConditions.urlContains(value));
        waitForDocumentReady();
        DemoLogger.pass("URL verified: " + driver.getCurrentUrl());
    }

    protected void waitForDocumentReady() {
        wait.until(webDriver -> "complete".equals(
                ((JavascriptExecutor) webDriver).executeScript("return document.readyState")));
    }


    private static void sleep(Duration duration) {
        try {
            Thread.sleep(duration.toMillis());
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while stabilizing Selenium interaction", ex);
        }
    }
}
