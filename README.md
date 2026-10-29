# TestZombie Selenium Java Example

A small Selenium Java project that demonstrates **TestZombie Self-Healing**, **Discovery** and optional **Persistent Healing** with `com.testzombie:testzombie-lib-java`.

The example intentionally uses normal Selenium tests and Page Objects. There are no TestZombie-specific fallback locators in the test code.

## Quick start — 1, 2, 3

### 1. Install

Clone the project and make sure the following are available:

- Java 21 or newer
- Maven
- Chrome, Firefox or Edge

The example uses **TestZombie Java SDK 1.3.1** (declared in `pom.xml`):

```xml
<dependency>
    <groupId>com.testzombie</groupId>
    <artifactId>testzombie-lib-java</artifactId>
    <version>1.3.1</version>
</dependency>
```

Maven resolves the dependencies when you run the tests. Selenium Manager handles browser drivers.

### 2. Configure TestZombie

Create an account at `https://testzombie.ai` and copy your **API key** and **account e-mail**. Choose one of the following ways to configure the credentials.

#### Option 1 — Enter credentials in `pom.xml` (quickest local setup)

Open the project's `pom.xml` and find the `defaults` profile at the bottom. Fill in the two existing, empty properties:

```xml
<profiles>
    <profile>
        <id>defaults</id>
        <activation>
            <activeByDefault>true</activeByDefault>
        </activation>
        <properties>
            <baseUrl>http://demoweb2.testzombie.ai</baseUrl>
            <browser>chrome</browser>
            <headless>false</headless>
            <testzombie.apikey>YOUR_API_KEY</testzombie.apikey>
            <testzombie.email>YOUR_EMAIL</testzombie.email>
        </properties>
    </profile>
</profiles>
```

Replace `YOUR_API_KEY` and `YOUR_EMAIL` with your own values. The existing Maven Surefire configuration passes these properties to the test JVM, where `DriverFactory` reads them and calls `TestZombieDriver.setCredentials(...)`.

Then start a demo without adding credential arguments:

```bash
mvn test -Dtest=OnboardingFlowTest
```

> **Security:** This is convenient for a local demo only. **Never commit or share a `pom.xml` containing real credentials.** Before pushing, restore the two properties to empty values. For shared repositories and CI, use one of the alternatives below.

#### Option 2 — Pass credentials as Maven parameters

Keep the two credential entries in `pom.xml` empty and pass the values when running a test:

```bash
mvn test -Dtest=OnboardingFlowTest \
  -Dtestzombie.apikey=YOUR_API_KEY \
  -Dtestzombie.email=YOUR_EMAIL
```

#### Option 3 — Set environment variables

Leave the two credential entries in `pom.xml` empty and set environment variables instead.

**Windows CMD**

```cmd
set TESTZOMBIE_API_KEY=YOUR_API_KEY
set TESTZOMBIE_EMAIL=YOUR_EMAIL
```

**PowerShell**

```powershell
$env:TESTZOMBIE_API_KEY="YOUR_API_KEY"
$env:TESTZOMBIE_EMAIL="YOUR_EMAIL"
```

**Linux / macOS**

```bash
export TESTZOMBIE_API_KEY="YOUR_API_KEY"
export TESTZOMBIE_EMAIL="YOUR_EMAIL"
```

`DriverFactory` looks for Java system properties first, then the corresponding environment variables. All three options use the same `TestZombieDriver.setCredentials(...)` integration.

### 3. Run the Self-Healing demo

Run the complete five-level onboarding flow in Chrome:

```bash
mvn test -Dtest=OnboardingFlowTest
```

To run it in headless mode:

```bash
mvn test -Dtest=OnboardingFlowTest -Dheadless=true
```

The same business flow runs through:

```text
OFF → LIGHT → REALISTIC → HARD → EXTREME
```

At higher mutation levels the original locators may become invalid. TestZombie attempts to identify the correct element and continues the Selenium action with the healed locator.

> **Why select the test class?** Unlike the Playwright example's `npm test`, `mvn test` without `-Dtest` also selects the Discovery and Code Update tests. The Code Update test intentionally writes to Java source files in its default `APPLY` mode.

---

## Discovery — learn a working business flow

Self-Healing fixes broken locators. **Discovery has a different job:** it observes a successful test flow and collects useful context about what happened.

Run the dedicated Discovery example with:

```bash
mvn test -Dtest=DiscoveryLevel0Test
```

The example executes one stable onboarding flow on **Mutation Level 0 / OFF**.

That is intentional: nothing should be broken or healed in this scenario. We want TestZombie to see how the application works when the business flow succeeds normally.

### What happens during Discovery?

In simple terms:

```text
Existing Selenium test
        ↓
successful user flow
        ↓
TestZombie Discovery
        ↓
pages + elements + interactions + context
        ↓
AI Test Design
        ↓
business test cases
        ↓
selected test cases
        ↓
automation code
```

While the test runs, TestZombie Discovery can collect context such as visited pages, relevant UI elements, interactions and their sequence. That gives AI Test Design context from a **real working user journey**, not just a URL or a screenshot. This context can be used to derive business test cases and, later, to generate automation for selected cases.

