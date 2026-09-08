package ai.testzombie.demo.pages;

import ai.testzombie.demo.support.DemoLogger;
import ai.testzombie.demo.support.MutationLevel;
import ai.testzombie.demo.support.SeleniumActions;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

public abstract class BasePage extends SeleniumActions {
    protected static final By MUTATION_LEVEL = By.id("mutationLevel");
    protected final String baseUrl;

    protected BasePage(WebDriver driver) {
        super(driver);
        this.baseUrl = System.getProperty("baseUrl", "http://demoweb2.testzombie.ai").replaceAll("/$", "");
    }

    public void setMutationLevel(MutationLevel level) {
        DemoLogger.step("Mutation Level auf " + level + " setzen");
        selectByValue(MUTATION_LEVEL, String.valueOf(level.value()));
        wait.until(d -> String.valueOf(level.value()).equals(
                ((JavascriptExecutor) d).executeScript("return localStorage.getItem('tz-mutation-level');")));
        DemoLogger.pass("Mutation Level gespeichert: " + level.value());
    }

    public int persistedMutationLevel() {
        Object value = ((JavascriptExecutor) driver)
                .executeScript("return localStorage.getItem('tz-mutation-level');");
        return Integer.parseInt(String.valueOf(value));
    }
}
