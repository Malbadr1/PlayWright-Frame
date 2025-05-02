package testFeatures;

import com.microsoft.playwright.Page;
import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import utils.BrowserTypeEnum;

import static io.qameta.allure.Allure.step;

/**
 * 📋 Cross-browser E2E test to verify the checkout step one form functionality.
 *
 * ✅ Launches browser based on @EnumSource (Chromium, Firefox, WebKit)
 * ✅ Records video of the test session and saves it to /videos
 * ✅ Simulates full user flow: Login → Add to Cart → Start Checkout → Fill Form
 * ✅ Shows result banner and renames video folder based on outcome
 * ✅ Sets test status and attaches artifacts (screenshots, video)
 * ⚠️ Known issue: Manual browser close may trigger "Playwright connection closed" warning
 */
@Epic("E2E Tests")
@Feature("Checkout Step One")
@DisplayName("📋 Checkout Step One Test")
public class CheckoutFlowTest extends BaseTest {

    @ParameterizedTest(name = "Checkout test on {0}")
    @EnumSource(BrowserTypeEnum.class)
    @AllureId("CHECKOUT-001")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that the first step of the checkout form works correctly across browsers")
    @DisplayName("testCheckoutStepOne")
    void testCheckoutStepOne(BrowserTypeEnum browserType) {
        boolean passed = false; // Used to track and set test status manually

        try {
            // 🌍 Set the current browser type before launch
            setCurrentBrowser(browserType);

            // 🚀 Launch browser manually
            launchBrowser();

            // 🌐 Open the homepage
            step("🌐 Navigate to homepage (https://www.saucedemo.com)");
            page.navigate("https://www.saucedemo.com/");

            // 🔐 Perform login using standard credentials
            step("🔐 Perform login with standard credentials");
            steps.login();

            // 🛒 Add products to the cart
            step("🛒 Add products to the shopping cart");
            steps.addItemsToCart();
            System.out.println("🟢 Items added to cart successfully on " + browserType);

            // 🧾 Proceed to checkout page
            step("🧾 Click checkout and navigate to step one form");
            steps.clickCheckout();

            // ⏳ Wait for URL to match checkout step one page
            step("⏳ Wait for checkout-step-one URL to load");
            page.waitForURL("**/checkout-step-one.html", new Page.WaitForURLOptions().setTimeout(5000));

            // 🔎 Wait for the form to appear
            step("🔎 Ensure checkout form is visible");
            page.waitForSelector("#first-name", new Page.WaitForSelectorOptions().setTimeout(3000));
            if (!page.locator("#first-name").isVisible()) {
                throw new RuntimeException("Checkout form is not visible");
            }

            // ✍️ Fill out the form
            step("✍️ Fill out checkout form fields");
            steps.fillFirstName();
            steps.fillLastName();
            steps.fillPostalCode();

            // ➡️ Continue to step two
            step("➡️ Submit form and validate step two URL");
            steps.clickContinue();
            steps.verifyStepTwoUrl();

            passed = true;
            System.out.println("🟢 Checkout step one completed successfully on " + browserType);

        } catch (Exception e) {
            System.err.println("🔴 Checkout flow failed on " + browserType + ": " + e.getMessage());
            throw e;

        } finally {
            // 🎥 Inject result banner into video for visual clarity
            try {
                String resultText = passed ? "✅ TEST PASSED" : "❌ TEST FAILED";
                String color = passed ? "green" : "red";
                page.evaluate("result => { const banner = document.createElement('div'); banner.textContent = result; banner.style.position = 'fixed'; banner.style.top = '40%'; banner.style.left = '20%'; banner.style.fontSize = '48px'; banner.style.color = '" + color + "'; banner.style.backgroundColor = '#000000cc'; banner.style.padding = '20px'; banner.style.zIndex = '9999'; document.body.appendChild(banner); }", resultText);
                Thread.sleep(2000); // Allow time for banner to be recorded
            } catch (Exception ignored) {}

            // ✅ Set status for video folder renaming
            setTestStatus(passed ? "PASSED" : "FAILED");

            // 🧹 Attempt to close browser and Playwright manually
            closeBrowser();
        }
    }
}
