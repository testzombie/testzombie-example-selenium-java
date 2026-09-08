package ai.testzombie.demo.pages;

import ai.testzombie.demo.support.DemoLogger;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

public class ProjectPage extends BasePage {
    private static final By OWNER_EMAIL = By.name("email");
    private static final By OWNER_ROLE = By.id("role");
    private static final By SAVE_OWNER = By.id("saveOwnerButton");
    private static final By PROJECT_TYPE_WEB = By.cssSelector("button[data-tab='web']");
    private static final By PROJECT_TYPE_API = By.cssSelector("button[data-tab='api']");
    private static final By CONTINUE_TO_PLAN = By.id("continueToCheckout");

    public ProjectPage(WebDriver driver) {
        super(driver);
    }

    public ProjectPage open() {
        DemoLogger.navigation("Projektseite");
        driver.get(baseUrl + "/");
        return this;
    }

    public ProjectPage disableRandomOverlay() {
        DemoLogger.step("Zufälliges Demo-Overlay deaktivieren");
        ((JavascriptExecutor) driver).executeScript("""
                localStorage.setItem('tz-random-overlay', 'off');
                document.querySelectorAll('.random-blocking-overlay, .overlay-scrim')
                        .forEach(element => element.remove());
                const toggle = document.getElementById('randomOverlayToggle');
                if (toggle) {
                    toggle.checked = false;
                }
                """);
        DemoLogger.pass("Zufälliges Overlay deaktiviert");
        return this;
    }

    public ProjectPage enterOwner(String email, String role) {
        DemoLogger.step("Projektverantwortlichen erfassen");
        type(OWNER_EMAIL, email);
        selectByText(OWNER_ROLE, role);
        click(SAVE_OWNER);
        return this;
    }

    public ProjectPage chooseProjectType(String type) {
        DemoLogger.step("Projekttyp auswählen: " + type);
        By locator = switch (type) {
            case "Web" -> PROJECT_TYPE_WEB;
            case "API" -> PROJECT_TYPE_API;
            default -> throw new IllegalArgumentException("Unsupported project type: " + type);
        };
        click(locator);
        return this;
    }

    public CheckoutPage continueToPlan() {
        DemoLogger.step("Zur Plan-Auswahl navigieren");
        click(CONTINUE_TO_PLAN);
        waitForUrlContaining("checkout");
        return new CheckoutPage(driver);
    }

    public void AICall(String findstring) {
        System.out.println("AI Healing Versuch");
        //WebElement webElement=driver.findElement(By.xpath("Confirm Critical Action"));
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        click(By.xpath(findstring));
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }
}
