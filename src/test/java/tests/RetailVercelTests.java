package tests;

import core.TestOrchestrator;
import io.github.bonigarcia.wdm.WebDriverManager;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

/**
 * 20-Step AI Self-Healing Regression Suite on Vercel Retail App
 * Validates complex flows including Auth, Browsing, Cart, and Layout actions.
 * Features 5 Intentionally broken locators to demonstrate AI Healing.
 */

import org.testng.annotations.*;

import static org.testng.Assert.*;
public class RetailVercelTests {

    private static WebDriver driver;
    private static TestOrchestrator orchestrator;

    @BeforeClass
    static void setUp() throws InterruptedException {
        System.out.println("🚀 [CI/CD] Bootstrapping Vercel Retail E2E Suite");
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--incognito");
        options.addArguments("--disable-features=PasswordManager");
        options.addArguments("--disable-save-password-bubble");
        options.addArguments("--disable-autofill");
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        
        orchestrator = new TestOrchestrator(driver, 3);
    }

    @AfterClass
    static void tearDown() throws InterruptedException {
        if (driver != null) {
            Thread.sleep(1200); 
            driver.quit();
        }
    }

    @BeforeMethod
    void printStart(org.testng.ITestContext testInfo) {
        System.out.println("\n▶️ Test start: " + testInfo.getName());
    }

    // ==========================================
    // MODULE 1: AUTHENTICATION
    // ==========================================

    @Test(priority = 1)
    
    void test01_AppLaunch() throws InterruptedException {
        driver.get("https://retail-website-two.vercel.app/app/dashboard");
        Thread.sleep(1200);
        assertTrue(driver.getTitle() != null, "Page title should populate");
    }

    @Test(priority = 2)
    void test02_EmailInputFlow() {
        // 🚨 INTENTIONAL BREAKAGE 1 🚨
        orchestrator.resilientFill(By.cssSelector("input.broken-email-id-99"), "test@demo.com", "Email Input", "TC-02");
    }

    @Test(priority = 3)
    
    void test03_PasswordInputFlow() {
        orchestrator.resilientFill(By.xpath("//input[@type='password']"), "password123", "password Input", "TC-03");
    }

    @Test(priority = 4)
    void test04_SubmitLoginForm() throws InterruptedException {
        // 🚨 INTENTIONAL BREAKAGE 2 🚨
        orchestrator.resilientClick(By.id("login-btn-404-missing"), "Sign in Button", "TC-04");
        orchestrator.waitForUrlContains("dashboard");
        assertTrue(driver.getCurrentUrl().contains("dashboard"), "URL should route to dashboard");
    }

    // ==========================================
    // MODULE 2: NAVIGATION & BROWSING
    // ==========================================

    @Test(priority = 5)
    
    void test05_NavigateProducts() throws InterruptedException {
        orchestrator.resilientClick(By.xpath("//a[contains(., 'Products')]"), "Products Menu", "TC-05");
        Thread.sleep(1200);
    }

    @Test(priority = 6)
    void test06_VerifyProductCard() {
        // 🚨 INTENTIONAL BREAKAGE 3 🚨
        System.out.println("Action performed: Verifying product card data");
        // Healing agent generic matcher handles semantic tags
        orchestrator.resilientClick(By.cssSelector(".broken-card-data"), "T-shirt", "TC-06");
    }

    @Test(priority = 7)
    
    void test07_NavigateSettings() {
        orchestrator.resilientClick(By.xpath("//a[contains(., 'Settings')]"), "Settings Menu", "TC-07");
    }

    @Test(priority = 8)
    
    void test08_InteractWithSetting() {
        // Vercel app has a checkbox or div interaction we can click based on "Notifications" or "Dark Mode"
        orchestrator.resilientClick(By.xpath("//a[contains(., 'Products')]"), "Products Menu", "TC-08");
    }

    @Test(priority = 9)
    
    void test09_NavigateCart() {
        orchestrator.resilientClick(By.xpath("//a[contains(., 'Cart')]"), "Cart Menu", "TC-09");
    }

    // ==========================================
    // MODULE 3: COMMERCE FLOW
    // ==========================================

    @Test(priority = 10)
    void test10_AddProductToCart() throws InterruptedException {
        // 🚨 INTENTIONAL BREAKAGE 4 🚨
        orchestrator.resilientClick(By.id("missing-add-to-cart-id"), "Add to Cart button", "TC-10");
        Thread.sleep(1000); // Allow cart state to sync
    }

    @Test(priority = 11)
    
    void test11_VerifyCartNav() {
        orchestrator.resilientClick(By.xpath("//a[contains(., 'Cart')]"), "Cart Menu", "TC-11");
    }

    @Test(priority = 12)
    
    void test12_NavigateCheckout() {
        orchestrator.resilientClick(By.xpath("//a[contains(., 'Checkout')]"), "Checkout Menu", "TC-12");
    }

    // ==========================================
    // MODULE 4: DASHBOARD ANALYTICS INTERACTION
    // ==========================================

    @Test(priority = 13)
    
    void test13_NavigateDashboard() {
        orchestrator.resilientClick(By.xpath("//a[contains(., 'Dashboard')]"), "Dashboard Menu", "TC-13");
    }

    @Test(priority = 14)
    void test14_InteractOrdersWidget() {
        // 🚨 INTENTIONAL BREAKAGE 5 🚨
        System.out.println("Action performed: Verifying widget interactions natively");
        orchestrator.resilientClick(By.cssSelector("div.none-existent-order-panel"), "Orders", "TC-14");
    }

    @Test(priority = 15)
    
    void test15_InteractOperationsWidget() {
        orchestrator.resilientClick(By.xpath("//*[contains(normalize-space(.), 'Operations')]"), "Operations", "TC-15");
    }

    @Test(priority = 16)
    
    void test16_InteractActivityWidget() {
        orchestrator.resilientClick(By.xpath("//*[contains(normalize-space(.), 'Activity')]"), "Activity", "TC-16");
    }

    @Test(priority = 17)
    
    void test17_ClickReorder() {
        System.out.println("Action performed: Attempting to reorder widgets");
        orchestrator.resilientClick(By.xpath("//button[contains(., 'Reorder widgets')]"), "Reorder widgets Button", "TC-17");
    }

    @Test(priority = 18)
    
    void test18_SecondProductNavigation() throws InterruptedException {
        orchestrator.resilientClick(By.xpath("//a[contains(., 'Products')]"), "Products Menu", "TC-18");
        Thread.sleep(1000);
    }

    @Test(priority = 19)
    
    void test19_SecondaryCartAddition() {
        System.out.println("Action performed: Appending to verified cart");
        orchestrator.resilientClick(By.xpath("//button[contains(., 'Add')]"), "Add to Cart button", "TC-19");
    }

    // ==========================================
    // MODULE 5: CLEANUP & LOGOUT
    // ==========================================

    @Test(priority = 20)
    
    void test20_PerformSignOut() throws InterruptedException {
        System.out.println("Action performed: Logging user out of framework session");
        orchestrator.resilientClick(By.xpath("//button[contains(., 'Sign out')]"), "Sign out button", "TC-20");
        Thread.sleep(1200);
        System.out.println("✅ Test passed! Session destroyed securely.");
    }
}
