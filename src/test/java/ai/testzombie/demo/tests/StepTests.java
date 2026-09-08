package ai.testzombie.demo.tests;

import ai.testzombie.demo.pages.AdminPage;
import ai.testzombie.demo.pages.CheckoutPage;
import ai.testzombie.demo.pages.ProjectPage;
import ai.testzombie.demo.support.DemoLogger;
import ai.testzombie.demo.support.MutationLevel;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StepTests extends BaseSeleniumTest {

    @ParameterizedTest(name = "Project step at {0}")
    @EnumSource(MutationLevel.class)
    void projectStep(MutationLevel level) {
        DemoLogger.info("SCENARIO", "Projekt-Schritt");
        ProjectPage page = new ProjectPage(driver).open();
        page.setMutationLevel(level);
        page.enterOwner("owner@testzombie.ai", "Admin").chooseProjectType("API");
        int actual = page.persistedMutationLevel();
        DemoLogger.verify("Gespeichertes Mutation Level", level.value(), actual);
        assertEquals(level.value(), actual);
        DemoLogger.pass("Projekt-Schritt erfolgreich verifiziert");
    }

    @ParameterizedTest(name = "Plan step at {0}")
    @EnumSource(MutationLevel.class)
    void planAndBillingStep(MutationLevel level) {
        DemoLogger.info("SCENARIO", "Plan- und Abrechnungsschritt");
        CheckoutPage page = new CheckoutPage(driver).open();
        page.setMutationLevel(level);
        page.choosePlan("Enterprise").enterBilling("Enterprise QA AG", "Bank transfer");
        int actual = page.persistedMutationLevel();
        DemoLogger.verify("Gespeichertes Mutation Level", level.value(), actual);
        assertEquals(level.value(), actual);
        DemoLogger.pass("Plan- und Abrechnungsschritt erfolgreich verifiziert");
    }

    @ParameterizedTest(name = "Configuration step at {0}")
    @EnumSource(MutationLevel.class)
    void configurationStep(MutationLevel level) {
        DemoLogger.info("SCENARIO", "Workspace-Konfigurationsschritt");
        AdminPage page = new AdminPage(driver).open();
        page.setMutationLevel(level);
        page.configureWorkspace("Standalone Configuration Test");
        int actual = page.persistedMutationLevel();
        DemoLogger.verify("Gespeichertes Mutation Level", level.value(), actual);
        assertEquals(level.value(), actual);
        DemoLogger.pass("Workspace-Konfiguration erfolgreich verifiziert");
    }
}