The dedicated example is in:

```text
src/test/java/ai/testzombie/demo/tests/DiscoveryLevel0Test.java
```

It enables Discovery by overriding the feature flag in the shared test base:

```java
@Override
protected boolean discoveryEnabled() {
    return true;
}
```

Before the browser is created, `BaseSeleniumTest` passes that flag to `DriverFactory`, which calls:

```java
NativeDiscovery.configure(discoveryEnabled);
```

`NativeDiscovery` configures **the TestZombie SDK's own Discovery**, not a separate Selenium-side JSON recorder. With the corresponding Java SDK setters available, the relevant configuration is equivalent to:

```java
TestZombieDriver.setDiscoveryEnabled(true);
TestZombieDriver.setDiscoveryFileEnabled(true);
TestZombieDriver.setDiscoveryFilePath(
    "test-results/testzombie-discovery-{testRunId}.json"
);
```

The project's `NativeDiscovery` helper currently resolves these setters at runtime. The file options are optional: if the SDK does not expose both file setters, backend Discovery may still work, but local JSON output is **not guaranteed**. When supported, the configured output location is:

```text
test-results/testzombie-discovery-<testRunId>.json
```

The test itself remains normal Selenium/Page-Object code.

### Why Mutation Level 0?

The regular demo intentionally breaks locators to demonstrate Self-Healing.

The Discovery demo does the opposite: it uses the stable baseline so the collected data describes a valid, successful business process.

That makes the roles easy to distinguish:

```text
Discovery     → understand the working flow
Self-Healing  → keep the automated flow running when locators change
```

---

## Code Update — run Level 0, heal Level 1 and update the source

Discovery learns a working flow. The **Code Update** example demonstrates the next maintenance step: what happens when the application changes and an existing Selenium locator no longer matches.

Run the dedicated example with:

```bash
mvn test -Dtest=CodeUpdateLevel0And1Test
```

Unlike the Playwright example (which runs Level 1 directly), this Java example executes the onboarding journey **twice in the same WebDriver/TestZombie session**:

1. **Mutation Level 0 / OFF** — complete the unchanged baseline.
2. **Reset the demo application state**, without closing the browser session.
3. **Mutation Level 1 / LIGHT** — run the same flow with changed locators; allow TestZombie to heal and persist successful replacements.

Persistent Healing is enabled in `APPLY` mode for this scenario only.

### What should happen?

In simple terms:

```text
Existing Selenium test
        ↓
successful Level-0 baseline
        ↓
reset application state (same browser session)
        ↓
Mutation Level 1 changes the UI / locators
        ↓
original locator no longer works
        ↓
TestZombie heals the element at runtime
        ↓
action succeeds with the healed locator
        ↓
Code Update writes the working locator back to the Page Object
```

The important point is that the test code does **not** contain manual fallback locators. TestZombie first finds a working replacement; successful healing can then be persisted to the Java source.

The dedicated example is in:

```text
src/test/java/ai/testzombie/demo/tests/CodeUpdateLevel0And1Test.java
```

It selects the source update mode explicitly:

```java
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
```

`BaseSeleniumTest` passes this mode to `DriverFactory`, which configures it **before browser creation**:

```java
TestZombieDriver.setSourceUpdateMode(sourceUpdateMode);
```

After the test completes, inspect the changed Page Object selectors with:

```bash
git diff -- src/test/java/ai/testzombie/demo/pages
```

To reset the demo and run it again from the original locator state:

```bash
git restore -- src/test/java/ai/testzombie/demo/pages
```

> `git restore` discards **all uncommitted changes** in those Page Object files. Review `git diff` first if you have edited them manually.

This keeps the two examples intentionally separate:

```text
Discovery Level 0        → learn a valid business flow
Code Update Level 0 → 1  → establish baseline, heal changed UI, persist locators
```

---

## How TestZombie is integrated

The central integration is intentionally small.

Normal Selenium:

```java
WebDriver driver = new ChromeDriver(options);
```

With TestZombie:

```java
TestZombieDriver.setCredentials(apiKey, email);
WebDriver driver = TestZombieDriver.createChrome(options);
```

The example centralizes this in:

```text
src/test/java/ai/testzombie/demo/support/DriverFactory.java
```

All three demos use the same Page Objects and `OnboardingScenario`. `BaseSeleniumTest` handles browser setup and teardown, while each demo selects its TestZombie feature through a small override.

Existing `By` locators and regular Selenium Page Objects can otherwise remain in place.

---

## Optional: Persistent Healing modes

The dedicated Code Update scenario above enables `APPLY` by default. The other two scenarios explicitly use `OFF` to avoid accidental source changes.

### Preview changes without modifying files

```bash
mvn test -Dtest=CodeUpdateLevel0And1Test \
  -Dtestzombie.codeupdate.mode=preview
```

TestZombie reports the source updates it would apply but does not modify the files.

### Apply successful healings to source code

