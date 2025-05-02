package testFeatures;

import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import utils.BrowserTypeEnum;

import static io.qameta.allure.Allure.step;

/**
 * ✅ This is a parameterized E2E login test that runs on all major browsers.
 * It uses a dynamic browser instance provided by JUnit's @EnumSource.
 * Each test run uses a different browser (Chromium, Firefox, WebKit).
 */
@Epic("E2E Tests")
@Feature("Login")
@DisplayName("🔐 Login Test (Cross-Browser)")
public class LoginTest extends BaseTest {

    /**
     * 🧪 This test runs once per browser type (Chromium, Firefox, WebKit)
     * If you enable parallel test execution in Maven, they will run in parallel.
     */
    @ParameterizedTest(name = "Login test on {0}")
    @EnumSource(BrowserTypeEnum.class)
    @AllureId("LOGIN-001")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Verifies that the login process works correctly across browsers")
    @DisplayName("testValidLogin")
    void testValidLogin(BrowserTypeEnum browserType) {
        boolean passed = false; // 🧠 Track test result manually

        try {
            // 🌍 Set the browser for this run
            setCurrentBrowser(browserType);

            // 🚀 Launch the correct browser (Chromium, Firefox, WebKit)
            launchBrowser();

            step("🌐 Navigate to login page using: " + browserType);
            page.navigate("https://www.saucedemo.com/");

            step("🔐 Perform login using valid credentials");
            steps.login(); // Username: standard_user, Password: secret_sauce

            step("✅ Verify successful login by checking inventory URL");
            if (!page.url().contains("inventory")) {
                throw new RuntimeException("Login did not reach inventory page!");
            }

            System.out.println("🟢 Login test passed on " + browserType);
            passed = true;

        } catch (Exception e) {
            System.err.println("🔴 Login test failed on " + browserType + ": " + e.getMessage());
            throw e; // Rethrow to fail test
        } finally {
            // ✅ Inject result into the browser for video visibility
            String resultText = passed ? "✅ TEST PASSED" : "❌ TEST FAILED";
            String color = passed ? "green" : "red";

            try {
                page.evaluate("result => {" +
                        "const banner = document.createElement('div');" +
                        "banner.textContent = result;" +
                        "banner.style.position = 'fixed';" +
                        "banner.style.top = '40%';" +
                        "banner.style.left = '20%';" +
                        "banner.style.fontSize = '48px';" +
                        "banner.style.color = '" + color + "';" +
                        "banner.style.backgroundColor = '#000000cc';" +
                        "banner.style.padding = '20px';" +
                        "banner.style.zIndex = '9999';" +
                        "document.body.appendChild(banner);" +
                        "}", resultText);

                Thread.sleep(2000); // 🎥 Let it show in video

            } catch (Exception ex) {
                System.err.println("⚠️ Could not display result banner: " + ex.getMessage());
            }

            // 🧹 Cleanup
            setTestStatus(passed ? "PASSED" : "FAILED");
            closeBrowser();
        }
    }
}
