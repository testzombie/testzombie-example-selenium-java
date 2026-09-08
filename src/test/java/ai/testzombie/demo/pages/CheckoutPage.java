package ai.testzombie.demo.pages;

import ai.testzombie.demo.support.DemoLogger;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class CheckoutPage extends BasePage {
    private static final By PLAN_FREE = By.cssSelector("button[data-plan='Free']");
    private static final By PLAN_STARTER = By.cssSelector("button[data-plan='Starter']");
    private static final By PLAN_PROFESSIONAL = By.cssSelector("button[data-plan='Professional']");
    private static final By PLAN_ENTERPRISE = By.cssSelector("button[data-plan='Enterprise']");
    private static final By COMPANY = By.cssSelector("input[data-testid='company-input']");
    private static final By PAYMENT_METHOD = By.cssSelector("select[data-testid='payment-method']");
    private static final By ACCEPT_TERMS = By.cssSelector("input[data-testid='terms-check']");
    private static final By SAVE_BILLING = By.id("payButton");
    private static final By CONTINUE_TO_CONFIGURATION = By.id("continueToAdmin");

    public CheckoutPage(WebDriver driver) {
        super(driver);
    }

    public CheckoutPage open() {
        DemoLogger.navigation("Plan- und Abrechnungsseite");
        driver.get(baseUrl + "/checkout.html");
        return this;
    }

    public CheckoutPage choosePlan(String plan) {
        DemoLogger.step("Plan auswählen: " + plan);
        By locator = switch (plan) {
            case "Free" -> PLAN_FREE;
            case "Starter" -> PLAN_STARTER;
            case "Professional" -> PLAN_PROFESSIONAL;
            case "Enterprise" -> PLAN_ENTERPRISE;
            default -> throw new IllegalArgumentException("Unsupported plan: " + plan);
        };
        click(locator);
        return this;
    }

    public CheckoutPage enterBilling(String company, String paymentMethod) {
        DemoLogger.step("Abrechnungsdaten erfassen");
        type(COMPANY, company);
        selectByText(PAYMENT_METHOD, paymentMethod);
        click(ACCEPT_TERMS);
        click(SAVE_BILLING);
        return this;
    }

    public AdminPage continueToConfiguration() {
        DemoLogger.step("Zur Workspace-Konfiguration navigieren");
        click(CONTINUE_TO_CONFIGURATION);
        waitForUrlContaining("admin");
        return new AdminPage(driver);
    }
}
