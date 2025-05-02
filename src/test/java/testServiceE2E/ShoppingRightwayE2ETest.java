package testServiceE2E;

import com.microsoft.playwright.*;
import io.qameta.allure.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;
import service.ShoppingRightwayE2ESteps;

import java.io.File;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

import static io.qameta.allure.Allure.step;

@Epic("E2E Tests")
@Feature("Shopping Flow with Service Class")
@DisplayName("🛍️ Full Shopping Flow Test")
public class ShoppingRightwayE2ETest implements TestWatcher {

    static Playwright playwright;
    static Browser browser;

    BrowserContext context;
    Page page;
    ShoppingRightwayE2ESteps steps;
    String testStatus = "UNKNOWN";
    Path videoDirPath;
    String baseVideoFolderName;

    /**
     * 🚀 This method runs once before all tests.
     * It launches a Chromium browser using Playwright.
     */
    @BeforeAll
    static void setupBrowser() {
        System.out.println("\n🚀 Launching Chromium browser...");
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                .setHeadless(false)     // Set to true to run in headless mode
                .setSlowMo(300));       // Slow down execution for debugging
    }

    /**
     * 🧹 This method runs once after all tests.
     * It closes the browser and cleans up Playwright.
     */
    @AfterAll
    static void closeBrowser() {
        System.out.println("🧹 Closing browser and shutting down Playwright...");
        if (browser != null) browser.close();
        if (playwright != null) playwright.close();
    }

    /**
     * 🎥 This method runs before each test.
     * It creates a browser context with video recording enabled.
     */
    @BeforeEach
    void setupTestContext(TestInfo testInfo) {
        // Create a unique folder name for storing the video
        String testName = testInfo.getDisplayName().replaceAll("[^a-zA-Z0-9]", "_");
        String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
        baseVideoFolderName = testName + "__" + timestamp;

        // Create a videos directory for this test
        videoDirPath = Paths.get("videos", baseVideoFolderName);
        new File(videoDirPath.toUri()).mkdirs();

        // Start a new browser context and page with video recording
        context = browser.newContext(new Browser.NewContextOptions()
                .setRecordVideoDir(videoDirPath)
                .setRecordVideoSize(1280, 720));
        page = context.newPage();

        // Initialize the service steps
        steps = new ShoppingRightwayE2ESteps(page);

        System.out.println("🎥 Test video will be saved in: " + videoDirPath);
    }

    /**
     * 📎 This method runs after each test.
     * It attaches the final screenshot and video to Allure,
     * then renames the video folder based on the test result (PASS or FAIL).
     */
    @AfterEach
    void attachArtifactsAndClose(TestInfo testInfo) {
        try {
            // 📸 Take a full-page screenshot
            Path screenshotPath = Paths.get("target/screenshots", testInfo.getDisplayName() + ".png");
            page.screenshot(new Page.ScreenshotOptions()
                    .setPath(screenshotPath)
                    .setFullPage(true));
            Allure.addAttachment("📸 Final Screenshot", "image/png",
                    screenshotPath.toFile().toURI().toString(), ".png");

            // 🎬 Attach video to Allure if available
            Path videoPath = page.video().path();
            if (videoPath != null && videoPath.toFile().exists()) {
                Allure.addAttachment("🎞️ Test Video", "video/webm",
                        videoPath.toFile().toURI().toString(), ".webm");
            }

        } catch (Exception e) {
            System.err.println("❌ Error while saving artifacts: " + e.getMessage());
        } finally {
            if (context != null) context.close();

            // ✅ Rename the video folder based on testStatus
            String suffix;
            if ("FAILED".equals(testStatus)) {
                suffix = "_FAIL";
            } else if ("PASSED".equals(testStatus)) {
                suffix = "_PASS";
            } else {
                suffix = "_UNKNOWN";
            }

            try {
                Path renamed = Paths.get("videos", baseVideoFolderName + suffix);
                Files.move(videoDirPath, renamed, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("📁 Video folder renamed to: " + renamed.getFileName());
            } catch (Exception e) {
                System.err.println("⚠️ Could not rename folder: " + e.getMessage());
            }
        }
    }

    /**
     * ✅ This is the main test that simulates a full shopping flow.
     * It logs all actions to Allure and sets the testStatus for video naming.
     */
    @Test
    @AllureId("SWAG-001")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Complete checkout flow using service class with step logging")
    @DisplayName("testCompleteCheckoutFlow")
    void testCompleteCheckoutFlow(TestInfo testInfo) {
        boolean passed = false; // 🧠 Local flag to capture test result
        try {
            step("🌐 Navigating to https://www.saucedemo.com/");
            System.out.println("🌐 Opening site...");
            page.navigate("https://www.saucedemo.com/");

            step("🔐 Login step");
            steps.login();

            step("🛒 Add products to cart");
            steps.addItemsToCart();

            step("📋 Fill checkout information");
            steps.checkoutStepOne();

            step("💰 Verify prices and totals");
            steps.verifyPrices();

            step("✅ Finish order and verify confirmation");
            steps.finishOrder();

            System.out.println("🟢 SUCCESS: Full shopping flow completed.");
            passed = true;
        } catch (Exception e) {
            System.err.println("🔴 ERROR in full shopping flow: " + e.getMessage());
            throw e; // Rethrow so JUnit marks it as failed
        } finally {
            // 🔁 Set testStatus based on whether an exception occurred
            testStatus = passed ? "PASSED" : "FAILED";
        }
    }

    // 🔍 Optional TestWatcher callbacks (not required for renaming logic anymore)
    @Override
    public void testSuccessful(ExtensionContext context) {
        System.out.println("🟢 testSuccessful() callback");
    }

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        System.out.println("🔴 testFailed() callback");
    }

    @Override
    public void testAborted(ExtensionContext context, Throwable cause) {
        System.out.println("🟡 testAborted() callback");
    }

    @Override
    public void testDisabled(ExtensionContext context, Optional<String> reason) {
        System.out.println("⚪ testDisabled() callback");
    }
}
