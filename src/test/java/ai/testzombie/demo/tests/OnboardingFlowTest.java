package ai.testzombie.demo.tests;

import ai.testzombie.demo.pages.AdminPage;
import ai.testzombie.demo.pages.CheckoutPage;
import ai.testzombie.demo.pages.ProjectPage;
import ai.testzombie.demo.support.DemoLogger;
import ai.testzombie.demo.support.MutationLevel;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OnboardingFlowTest extends BaseSeleniumTest {

    @ParameterizedTest(name = "Complete onboarding at mutation level {0}")
    @EnumSource(MutationLevel.class)
    void completeOnboardingOnEveryMutationLevel(MutationLevel level) {
        DemoLogger.info("SCENARIO", "Vollständiges Workspace-Onboarding");
        DemoLogger.info("MUTATION", level);

        ProjectPage project = new ProjectPage(driver).open();
        project.disableRandomOverlay();
        project.setMutationLevel(level);
        project.enterOwner("qa+" + level.name().toLowerCase() + "@testzombie.ai", "QA Engineer")
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

        admin.configureWorkspace("Mutation " + level.value() + " Workspace")
             .activateWorkspace();

        String title = admin.completionTitle();
        DemoLogger.verify("Abschlussdialog bestätigt erfolgreiches Onboarding",
                "contains: Workspace successfully created", title);
        assertTrue(title.contains("Workspace successfully created"));
        DemoLogger.pass("Onboarding erfolgreich abgeschlossen");

        admin.closeCompletionDialog();

        //AI Healing Demo with plain Text as locator
        if(level==MutationLevel.EXTREME) {
            System.out.println("TestZombieAI -> forced AI Call -needed active Subscription or TopUp");
            System.out.println("TestZombieAI -> find element over plain text as locator, triggers AI Call");
            driver.get("http://demoweb2.testzombie.ai/index.html");
            project.AICall("Confirm Critical Action");
        }
    }
}
