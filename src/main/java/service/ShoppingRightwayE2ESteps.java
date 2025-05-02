package service;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
import io.qameta.allure.Step;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class ShoppingRightwayE2ESteps {
    private final Page page;

    public ShoppingRightwayE2ESteps(Page page) {
        this.page = page;
    }

    @Step("Login with standard_user account")
    public void login() {
        typeUsername();
        typePassword();
        clickLogin();
        verifyLoginSuccess();
    }

    @Step("Type username")
    public void typeUsername() {
        page.getByPlaceholder("Username").type("standard_user");
    }

    @Step("Type password")
    public void typePassword() {
        page.getByPlaceholder("Password").type("secret_sauce");
    }

    @Step("Click login button")
    public void clickLogin() {
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Login")).click();
    }

    @Step("Verify successful login")
    public void verifyLoginSuccess() {
        assertThat(page).hasURL("https://www.saucedemo.com/inventory.html");
        assertThat(page.locator("[data-test='title']")).hasText("Products");
    }

    @Step("Add 3 products to cart")
    public void addItemsToCart() {
        addBackpack();
        addOnesie();
        addRedShirt();
        openCart();
        verifyCartCount();
        verifyCartTitle();
    }

    @Step("Add backpack to cart")
    public void addBackpack() {
        page.locator("#add-to-cart-sauce-labs-backpack").click();
    }

    @Step("Add onesie to cart")
    public void addOnesie() {
        page.locator("#add-to-cart-sauce-labs-onesie").click();
    }

    @Step("Add red t-shirt to cart")
    public void addRedShirt() {
        page.locator("#add-to-cart-test\\.allthethings\\(\\)-t-shirt-\\(red\\)").click();
    }

    @Step("Open the shopping cart")
    public void openCart() {
        page.locator("#shopping_cart_container a").click();
    }

    @Step("Verify cart has 3 items")
    public void verifyCartCount() {
        assertThat(page.locator(".shopping_cart_badge")).hasText("3");
    }

    @Step("Verify cart title is 'Your Cart'")
    public void verifyCartTitle() {
        assertThat(page.locator("span.title")).containsText("Your Cart");
    }

    @Step("Fill customer info and continue to checkout step 2")
    public void checkoutStepOne() {

        clickCheckout();
        fillFirstName();
        fillLastName();
        fillPostalCode();
        clickContinue();
        verifyStepTwoUrl();
    }

    @Step("Click 'Checkout'")
    public void clickCheckout() {
        page.locator("#checkout").click();
    }

    @Step("Fill in first name")
    public void fillFirstName() {
        page.getByPlaceholder("First Name").type("mohanad");
    }

    @Step("Fill in last name")
    public void fillLastName() {
        page.getByPlaceholder("Last Name").type("al badri");
    }

    @Step("Fill in postal code")
    public void fillPostalCode() {
        page.getByPlaceholder("Zip/Postal Code").type("1030");
    }

    @Step("Click 'Continue'")
    public void clickContinue() {
        page.locator("#continue").click();
    }

    @Step("Verify navigated to checkout step 2")
    public void verifyStepTwoUrl() {
        assertThat(page).hasURL("https://www.saucedemo.com/checkout-step-two.html");
    }

    @Step("Verify item prices, tax and total")
    public void verifyPrices() {
        checkItemTotal();
        checkTax();
        checkTotal();
    }

    @Step("Check item total is $53.97")
    public void checkItemTotal() {
        assertThat(page.locator("[data-test='subtotal-label']")).containsText("$53.97");
    }

    @Step("Check tax is $4.32")
    public void checkTax() {
        assertThat(page.locator("[data-test='tax-label']")).containsText("$4.32");
    }

    @Step("Check total is $58.29")
    public void checkTotal() {
        assertThat(page.locator("[data-test='total-label']")).containsText("$58.29");
    }

    @Step("Finish the checkout process")
    public void finishOrder() {
        clickFinish();
        verifyConfirmation();
        goBackToProducts();
    }

    @Step("Click 'Finish'")
    public void clickFinish() {
        page.locator("#finish").click();
    }

    @Step("Verify confirmation page")
    public void verifyConfirmation() {
        assertThat(page.locator("span.title")).containsText("Checkout: Complete!");
        assertThat(page.locator("#checkout_complete_container h2")).containsText("Thank you for your order!");
    }

    @Step("Click 'Back to Products'")
    public void goBackToProducts() {
        page.locator("#back-to-products").click();
        assertThat(page.locator("[data-test='title']")).hasText("Products");
    }
}
