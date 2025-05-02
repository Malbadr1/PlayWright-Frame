package testFeatures;

import com.microsoft.playwright.*;
import com.microsoft.playwright.BrowserType.LaunchOptions;
import io.qameta.allure.Allure;
import org.junit.jupiter.api.*;
import service.ShoppingRightwayE2ESteps;
import utils.BrowserTypeEnum;

import java.io.File;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 🤪 BaseTest is a reusable class that sets up and tears down the Playwright test environment.
 * It:
 * ✅ Launches a browser (Chromium, Firefox, WebKit)
 * ✅ Starts a context and page for each test
 * ✅ Records video of each test session
 * ✅ Attaches video and screenshots to Allure reports
 * ✅ Renames the video directory based on test result (PASS/FAIL/UNKNOWN)
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BaseTest {

    protected Playwright playwright;
    protected Browser browser;
    protected BrowserContext context;
    protected Page page;
    protected ShoppingRightwayE2ESteps steps;

    protected Path videoDirPath;
    protected String baseVideoFolderName;
    protected String testStatus = "UNKNOWN"; // Default test status
    protected BrowserTypeEnum currentBrowserType = BrowserTypeEnum.CHROMIUM; // Default browser type

    /**
     * 🚀 Setup executed before each test.
     * It launches the browser, creates a unique folder for videos,
     * starts a browser context with video recording, and opens a new page.
     */
    @BeforeEach
    void setup(TestInfo testInfo) {
        try {
            System.out.println("\n🚀 Launching browser (" + currentBrowserType + ")...");
            playwright = Playwright.create();
            browser = launchBrowser(playwright, currentBrowserType);

            // Generate unique folder name for video storage
            String testName = testInfo.getDisplayName().replaceAll("[^a-zA-Z0-9]", "_");
            String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
            baseVideoFolderName = testName + "__" + timestamp;
            videoDirPath = Paths.get("videos", baseVideoFolderName);
            Files.createDirectories(videoDirPath);

            // Start browser context with video recording enabled
            context = browser.newContext(new Browser.NewContextOptions()
                    .setRecordVideoDir(videoDirPath)
                    .setRecordVideoSize(1280, 720));
            page = context.newPage();

            // Initialize test steps instance with current page
            steps = new ShoppingRightwayE2ESteps(page);

            System.out.println("🎥 Recording video to: " + videoDirPath);

        } catch (Exception e) {
            System.err.println("❌ Error during setup: " + e.getMessage());
        }
    }

    /**
     * 🧹 Cleanup executed after each test.
     * It captures a screenshot, attaches video/screenshot to Allure,
     * closes the browser and context, and renames the video folder based on result.
     */
    @AfterEach
    void cleanup(TestInfo testInfo) {
        try {
            // 📸 Capture screenshot for report
            Path screenshotPath = Paths.get("target/screenshots", testInfo.getDisplayName() + ".png");
            page.screenshot(new Page.ScreenshotOptions().setPath(screenshotPath).setFullPage(true));
            Allure.addAttachment("📸 Screenshot", "image/png",
                    Files.newInputStream(screenshotPath), ".png");

            // 🎬 Attach test video to report
            Path videoPath = page.video().path();
            if (videoPath != null && videoPath.toFile().exists()) {
                Allure.addAttachment("🎬 Video", "video/webm",
                        Files.newInputStream(videoPath), ".webm");
            }

        } catch (Exception e) {
            System.err.println("❌ Error attaching artifacts: " + e.getMessage());
        } finally {
            try {
                Thread.sleep(2000); // Wait for video finalization
            } catch (InterruptedException ignored) {}

            if (context != null) context.close();

            try {
                if (browser != null && browser.isConnected()) browser.close();
            } catch (Exception e) {
                System.err.println("⚠️ Browser close failed: " + e.getMessage());
            }

            try {
                if (playwright != null) playwright.close();
            } catch (Exception e) {
                System.err.println("⚠️ Playwright close failed: " + e.getMessage());
            }

            // Rename video folder with test result
            String suffix = switch (testStatus) {
                case "PASSED" -> "_PASS";
                case "FAILED" -> "_FAIL";
                default -> "_UNKNOWN";
            };
            try {
                Path renamed = Paths.get("videos", baseVideoFolderName + suffix);
                Files.move(videoDirPath, renamed, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("📁 Renamed video folder to: " + renamed.getFileName());
            } catch (Exception e) {
                System.err.println("⚠️ Could not rename folder: " + e.getMessage());
            }
        }
    }

    /**
     * ✅ Used to set the result status from within test (PASS/FAIL)
     */
    public void setTestStatus(String status) {
        this.testStatus = status;
    }

    /**
     * ✅ Used to switch the browser type dynamically per test
     */
    public void setCurrentBrowser(BrowserTypeEnum browserType) {
        this.currentBrowserType = browserType;
    }

    /**
     * 🚀 Launch the browser based on enum type (Chromium, Firefox, WebKit)
     */
    public static Browser launchBrowser(Playwright pw, BrowserTypeEnum type) {
        return switch (type) {
            case CHROMIUM -> pw.chromium().launch(new LaunchOptions().setHeadless(false).setSlowMo(300));
            case FIREFOX -> pw.firefox().launch(new LaunchOptions().setHeadless(false).setSlowMo(300));
            case WEBKIT -> pw.webkit().launch(new LaunchOptions().setHeadless(false).setSlowMo(300));
        };
    }

    /**
     * ✅ Optional method to launch browser manually (outside test lifecycle)
     */
    public void launchBrowser() {
        System.out.println("🚀 Launching browser manually: " + currentBrowserType);
        playwright = Playwright.create();
        browser = launchBrowser(playwright, currentBrowserType);
    }

    /**
     * ✅ Optional method to close browser with connection safety check
     */
    public void closeBrowser() {
        System.out.println("🧹  closing browser and Playwright...");

        try {
            if (browser != null && browser.isConnected()) {
                browser.close();
            } else {
                System.out.println("ℹ️ Browser already disconnected or null.");
            }
        } catch (Exception e) {
            System.err.println("⚠️ Browser close failed: " + e.getMessage());
        }

        try {
            if (playwright != null) {
                playwright.close();
            }
        } catch (Exception e) {
            System.err.println("⚠️ Playwright close failed: " + e.getMessage());
        }
    }
}