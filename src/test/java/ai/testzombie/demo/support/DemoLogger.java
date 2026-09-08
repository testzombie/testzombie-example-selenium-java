package ai.testzombie.demo.support;

import org.openqa.selenium.By;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

public final class DemoLogger {
    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
    private static final AtomicInteger STEP = new AtomicInteger();
    private static Instant startedAt;

    private DemoLogger() {}

    public static void startTest(String testName, String baseUrl, String browser, boolean headless) {
        STEP.set(0);
        startedAt = Instant.now();
        line();
        System.out.println(" TestZombie Selenium Demo");
        line();
        info("TEST", testName);
        info("URL", baseUrl);
        info("BROWSER", browser + (headless ? " (headless)" : " (visible)"));
        line();
    }

    public static void step(String description) {
        System.out.printf("%n[%s] STEP %02d  %s%n", timestamp(), STEP.incrementAndGet(), description);
    }

    public static void action(String action, By locator) {
        System.out.printf("[%s] ACTION   %-12s %s%n", timestamp(), action, locator);
    }

    public static void action(String action, By locator, String value) {
        System.out.printf("[%s] ACTION   %-12s %s | value=%s%n", timestamp(), action, locator, value);
    }

    public static void navigation(String target) {
        System.out.printf("[%s] NAVIGATE %s%n", timestamp(), target);
    }

    public static void pass(String message) {
        System.out.printf("[%s] PASS     %s%n", timestamp(), message);
    }

    public static void verify(String description, Object expected, Object actual) {
        System.out.printf("[%s] VERIFY   %s | expected=%s | actual=%s%n",
                timestamp(), description, expected, actual);
    }

    public static void info(String label, Object value) {
        System.out.printf("[%s] %-8s %s%n", timestamp(), label, value);
    }

    public static void failure(String message, Throwable error) {
        System.out.printf("[%s] FAIL     %s | %s: %s%n", timestamp(), message,
                error.getClass().getSimpleName(), error.getMessage());
    }

    public static void finish(boolean success) {
        long millis = startedAt == null ? 0 : Duration.between(startedAt, Instant.now()).toMillis();
        line();
        System.out.printf(" RESULT: %s | duration=%.2f s%n", success ? "SUCCESS" : "FAILED", millis / 1000.0);
        line();
    }

    private static String timestamp() {
        return LocalTime.now().format(TIME);
    }

    private static void line() {
        System.out.println("============================================================");
    }
}
