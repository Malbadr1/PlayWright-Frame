package testFeatures;

import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import utils.BrowserTypeEnum;

import static io.qameta.allure.Allure.step;

/**
 * 🛒 E2E test to verify adding items to the shopping cart.
 * This test runs across all major browsers using Playwright + JUnit + Allure.
 */
@Epic("E2E Tests")
@Feature("Cart")
@DisplayName("🛒 Add Items to Cart Test (Cross-Browser)")
public class AddItemsToCartTest extends BaseTest {

    /**
     * This test verifies that users can successfully add items to the cart
     * after logging in. It runs once per browser type (Chromium, Firefox, WebKit).
     */
    @ParameterizedTest(name = "Add items to cart on {0}")
    @EnumSource(BrowserTypeEnum.class)
    @AllureId("CART-002")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Ensures that adding items to cart works across all browsers after login")
    @DisplayName("testAddItemsToCart")
    void testAddItemsToCart(BrowserTypeEnum browserType) {
        boolean passed = false; // Track test result manually for reporting

        try {
            // Set the current browser type for this test iteration
            setCurrentBrowser(browserType);

            // Launch the browser using BaseTest utility
            launchBrowser();

            // Navigate to the login page
            step("🌐 Navigate to login page using: " + browserType);
            page.navigate("https://www.saucedemo.com/");

            // Log in using valid test credentials (standard_user/secret_sauce)
            step("🔐 Login with valid credentials");
            steps.login();

            // Add multiple items to the cart using ShoppingRightwayE2ESteps
            step("🛒 Add items to cart");
            steps.addItemsToCart();

            // Click the cart icon to go to the cart page
            step("📦 Verify items are in the cart");
            page.click(".shopping_cart_link");

            // Validate that the current URL is the cart page
            String cartUrl = page.url();
            if (!cartUrl.contains("cart")) {
                throw new RuntimeException("🛑 Did not reach cart page!");
            }

            // Validate that at least one item is present in the cart
            int itemCount = page.locator(".cart_item").count();
            if (itemCount < 1) {
                throw new RuntimeException("🛑 No items found in the cart!");
            }

            // Log success to console
            System.out.println("🟢 Items added to cart successfully on " + browserType);
            passed = true;

        } catch (Exception e) {
            // Print error details to console and rethrow to mark test as failed
            System.err.println("🔴 Add to cart test failed on " + browserType + ": " + e.getMessage());
            throw e;
        } finally {
            // Show result in the recorded video for visibility
            String resultText = passed ? "✅ TEST PASSED" : "❌ TEST FAILED";
            String color = passed ? "green" : "red";

            try {
                // Inject a result banner into the page for video reporting
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

                // Pause briefly so the result banner appears in the video
                Thread.sleep(2000);
            } catch (Exception ex) {
                System.err.println("⚠️ Could not display result banner: " + ex.getMessage());
            }

            // Set the test result status for video folder renaming
            setTestStatus(passed ? "PASSED" : "FAILED");

            // Close browser and release resources
            closeBrowser();
        }
    }
}
