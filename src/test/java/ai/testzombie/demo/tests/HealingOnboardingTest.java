package ai.testzombie.demo.tests;

import ai.testzombie.demo.pages.ProjectPage;
import ai.testzombie.demo.support.DemoLogger;
import ai.testzombie.demo.support.MutationLevel;
import ai.testzombie.demo.testbase.BaseSeleniumTest;
import ai.testzombie.demo.testbase.OnboardingScenario;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

/** Self-Healing demo: all five mutation levels, without persistent source changes. */
class HealingOnboardingTest extends BaseSeleniumTest {

    @ParameterizedTest(name = "Self-Healing at mutation level {0}")
    @EnumSource(MutationLevel.class)
    void completeOnboardingOnEveryMutationLevel(MutationLevel level) {
        OnboardingScenario.run(driver, level, "Mutation " + level.value(),
                "qa+" + level.name().toLowerCase(java.util.Locale.ROOT) + "@testzombie.ai",
                "Mutation " + level.value() + " Workspace");

        // Costs extra AI credits; this is opt-in just like in the Playwright example.
        if (level == MutationLevel.EXTREME && (Boolean.getBoolean("run.forced.ai.demo")
                || Boolean.parseBoolean(System.getenv("RUN_FORCED_AI_DEMO")))) {
            DemoLogger.info("AI", "Forced AI demo enabled (uses AI credits)");
            driver.get(System.getProperty("baseUrl", "http://demoweb2.testzombie.ai") + "/index.html");
            new ProjectPage(driver).aiCall("Confirm Critical Action");
        }
    }
}
