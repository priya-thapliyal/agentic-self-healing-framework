package e2e;

import core.TestOrchestrator;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class DashboardTest {

    private WebDriver driver;
    private TestOrchestrator orchestrator;

    @BeforeAll
    static void setupClass() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    void setupTest() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--incognito");
        options.addArguments("--disable-features=PasswordManager");
        options.addArguments("--disable-save-password-bubble");
        options.addArguments("--disable-autofill");
        
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        
        // Initialize the Agentic Orchestrator with 2 seconds timeout for fast failure demo
        orchestrator = new TestOrchestrator(driver, 2);
    }

    @AfterEach
    void teardown() {
        if (driver != null) {
            try { Thread.sleep(3000); } catch (Exception e) {} // Wait 3s so CTO can see success before closing
            driver.quit();
        }
    }

    @Test
    void testSelfHealingE2EFlow() throws InterruptedException {
        System.out.println("==================================================");
        System.out.println("🚀 STARTING E2E SELF-HEALING SHOWCASE 🚀");
        System.out.println("==================================================");

        // 1. Navigate to target application
        driver.get("https://retail-website-two.vercel.app/app/dashboard");
        Thread.sleep(3000); // Wait for React initial load
        
        // ============================================
        // STEP 1: FILL EMAIL (INTENTIONAL FAILURE HERE)
        // ============================================
        System.out.println("\n--- 🔴 STEP 1: EMAIL INPUT (BROKEN SELECTOR) ---");
        System.out.println("Intent: Enter email using old selector 'input.old-email-field'");
        
        orchestrator.resilientFill(By.cssSelector("input.old-email-field"), "test@demo.com", "Email Input", "Test Case 1");
        Thread.sleep(500);

        // ============================================
        // STEP 2: FILL PASSWORD (STABLE DETERMINISTIC)
        // ============================================
        System.out.println("\n--- 🟢 STEP 2: PASSWORD INPUT (STABLE SELECTOR) ---");
        orchestrator.resilientFill(By.xpath("//input[@type='password']"), "password123", "Password Input", "Test Case 2");
        Thread.sleep(500);

        // ============================================
        // STEP 3: CLICK SIGN IN (INTENTIONAL FAILURE)
        // ============================================
        System.out.println("\n--- 🔴 STEP 3: SIGN IN BUTTON (BROKEN SELECTOR) ---");
        orchestrator.resilientClick(By.id("legacy-login-btn-99"), "Sign In Button", "Test Case 3");
        Thread.sleep(6000); // Wait for dashboard to load after login

        // ============================================
        // STEP 4: NAVIGATE TO PRODUCTS (STABLE)
        // ============================================
        System.out.println("\n--- 🟢 STEP 4: CLICK PRODUCTS MENU (STABLE) ---");
        orchestrator.resilientClick(By.xpath("//a[contains(., 'Products')]"), "Products Menu", "Test Case 4");
        Thread.sleep(3000); // Wait for products to load

        // ============================================
        // STEP 5: ADD PRODUCT TO CART (INTENTIONAL FAILURE)
        // ============================================
        System.out.println("\n--- 🔴 STEP 5: ADD TO CART (BROKEN SELECTOR) ---");
        System.out.println("Intent: Click Add to Cart using old ID 'btn-legacy-cart-add-42'");
        orchestrator.resilientClick(By.id("btn-legacy-cart-add-42"), "Add to Cart Button", "Test Case 5");
        Thread.sleep(2000); // Wait to see the item added!

        // ============================================
        // STEP 6: NAVIGATE TO CART (BROKEN SELECTOR)
        // ============================================
        System.out.println("\n--- 🔴 STEP 6: CLICK CART MENU (BROKEN SELECTOR) ---");
        orchestrator.resilientClick(By.cssSelector(".missing-cart-nav"), "Cart Menu", "Test Case 6");
        Thread.sleep(1500);

        // ============================================
        // STEP 7: NAVIGATE TO CHECKOUT (BROKEN SELECTOR)
        // ============================================
        System.out.println("\n--- 🔴 STEP 7: CLICK CHECKOUT MENU (BROKEN SELECTOR) ---");
        orchestrator.resilientClick(By.id("checkout-link-404"), "Checkout Menu", "Test Case 7");
        Thread.sleep(1500);

        // ============================================
        // STEP 8: NAVIGATE TO SETTINGS (BROKEN SELECTOR)
        // ============================================
        System.out.println("\n--- 🔴 STEP 8: CLICK SETTINGS MENU (BROKEN SELECTOR) ---");
        orchestrator.resilientClick(By.xpath("//nav/a[@href='broken-settings']"), "Settings Menu", "Test Case 8");
        Thread.sleep(1500);

        // ============================================
        // STEP 9: NAVIGATE TO DASHBOARD (BROKEN SELECTOR)
        // ============================================
        System.out.println("\n--- 🔴 STEP 9: CLICK DASHBOARD (BROKEN SELECTOR) ---");
        orchestrator.resilientClick(By.cssSelector("li.broken-dash"), "Dashboard Menu", "Test Case 9");
        Thread.sleep(1500);

        // ============================================
        // STEP 10: INTERACT WITH REORDER WIDGETS
        // ============================================
        System.out.println("\n--- 🔴 STEP 10: REORDER WIDGETS (BROKEN SELECTOR) ---");
        orchestrator.resilientClick(By.id("bad-widget-btn"), "Reorder widgets Button", "Test Case 10");
        Thread.sleep(1500);

        // ============================================
        // STEP 11: NAVIGATE PRODUCTS AGAIN (BROKEN SELECTOR)
        // ============================================
        System.out.println("\n--- 🔴 STEP 11: CLICK PRODUCTS MENU AGAIN (BROKEN SELECTOR) ---");
        orchestrator.resilientClick(By.cssSelector("a.fail-products"), "Products Menu", "Test Case 11");
        Thread.sleep(1500);

        // ============================================
        // STEP 12: ADD SECOND PRODUCT (BROKEN SELECTOR)
        // ============================================
        System.out.println("\n--- 🔴 STEP 12: ADD TO CART SECOND PRODUCT (BROKEN SELECTOR) ---");
        orchestrator.resilientClick(By.xpath("//button[@data-test='missing']"), "Add To Cart Button", "Test Case 12");
        Thread.sleep(1500);

        // ============================================
        // STEP 13: NAVIGATE CART AGAIN (BROKEN SELECTOR)
        // ============================================
        System.out.println("\n--- 🔴 STEP 13: CLICK CART AGAIN (BROKEN SELECTOR) ---");
        orchestrator.resilientClick(By.id("nav-cart-err"), "Cart Menu", "Test Case 13");
        Thread.sleep(1500);

        // ============================================
        // STEP 14: NAVIGATE CHECKOUT AGAIN (BROKEN SELECTOR)
        // ============================================
        System.out.println("\n--- 🔴 STEP 14: CLICK CHECKOUT AGAIN (BROKEN SELECTOR) ---");
        orchestrator.resilientClick(By.cssSelector(".checkout-err"), "Checkout Menu", "Test Case 14");
        Thread.sleep(1500);

        // ============================================
        // STEP 15: NAVIGATE SETTINGS AGAIN (BROKEN SELECTOR)
        // ============================================
        System.out.println("\n--- 🔴 STEP 15: CLICK SETTINGS AGAIN (BROKEN SELECTOR) ---");
        orchestrator.resilientClick(By.xpath("//a[@class='fail-setting']"), "Settings Menu", "Test Case 15");
        Thread.sleep(1500);

        // ============================================
        // STEP 16: RETURN TO DASHBOARD FINAL (BROKEN SELECTOR)
        // ============================================
        System.out.println("\n--- 🔴 STEP 16: CLICK DASHBOARD FINAL (BROKEN SELECTOR) ---");
        orchestrator.resilientClick(By.id("dash-err"), "Dashboard Menu", "Test Case 16");
        Thread.sleep(1500);

        // ============================================
        // STEP 17: DASHBOARD METRIC - ORDERS (BROKEN SELECTOR)
        // ============================================
        System.out.println("\n--- 🔴 STEP 17: CLICK ORDERS STATISTIC (BROKEN SELECTOR) ---");
        orchestrator.resilientClick(By.cssSelector(".broken-orders"), "Orders", "Test Case 17");
        Thread.sleep(1500);

        // ============================================
        // STEP 18: DASHBOARD METRIC - ACTIVITY (BROKEN SELECTOR)
        // ============================================
        System.out.println("\n--- 🔴 STEP 18: CLICK ACTIVITY STATISTIC (BROKEN SELECTOR) ---");
        orchestrator.resilientClick(By.id("bad-activity"), "Activity", "Test Case 18");
        Thread.sleep(1500);

        // ============================================
        // STEP 19: DASHBOARD METRIC - REVENUE (BROKEN SELECTOR)
        // ============================================
        System.out.println("\n--- 🔴 STEP 19: CLICK REVENUE STATISTIC (BROKEN SELECTOR) ---");
        orchestrator.resilientClick(By.className("missing-revenue"), "Revenue", "Test Case 19");
        Thread.sleep(1500);

        // ============================================
        // STEP 20: COMMERCE METRIC - REFUNDS (BROKEN SELECTOR)
        // ============================================
        System.out.println("\n--- 🔴 STEP 20: CLICK REFUNDS STATISTIC (BROKEN SELECTOR) ---");
        orchestrator.resilientClick(By.id("missing-refund"), "Refunds", "Test Case 20");
        Thread.sleep(1500);

        // ============================================
        // STEP 21: COMMERCE METRIC - OPERATIONS (BROKEN SELECTOR)
        // ============================================
        System.out.println("\n--- 🔴 STEP 21: CLICK OPERATIONS WIDGET (BROKEN SELECTOR) ---");
        orchestrator.resilientClick(By.xpath("//*[@data-testid='ops']"), "Operations", "Test Case 21");
        Thread.sleep(1500);

        // ============================================
        // STEP 22: NAVIGATE PRODUCTS FINAL (BROKEN SELECTOR)
        // ============================================
        System.out.println("\n--- 🔴 STEP 22: CLICK PRODUCTS FINAL (BROKEN SELECTOR) ---");
        orchestrator.resilientClick(By.cssSelector(".missing-products"), "Products Menu", "Test Case 22");
        Thread.sleep(1500);

        // ============================================
        // STEP 23: ADD THIRD PRODUCT (BROKEN SELECTOR)
        // ============================================
        System.out.println("\n--- 🔴 STEP 23: ADD TO CART THIRD PRODUCT (BROKEN SELECTOR) ---");
        orchestrator.resilientClick(By.xpath("//button[@id='wrong-id']"), "Add To Cart Button", "Test Case 23");
        Thread.sleep(1500);

        // ============================================
        // STEP 23.5: RETURN TO DASHBOARD
        // ============================================
        System.out.println("\n--- 🔴 STEP 23.5: RETURN TO DASHBOARD (BROKEN SELECTOR) ---");
        orchestrator.resilientClick(By.id("missing-dash-link"), "Dashboard Menu", "Test Case 23.5");
        Thread.sleep(1500);

        // ============================================
        // STEP 24: REORDER WIDGETS FINAL (BROKEN SELECTOR)
        // ============================================
        System.out.println("\n--- 🔴 STEP 24: REORDER WIDGETS FINAL (BROKEN SELECTOR) ---");
        orchestrator.resilientClick(By.xpath("//button[@class='bad-reorder']"), "Reorder widgets Button", "Test Case 24");
        Thread.sleep(1500);

        // ============================================
        // STEP 25: SIGN OUT LOGOUT FLOW (BROKEN SELECTOR)
        // ============================================
        System.out.println("\n--- 🔴 STEP 25: SIGN OUT BUTTON (BROKEN SELECTOR) ---");
        orchestrator.resilientClick(By.id("logout-broken"), "Sign out Button", "Test Case 25");
        Thread.sleep(3000); // Verify logout route
        
        System.out.println("\n--- ✅ FULL 25-STEP E2E TEST PASSED ---");
        System.out.println("The script completed successfully despite 20+ broken selectors sequentially handled without human intervention!");
        System.out.println("==================================================");
    }
}
