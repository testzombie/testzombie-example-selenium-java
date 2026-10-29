package ai.testzombie.demo.tests;

import ai.testzombie.demo.support.DemoLogger;
import ai.testzombie.demo.support.MutationLevel;
import ai.testzombie.demo.testbase.BaseSeleniumTest;
import ai.testzombie.demo.testbase.OnboardingScenario;
import org.junit.jupiter.api.Test;

/**
 * Discovery demo, equivalent to Playwright's discovery-level0.spec.ts:
 * record one successful, unmodified onboarding journey (mutation level OFF).
 */
class DiscoveryLevel0Test extends BaseSeleniumTest {
    @Override
    protected boolean discoveryEnabled() {
        return true;
    }

    @Test
    void discoverStableOnboardingAtLevelZero() {
        DemoLogger.info("DISCOVERY", "Observe one successful Level 0 onboarding flow");
        OnboardingScenario.run(driver, MutationLevel.OFF, "Discovery Level 0",
                "qa+discovery@testzombie.ai", "Discovery Example Workspace");
    }
}
