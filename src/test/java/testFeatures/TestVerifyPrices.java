package testFeatures;

import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import utils.BrowserTypeEnum;

import static io.qameta.allure.Allure.step;

/**
 * 💰 Parameterized E2E test to verify price calculations on all browsers.
 *
 * ✅ Runs the test on Chromium, Firefox, and WebKit using @EnumSource
 * ✅ Verifies subtotal, tax, and total values on the checkout overview page
 * ✅ Automatically records video and renames folder based on result
 * ✅ Gracefully handles browser closure and logs test status
 */
@Epic("E2E Tests")
@Feature("Verify Prices")
@DisplayName("💰 Verify Prices Test (Cross-Browser)")
public class TestVerifyPrices extends BaseTest {

    @ParameterizedTest(name = "Verify prices on {0}")
    @EnumSource(BrowserTypeEnum.class)
    @AllureId("PRICE-001")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Ensure subtotal, tax, and total are calculated correctly across browsers")
    @DisplayName("testVerifyPrices")
    void testVerifyPrices(BrowserTypeEnum browserType) {
        boolean passed = false; // 📊 Used to track final result

        try {
            // 🌍 Set the browser for this specific test run
            setCurrentBrowser(browserType);
            launchBrowser();

            // 🌐 Navigate to the homepage and perform login
            step("🌐 Navigate to homepage using " + browserType);
            page.navigate("https://www.saucedemo.com/");

            step("🔐 Login using valid credentials");
            steps.login();

            // 🛒 Add products and proceed to checkout overview
            step("🛒 Add items to cart and continue to checkout overview");
            steps.addItemsToCart();
            steps.checkoutStepOne();

            // 💵 Verify pricing accuracy (subtotal + tax = total)
            step("💵 Check subtotal, tax, and total consistency");
            steps.verifyPrices();

            System.out.println("🟢 Price verification successful on " + browserType);
            passed = true;

        } catch (Exception e) {
            System.err.println("🔴 Price verification failed on " + browserType + ": " + e.getMessage());
            throw e;
        } finally {
            // 🏁 Inject test result into the page to display in video
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
                Thread.sleep(2000); // 🎥 Ensure banner is visible in video

            } catch (Exception ex) {
                System.err.println("⚠️ Could not display result banner: " + ex.getMessage());
            }

            // 🧹 Clean up browser session and report status
            setTestStatus(passed ? "PASSED" : "FAILED");
            closeBrowser();
        }
    }
}