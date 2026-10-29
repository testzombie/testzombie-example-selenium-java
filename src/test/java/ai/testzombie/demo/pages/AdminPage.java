package ai.testzombie.demo.pages;

import ai.testzombie.demo.support.DemoLogger;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class AdminPage extends BasePage {
       private static final By PROJECT_NAME = By.cssSelector("input[data-testid='project-name']");
    private static final By SAVE_RULES = By.id("saveRulesButton");
    private static final By ACTIVATE_WORKSPACE = By.id("activateWorkspace");
    private static final By COMPLETION_DIALOG = By.cssSelector("[role='dialog'], dialog");
    private static final By COMPLETION_TITLE = By.xpath("//*[@role='dialog']//h3[normalize-space(.)='Workspace successfully created'] | //dialog//h3[normalize-space(.)='Workspace successfully created']");
    private static final By CLOSE_COMPLETION_DIALOG = By.cssSelector("[role='dialog'] button[aria-label='Close onboarding summary'], dialog button[aria-label='Close onboarding summary']");

    public AdminPage(WebDriver driver) {
        super(driver);
    }

    public AdminPage open() {
        DemoLogger.navigation("Workspace-Konfiguration");
        driver.get(baseUrl + "/admin.html");
        return this;
    }

    public AdminPage configureWorkspace(String projectName) {
        DemoLogger.step("Workspace konfigurieren: " + projectName);
        WebElement input = find(PROJECT_NAME);
        input.sendKeys(Keys.chord(Keys.CONTROL, "a"), projectName);
        click(SAVE_RULES);
        return this;
    }

    public AdminPage activateWorkspace() {
        DemoLogger.step("Workspace aktivieren");
        click(ACTIVATE_WORKSPACE);
        find(COMPLETION_DIALOG);
        return this;
    }

    public String completionTitle() {
        DemoLogger.step("Abschlussdialog prüfen");
        return find(COMPLETION_TITLE).getText();
    }

    public AdminPage closeCompletionDialog() {
        DemoLogger.step("Abschlussdialog schließen");
        click(CLOSE_COMPLETION_DIALOG);
        wait.until(org.openqa.selenium.support.ui.ExpectedConditions.invisibilityOfElementLocated(COMPLETION_DIALOG));
        return this;
    }
}
