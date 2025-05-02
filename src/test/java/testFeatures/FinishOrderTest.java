package testFeatures;

import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import utils.BrowserTypeEnum;

import static io.qameta.allure.Allure.step;

/**
 * ✅ Final E2E test case to verify finishing the order, across all major browsers.
 *
 * 🔁 Uses BaseTest to set up Playwright browser, context, and video recording.
 * 🌍 Each test is parameterized to run with Chromium, Firefox, and WebKit.
 * 🧭 Follows full journey: Login → Add Items → Checkout → Verify Prices → Finish Order.
 * 📹 Attaches videos and screenshots to Allure report.
 * 📁 Renames video folder after test completion based on result (PASS/FAIL).
 */
@Epic("E2E Tests")
@Feature("Finish Order")
@DisplayName("✅ Finish Order Test (Cross-Browser)")
public class FinishOrderTest extends BaseTest {

    @ParameterizedTest(name = "Finish Order test on {0}")
    @EnumSource(BrowserTypeEnum.class)
    @AllureId("ORDER-001")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verifies that the user can complete an order and see confirmation across browsers")
    @DisplayName("testFinishOrder")
    void testFinishOrder(BrowserTypeEnum browserType) {
        boolean passed = false; // 🧠 Track test status manually

        try {
            // 🌍 Set the browser type dynamically for this run
            setCurrentBrowser(browserType);

            // 🚀 Launch the browser with video/context configuration
            launchBrowser();

            // 🌐 Open the homepage
            step("🌐 Navigate to the homepage using: " + browserType);
            page.navigate("https://www.saucedemo.com/");

            // 🔐 Perform login
            step("🔐 Login with standard_user");
            steps.login();

            // 🛒 Add products to cart
            step("🛒 Add items to shopping cart");
            steps.addItemsToCart();

            // 🧾 Go through checkout process
            step("🧾 Proceed to checkout and fill personal info");
            steps.checkoutStepOne();

            // 💰 Check price breakdown
            step("💰 Verify subtotal, tax, and total prices");
            steps.verifyPrices();

            // ✅ Finish the order
            step("✅ Click 'Finish' and verify confirmation message");
            steps.finishOrder();

            System.out.println("🟢 Order finished successfully on " + browserType);
            passed = true;

        } catch (Exception e) {
            System.err.println("🔴 Finish order test failed on " + browserType + ": " + e.getMessage());
            throw e; // Rethrow to ensure failure is logged
        } finally {
            // 🧾 Inject visual result banner into the test video
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
                Thread.sleep(2000); // Wait so banner appears in video
            } catch (Exception ex) {
                System.err.println("⚠️ Could not display result banner: " + ex.getMessage());
            }

            // ✅ Save test result and close browser
            setTestStatus(passed ? "PASSED" : "FAILED");
            closeBrowser();
        }
    }
}