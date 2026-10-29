package ai.testzombie.demo.testbase;

import ai.testzombie.demo.pages.AdminPage;
import ai.testzombie.demo.pages.CheckoutPage;
import ai.testzombie.demo.pages.ProjectPage;
import ai.testzombie.demo.support.DemoLogger;
import ai.testzombie.demo.support.MutationLevel;
import org.openqa.selenium.WebDriver;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Shared business flow: the three feature demos run identical Selenium actions. */
public final class OnboardingScenario {
    private OnboardingScenario() {}

    static void run(WebDriver driver, MutationLevel level, String scenario) {
        // Backwards-compatible default for legacy generated/step tests.
        String email = "qa+" + scenario.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+", "-")
                + "@testzombie.ai";
        run(driver, level, scenario, email, scenario + " Workspace");
    }

    public static void run(WebDriver driver, MutationLevel level, String scenario,
                    String ownerEmail, String workspaceName) {
        DemoLogger.info("SCENARIO", scenario);
        DemoLogger.info("MUTATION", level);

        ProjectPage project = new ProjectPage(driver).open();
        project.disableRandomOverlay();
        project.setMutationLevel(level);
        project.enterOwner(ownerEmail, "QA Engineer")
                .chooseProjectType("Web");

        CheckoutPage checkout = project.continueToPlan();
        int checkoutLevel = checkout.persistedMutationLevel();
        DemoLogger.verify("Mutation Level auf Plan-Seite übernommen", level.value(), checkoutLevel);
        assertEquals(level.value(), checkoutLevel, "Mutation level must survive navigation");
        DemoLogger.pass("Mutation Level auf Plan-Seite verifiziert");

        checkout.choosePlan("Professional")
                .enterBilling("TestZombie QA GmbH", "Invoice");

        AdminPage admin = checkout.continueToConfiguration();
        int adminLevel = admin.persistedMutationLevel();
        DemoLogger.verify("Mutation Level auf Konfigurationsseite übernommen", level.value(), adminLevel);
        assertEquals(level.value(), adminLevel, "Mutation level must survive all pages");
        DemoLogger.pass("Mutation Level auf Konfigurationsseite verifiziert");

        admin.configureWorkspace(workspaceName).activateWorkspace();
        String title = admin.completionTitle();
        DemoLogger.verify("Abschlussdialog", "contains: Workspace successfully created", title);
        assertTrue(title.contains("Workspace successfully created"), "Onboarding must succeed");
        DemoLogger.pass("Onboarding erfolgreich abgeschlossen");
        admin.closeCompletionDialog();
    }
}