```bash
mvn test -Dtest=CodeUpdateLevel0And1Test \
  -Dtestzombie.codeupdate.mode=apply
```

After the run, inspect the changes with:

```bash
git diff -- src/test/java/ai/testzombie/demo/pages
```

### Page Object constants are supported

The example uses ordinary Java Page Object selectors:

```java
private static final By SAVE_OWNER = By.id("saveOwnerButton");

public ProjectPage enterOwner(String email, String role) {
    // ...
    click(SAVE_OWNER);
    return this;
}
```

When a locator is successfully healed, Source Update can replace the declaration while leaving the calling action unchanged. For example:

```java
// private static final By SAVE_OWNER = By.id("saveOwnerButton");
private static final By SAVE_OWNER = By.id("updatedSaveOwnerButton");
```

The replacement above is **illustrative**, not a claim that the demo uses that particular new ID.

### Environment-variable configuration

Outside the dedicated demo, the Java library also supports `TESTZOMBIE_SOURCE_UPDATE_MODE` values `off`, `preview` and `apply`.

**Linux / macOS**

```bash
export TESTZOMBIE_SOURCE_UPDATE_MODE=preview
```

**PowerShell**

```powershell
$env:TESTZOMBIE_SOURCE_UPDATE_MODE="preview"
```

**Windows CMD**

```cmd
set TESTZOMBIE_SOURCE_UPDATE_MODE=preview
```

**Important for this repository:** `DriverFactory` explicitly calls `TestZombieDriver.setSourceUpdateMode(...)` for every test, so the selected test mode takes precedence over the environment variable. For this project's Code Update demo, use `-Dtestzombie.codeupdate.mode=preview` or `apply` as shown above.

---

## Useful commands

| Command | Purpose |
| --- | --- |
| `mvn test -Dtest=OnboardingFlowTest` | Complete five-level onboarding flow in Chrome |
| `mvn test -Dtest=DiscoveryLevel0Test` | Stable Level-0 flow with native Discovery enabled |
| `mvn test -Dtest=CodeUpdateLevel0And1Test` | Level 0 → Level 1 with runtime healing and source-code updates |
| `mvn test -Dtest=CodeUpdateLevel0And1Test -Dtestzombie.codeupdate.mode=preview` | Preview source updates without writing files |
| `mvn test -Dtest=OnboardingFlowTest -Dheadless=true` | Run the Self-Healing flow headless |
| `mvn test -Dtest=OnboardingFlowTest -Dbrowser=firefox` | Self-Healing flow in Firefox |
| `mvn test -Dtest=OnboardingFlowTest -Dbrowser=edge` | Self-Healing flow in Edge |
| `mvn test -Dtest=OnboardingFlowTest -DbaseUrl=https://your-environment.example` | Use another demo base URL (if compatible) |
| `mvn test` | Run **all** tests, including the source-modifying Code Update demo |

The Self-Healing test optionally includes a forced-AI probe at mutation Level 4. Enable it with `-Drun.forced.ai.demo=true` if you explicitly want that extra call; it may consume additional AI credits.

## Project structure

```text
src/test/java/ai/testzombie/demo/
├── pages/
│   ├── AdminPage.java
│   ├── BasePage.java
│   ├── CheckoutPage.java
│   └── ProjectPage.java
├── support/
│   ├── DemoLogger.java
│   ├── DriverFactory.java
│   ├── MutationLevel.java
│   ├── NativeDiscovery.java
│   └── SeleniumActions.java
├── testbase/
│   ├── BaseSeleniumTest.java
│   └── OnboardingScenario.java
└── tests/
    ├── CodeUpdateLevel0And1Test.java
    ├── DiscoveryLevel0Test.java
    └── OnboardingFlowTest.java
```

The shared `OnboardingScenario` executes the same multi-page SaaS onboarding process in all three demos:

```text
Project / owner details
        ↓
project type
        ↓
plan and billing
        ↓
workspace configuration
        ↓
activation and success dialog
```

The demo also disables the random blocking overlay to keep the mutation-level comparison reproducible. Ordinary Selenium waits and interaction retries remain separate from TestZombie's locator-healing behavior.

## Browser support

The project supports:

- Chrome
- Firefox
- Edge

Select the browser with `-Dbrowser=chrome`, `-Dbrowser=firefox`, or `-Dbrowser=edge`. The default is Chrome with a visible window; use `-Dheadless=true` when needed.

## Security

Never commit `TESTZOMBIE_API_KEY`, account credentials or browser session data. Use environment variables, Maven properties supplied securely at runtime, or your CI/CD secret store.

The `pom.xml` keeps credential properties empty by default. If you use **Quick start → Option 1**, enter values only in your local copy and restore the empty values before committing or sharing it.

## More validation

The five mutation levels allow you to distinguish baseline behavior from healing under increasingly altered locators and DOM structures. The three main tests are feature demonstrations, not a complete adapter acceptance or parallel-isolation suite.

For unexpected locator failures, start with Level 0, then move to higher mutation levels and inspect the TestZombie log output. For source-update runs, always review the resulting `git diff` before committing changes.
