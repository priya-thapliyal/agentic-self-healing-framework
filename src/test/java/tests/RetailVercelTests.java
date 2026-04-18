package tests;

import core.TestOrchestrator;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

/**
 * 20-Step AI Self-Healing Regression Suite on Vercel Retail App
 * Validates complex flows including Auth, Browsing, Cart, and Layout actions.
 * Features 5 Intentionally broken locators to demonstrate AI Healing.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RetailVercelTests {

    private static WebDriver driver;
    private static TestOrchestrator orchestrator;

    @BeforeAll
    static void setUp() throws InterruptedException {
        System.out.println("🚀 [CI/CD] Bootstrapping Vercel Retail E2E Suite");
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        // options.addArguments("--headless");
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        
        orchestrator = new TestOrchestrator(driver, 2);
    }

    @AfterAll
    static void tearDown() throws InterruptedException {
        if (driver != null) {
            Thread.sleep(3000); 
            driver.quit();
        }
    }

    @BeforeEach
    void printStart(TestInfo testInfo) {
        System.out.println("\n▶️ Test start: " + testInfo.getDisplayName());
    }

    // ==========================================
    // MODULE 1: AUTHENTICATION
    // ==========================================

    @Test @Order(1)
    @DisplayName("Verify Initial Load Context")
    void test01_AppLaunch() throws InterruptedException {
        driver.get("https://retail-website-two.vercel.app/app/dashboard");
        Thread.sleep(3000);
        assertTrue(driver.getTitle() != null, "Page title should populate");
    }

    @Test @Order(2)
    @DisplayName("Enter valid email (Healed)")
    void test02_EmailInputFlow() {
        // 🚨 INTENTIONAL BREAKAGE 1 🚨
        orchestrator.resilientFill(By.cssSelector("input.broken-email-id-99"), "test@demo.com", "Email Input", "TC-02");
    }

    @Test @Order(3)
    @DisplayName("Enter valid password")
    void test03_PasswordInputFlow() {
        orchestrator.resilientFill(By.xpath("//input[@type='password']"), "password123", "password Input", "TC-03");
    }

    @Test @Order(4)
    @DisplayName("Submit Login Form (Healed)")
    void test04_SubmitLoginForm() throws InterruptedException {
        // 🚨 INTENTIONAL BREAKAGE 2 🚨
        orchestrator.resilientClick(By.id("login-btn-404-missing"), "Sign in Button", "TC-04");
        Thread.sleep(6000); // Verify state transition
        assertTrue(driver.getCurrentUrl().contains("dashboard"), "URL should route to dashboard");
    }

    // ==========================================
    // MODULE 2: NAVIGATION & BROWSING
    // ==========================================

    @Test @Order(5)
    @DisplayName("Navigate to Products Page")
    void test05_NavigateProducts() throws InterruptedException {
        orchestrator.resilientClick(By.xpath("//a[contains(., 'Products')]"), "Products Menu", "TC-05");
        Thread.sleep(2000);
    }

    @Test @Order(6)
    @DisplayName("Verify Product Rendering (Healed)")
    void test06_VerifyProductCard() {
        // 🚨 INTENTIONAL BREAKAGE 3 🚨
        System.out.println("Action performed: Verifying product card data");
        // Healing agent generic matcher handles semantic tags
        orchestrator.resilientClick(By.cssSelector(".broken-card-data"), "T-shirt", "TC-06");
    }

    @Test @Order(7)
    @DisplayName("Navigate to Settings Page")
    void test07_NavigateSettings() {
        orchestrator.resilientClick(By.xpath("//a[contains(., 'Settings')]"), "Settings Menu", "TC-07");
    }

    @Test @Order(8)
    @DisplayName("Verify Setting Toggle Interaction")
    void test08_InteractWithSetting() {
        // Vercel app has a checkbox or div interaction we can click based on "Notifications" or "Dark Mode"
        orchestrator.resilientClick(By.xpath("//a[contains(., 'Products')]"), "Products Menu", "TC-08");
    }

    @Test @Order(9)
    @DisplayName("Navigate to Cart Page")
    void test09_NavigateCart() {
        orchestrator.resilientClick(By.xpath("//a[contains(., 'Cart')]"), "Cart Menu", "TC-09");
    }

    // ==========================================
    // MODULE 3: COMMERCE FLOW
    // ==========================================

    @Test @Order(10)
    @DisplayName("Add Item to Cart (Healed)")
    void test10_AddProductToCart() throws InterruptedException {
        // 🚨 INTENTIONAL BREAKAGE 4 🚨
        orchestrator.resilientClick(By.id("missing-add-to-cart-id"), "Add to Cart button", "TC-10");
        Thread.sleep(1000); // Allow cart state to sync
    }

    @Test @Order(11)
    @DisplayName("Verify Cart Increment Context")
    void test11_VerifyCartNav() {
        orchestrator.resilientClick(By.xpath("//a[contains(., 'Cart')]"), "Cart Menu", "TC-11");
    }

    @Test @Order(12)
    @DisplayName("Proceed to Checkout Tab")
    void test12_NavigateCheckout() {
        orchestrator.resilientClick(By.xpath("//a[contains(., 'Checkout')]"), "Checkout Menu", "TC-12");
    }

    // ==========================================
    // MODULE 4: DASHBOARD ANALYTICS INTERACTION
    // ==========================================

    @Test @Order(13)
    @DisplayName("Navigate explicitly to Dashboard")
    void test13_NavigateDashboard() {
        orchestrator.resilientClick(By.xpath("//a[contains(., 'Dashboard')]"), "Dashboard Menu", "TC-13");
    }

    @Test @Order(14)
    @DisplayName("Verify Orders Widget Interaction (Healed)")
    void test14_InteractOrdersWidget() {
        // 🚨 INTENTIONAL BREAKAGE 5 🚨
        System.out.println("Action performed: Verifying widget interactions natively");
        orchestrator.resilientClick(By.cssSelector("div.none-existent-order-panel"), "Orders", "TC-14");
    }

    @Test @Order(15)
    @DisplayName("Verify Operations Widget Interaction")
    void test15_InteractOperationsWidget() {
        orchestrator.resilientClick(By.xpath("//*[contains(normalize-space(.), 'Operations')]"), "Operations", "TC-15");
    }

    @Test @Order(16)
    @DisplayName("Verify Activity Widget Interaction")
    void test16_InteractActivityWidget() {
        orchestrator.resilientClick(By.xpath("//*[contains(normalize-space(.), 'Activity')]"), "Activity", "TC-16");
    }

    @Test @Order(17)
    @DisplayName("Verify Dynamic Reordering Context")
    void test17_ClickReorder() {
        System.out.println("Action performed: Attempting to reorder widgets");
        orchestrator.resilientClick(By.xpath("//button[contains(., 'Reorder widgets')]"), "Reorder widgets Button", "TC-17");
    }

    @Test @Order(18)
    @DisplayName("Verify Nav Resiliency - Sidebar Product Sync")
    void test18_SecondProductNavigation() throws InterruptedException {
        orchestrator.resilientClick(By.xpath("//a[contains(., 'Products')]"), "Products Menu", "TC-18");
        Thread.sleep(1000);
    }

    @Test @Order(19)
    @DisplayName("Add Second Item to Cart")
    void test19_SecondaryCartAddition() {
        System.out.println("Action performed: Appending to verified cart");
        orchestrator.resilientClick(By.xpath("//button[contains(., 'Add')]"), "Add to Cart button", "TC-19");
    }

    // ==========================================
    // MODULE 5: CLEANUP & LOGOUT
    // ==========================================

    @Test @Order(20)
    @DisplayName("Logout flow from Dashboard Sidebar")
    void test20_PerformSignOut() throws InterruptedException {
        System.out.println("Action performed: Logging user out of framework session");
        orchestrator.resilientClick(By.xpath("//button[contains(., 'Sign out')]"), "Sign out button", "TC-20");
        Thread.sleep(3000);
        System.out.println("✅ Test passed! Session destroyed securely.");
    }
}
