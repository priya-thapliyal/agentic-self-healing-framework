package e2e;

import core.TestOrchestrator;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

/**
 * 20-Step AI Self-Healing Regression Suite
 * Demonstrates the AI's ability to natively heal 20 consecutive broken interactions 
 * without human intervention, specifically designed for CI/CD QA Dashboards.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ShowcaseRegressionTest {

    private static WebDriver driver;
    private static TestOrchestrator orchestrator;

    @BeforeAll
    static void setupSuite() throws InterruptedException {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        // options.addArguments("--headless");
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        
        // Initialize Orchestrator
        orchestrator = new TestOrchestrator(driver, 2);
        
        System.out.println("🚀 [CI/CD] Bootstrapping 20-Step Regression Suite");
        driver.get("https://retail-website-two.vercel.app/app/dashboard");
        Thread.sleep(4000); // Wait for initial app mount
    }

    @AfterAll
    static void teardownSuite() throws InterruptedException {
        if (driver != null) {
            Thread.sleep(3000); // Pause so CTO can see final state
            driver.quit();
        }
    }

    // ==========================================
    // PHASE 1: AUTHENTICATION HEALING
    // ==========================================

    @Test @Order(1)
    void test01_HealEmailField() {
        orchestrator.resilientFill(By.id("broken-email-99"), "test@demo.com", "Email Input", "TC-01");
    }

    @Test @Order(2)
    void test02_HealPasswordField() {
        orchestrator.resilientFill(By.id("password-broken-404"), "password123", "Password Input", "TC-02");
    }

    @Test @Order(3)
    void test03_HealSignInButton() throws InterruptedException {
        orchestrator.resilientClick(By.cssSelector(".fake-login-btn"), "Sign In Button", "TC-03");
        Thread.sleep(6000); // Wait for dashboard routing
    }

    // ==========================================
    // PHASE 2: PRIMARY NAVIGATION & E-COMMERCE
    // ==========================================

    @Test @Order(4)
    void test04_HealProductsTab() {
        orchestrator.resilientClick(By.xpath("//a[@class='broken-products']"), "Products Menu", "TC-04");
    }

    @Test @Order(5)
    void test05_HealAddToCartAction() throws InterruptedException {
        orchestrator.resilientClick(By.id("add-cart-fake"), "Add To Cart Button", "TC-05");
        Thread.sleep(1000);
    }

    @Test @Order(6)
    void test06_HealCartTab() {
        orchestrator.resilientClick(By.cssSelector(".missing-cart-nav"), "Cart Menu", "TC-06");
    }

    @Test @Order(7)
    void test07_HealCheckoutTab() {
        orchestrator.resilientClick(By.id("checkout-link-404"), "Checkout Menu", "TC-07");
    }

    @Test @Order(8)
    void test08_HealSettingsTab() {
        orchestrator.resilientClick(By.xpath("//nav/a[@href='broken-settings']"), "Settings Menu", "TC-08");
    }

    @Test @Order(9)
    void test09_HealDashboardTab() {
        orchestrator.resilientClick(By.cssSelector("li.broken-dash"), "Dashboard Menu", "TC-09");
    }

    // ==========================================
    // PHASE 3: COMPLEX STATE RE-VERIFICATION
    // ==========================================

    @Test @Order(10)
    void test10_HealReorderWidgetsAction() {
        orchestrator.resilientClick(By.id("bad-widget-btn"), "Reorder widgets Button", "TC-10");
    }

    @Test @Order(11)
    void test11_HealProductsAgain() {
        orchestrator.resilientClick(By.cssSelector("a.fail-products"), "Products Menu", "TC-11");
    }

    @Test @Order(12)
    void test12_HealAddSecondProduct() throws InterruptedException {
        orchestrator.resilientClick(By.xpath("//button[@data-test='missing']"), "Add To Cart Button", "TC-12");
        Thread.sleep(1000);
    }

    @Test @Order(13)
    void test13_HealCartAgain() {
        orchestrator.resilientClick(By.id("nav-cart-err"), "Cart Menu", "TC-13");
    }

    @Test @Order(14)
    void test14_HealCheckoutAgain() {
        orchestrator.resilientClick(By.cssSelector(".checkout-err"), "Checkout Menu", "TC-14");
    }

    @Test @Order(15)
    void test15_HealSettingsAgain() {
        orchestrator.resilientClick(By.xpath("//a[@class='fail-setting']"), "Settings Menu", "TC-15");
    }

    // ==========================================
    // PHASE 4: FINAL ASSERTIONS & LOGOUT
    // ==========================================

    @Test @Order(16)
    void test16_HealDashboardFinal() {
        orchestrator.resilientClick(By.id("dash-err"), "Dashboard Menu", "TC-16");
    }

    @Test @Order(17)
    void test17_HealRevenueWidgetRefresh() {
        // Interacting with the "Orders" block text inside the Dashboard panel
        orchestrator.resilientClick(By.cssSelector(".broken-orders"), "Orders", "TC-17");
    }

    @Test @Order(18)
    void test18_HealActivityWidgetRefresh() {
        orchestrator.resilientClick(By.id("bad-activity"), "Activity", "TC-18");
    }

    @Test @Order(19)
    void test19_HealReorderWidgetsFinal() {
        orchestrator.resilientClick(By.xpath("//button[@class='bad-reorder']"), "Reorder widgets Button", "TC-19");
    }

    @Test @Order(20)
    void test20_HealSignOut() throws InterruptedException {
        orchestrator.resilientClick(By.id("logout-broken"), "Sign out Button", "TC-20");
        Thread.sleep(3000); // Verify logout route
    }
}
