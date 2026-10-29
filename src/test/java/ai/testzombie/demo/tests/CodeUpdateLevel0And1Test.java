package ai.testzombie.demo.tests;

import ai.testzombie.demo.support.DemoLogger;
import ai.testzombie.demo.support.MutationLevel;
import ai.testzombie.demo.testbase.BaseSeleniumTest;
import ai.testzombie.demo.testbase.OnboardingScenario;
import com.testzombie.driver.SourceUpdateMode;
import org.junit.jupiter.api.Test;

import java.util.Locale;

/**
 * Persistent Healing demo: first complete the stable Level 0 onboarding, then
 * repeat it with Level 1 mutations in the same browser/TestZombie session.
 * The second pass triggers healing and optionally persists repaired locators.
 */
class CodeUpdateLevel0And1Test extends BaseSeleniumTest {
    @Override
    protected SourceUpdateMode sourceUpdateMode() {
        String value = System.getProperty("testzombie.codeupdate.mode", "apply").trim();
        return switch (value.toLowerCase(Locale.ROOT)) {
            case "apply" -> SourceUpdateMode.APPLY;
            case "preview" -> SourceUpdateMode.PREVIEW;
            default -> throw new IllegalArgumentException(
                    "testzombie.codeupdate.mode must be apply or preview");
        };
    }

    @Test
    void completeBaselineThenHealAndPersistLevelOneLocators() {
        // BaseSeleniumTest passes APPLY / PREVIEW to DriverFactory BEFORE browser creation.
        DemoLogger.info("CODE UPDATE", "Source update mode: " + sourceUpdateMode());
        DemoLogger.info("CODE UPDATE", "1/2: baseline onboarding at mutation level 0");
        OnboardingScenario.run(driver, MutationLevel.OFF, "Code Update Level 0",
                "qa+codeupdate@testzombie.ai", "Code Update Example Workspace");

        // A fresh application state, but the same WebDriver and TestZombie configuration.
        DemoLogger.info("CODE UPDATE", "Level 0 complete; reset demo without restarting browser");
        resetDemoState();

        DemoLogger.info("CODE UPDATE", "2/2: healing and code update at mutation level 1");
        OnboardingScenario.run(driver, MutationLevel.LIGHT, "Code Update Level 1",
                "qa+codeupdate@testzombie.ai", "Code Update Example Workspace");

        DemoLogger.info("CODE UPDATE", "Inspect changes: git diff -- src/test/java/ai/testzombie/demo/pages");
    }
}
