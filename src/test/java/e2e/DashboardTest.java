package e2e;

import core.TestOrchestrator;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.testng.annotations.AfterMethod;
import java.time.Duration;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class DashboardTest {

    private WebDriver driver;
    private TestOrchestrator orchestrator;
    private final String BASE_URL = "https://retail-website-two.vercel.app";

    @BeforeClass
    public void setupClass() {
        WebDriverManager.chromedriver()
            .clearDriverCache()
            .setup();
    }

    @BeforeMethod
    public void setupTest() {
        ChromeOptions options = new ChromeOptions();
        
        // CI/Headless Environment Optimization
        String isCi = System.getenv("GITHUB_ACTIONS");
        if ("true".equals(isCi)) {
            options.addArguments("--headless=new");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--disable-gpu");
            options.addArguments("--disable-extensions");
            options.addArguments("--disable-setuid-sandbox");
            options.addArguments("--remote-allow-origins=*");
            options.addArguments("--window-size=1920,1080");
        }

        options.addArguments("--incognito");
        options.addArguments("--disable-features=PasswordManager");
        options.addArguments("--disable-save-password-bubble");
        options.addArguments("--disable-autofill");
        
        driver = new ChromeDriver(options);
        
        // Set implicit waits and stability timeouts
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));

        if (!"true".equals(isCi)) {
            driver.manage().window().maximize();
        }
        orchestrator = new TestOrchestrator(driver, 3);
    }

    @AfterMethod
    public void teardown() {
        if (driver != null) {
            try { Thread.sleep(500); } catch (Exception e) {}
            driver.quit();
        }
    }

    @Step("System Login Flow")
    private void login() {
        driver.get(BASE_URL + "/");
        try {
            orchestrator.resilientFill(By.xpath("//input[@type='email']"), "test@demo.com", "Email Input", "Setup");
            orchestrator.resilientFill(By.xpath("//input[@type='password']"), "password123", "Password Input", "Setup");
            orchestrator.resilientClick(By.xpath("//button[@type='submit']"), "Sign In Button", "Setup");
            Thread.sleep(1000);
        } catch(Exception e){}
    }

    @Step("Direct Navigation To: {0}")
    private void navigateTo(String path) {
        if(path.equals("/")) {
            driver.get(BASE_URL + "/");
        } else {
            login();
            if(!path.equals("/app/dashboard")) {
                driver.get(BASE_URL + path);
                try { Thread.sleep(800); } catch (Exception e) {}
            }
        }
    }
    @Test(priority = 1)
    @Description("Independent Optimized Execution of Step 1")
    public void test01_EmailInput() throws Exception {
        navigateTo("/");
        System.out.println("\n--- 🟢 EXECUTING MAIN TEST ACTION: STEP 1 ---");
        orchestrator.resilientFill(By.cssSelector("input.old-email-field"), "test@demo.com", "Email Input", "Test Case 1");
        Thread.sleep(1000);
    }
    @Test(priority = 2)
    @Description("Independent Optimized Execution of Step 2")
    public void test02_PasswordInput() throws Exception {
        navigateTo("/");
        System.out.println("\n--- 🟢 EXECUTING MAIN TEST ACTION: STEP 2 ---");
        orchestrator.resilientFill(By.xpath("//input[@type='password']"), "password123", "Password Input", "Test Case 2");
        Thread.sleep(1000);
    }
    @Test(priority = 3)
    @Description("Independent Optimized Execution of Step 3")
    public void test03_SignInButton() throws Exception {
        navigateTo("/");
        System.out.println("\n--- 🟢 EXECUTING MAIN TEST ACTION: STEP 3 ---");
        orchestrator.resilientClick(By.id("legacy-login-btn-99"), "Sign In Button", "Test Case 3");
        Thread.sleep(1000);
    }
    @Test(priority = 4)
    @Description("Independent Optimized Execution of Step 4")
    public void test04_ProductsMenu() throws Exception {
        navigateTo("/app/dashboard");
        System.out.println("\n--- 🟢 EXECUTING MAIN TEST ACTION: STEP 4 ---");
        orchestrator.resilientClick(By.xpath("//a[contains(., 'Products')]"), "Products Menu", "Test Case 4");
        Thread.sleep(1000);
    }
    @Test(priority = 5)
    @Description("Independent Optimized Execution of Step 5")
    public void test05_AddtoCartButton() throws Exception {
        navigateTo("/app/products");
        System.out.println("\n--- 🟢 EXECUTING MAIN TEST ACTION: STEP 5 ---");
        orchestrator.resilientClick(By.id("btn-legacy-cart-add-42"), "Add to Cart Button", "Test Case 5");
        Thread.sleep(1000);
    }
    @Test(priority = 6)
    @Description("Independent Optimized Execution of Step 6")
    public void test06_CartMenu() throws Exception {
        navigateTo("/app/products");
        System.out.println("\n--- 🟢 EXECUTING MAIN TEST ACTION: STEP 6 ---");
        orchestrator.resilientClick(By.cssSelector(".missing-cart-nav"), "Cart Menu", "Test Case 6");
        Thread.sleep(1000);
    }
    @Test(priority = 7)
    @Description("Independent Optimized Execution of Step 7")
    public void test07_CheckoutMenu() throws Exception {
        navigateTo("/app/cart");
        System.out.println("\n--- 🟢 EXECUTING MAIN TEST ACTION: STEP 7 ---");
        orchestrator.resilientClick(By.id("checkout-link-404"), "Checkout Menu", "Test Case 7");
        Thread.sleep(1000);
    }
    @Test(priority = 8)
    @Description("Independent Optimized Execution of Step 8")
    public void test08_SettingsMenu() throws Exception {
        navigateTo("/app/checkout");
        System.out.println("\n--- 🟢 EXECUTING MAIN TEST ACTION: STEP 8 ---");
        orchestrator.resilientClick(By.xpath("//nav/a[@href='broken-settings']"), "Settings Menu", "Test Case 8");
        Thread.sleep(1000);
    }
    @Test(priority = 9)
    @Description("Independent Optimized Execution of Step 9")
    public void test09_DashboardMenu() throws Exception {
        navigateTo("/app/settings");
        System.out.println("\n--- 🟢 EXECUTING MAIN TEST ACTION: STEP 9 ---");
        orchestrator.resilientClick(By.cssSelector("li.broken-dash"), "Dashboard Menu", "Test Case 9");
        Thread.sleep(1000);
    }
    @Test(priority = 10)
    @Description("Independent Optimized Execution of Step 10")
    public void test10_ReorderwidgetsButton() throws Exception {
        navigateTo("/app/dashboard");
        System.out.println("\n--- 🟢 EXECUTING MAIN TEST ACTION: STEP 10 ---");
        orchestrator.resilientClick(By.id("bad-widget-btn"), "Reorder widgets Button", "Test Case 10");
        Thread.sleep(1000);
    }
/*
    @Test(priority = 11)
    @Description("Independent Optimized Execution of Step 11")
    public void test11_ProductsMenu() throws Exception {
        navigateTo("/app/dashboard");
        System.out.println("\n--- \uD83D\uDFE2 EXECUTING MAIN TEST ACTION: STEP 11 ---");
        orchestrator.resilientClick(By.cssSelector("a.fail-products"), "Products Menu", "Test Case 11");
        Thread.sleep(1000);
    }
    @Test(priority = 12)
    @Description("Independent Optimized Execution of Step 12")
    public void test12_AddToCartButton() throws Exception {
        navigateTo("/app/products");
        System.out.println("\n--- \uD83D\uDFE2 EXECUTING MAIN TEST ACTION: STEP 12 ---");
        orchestrator.resilientClick(By.xpath("//button[@data-test='missing']"), "Add To Cart Button", "Test Case 12");
        Thread.sleep(1000);
    }
    @Test(priority = 13)
    @Description("Independent Optimized Execution of Step 13")
    public void test13_CartMenu() throws Exception {
        navigateTo("/app/products");
        System.out.println("\n--- \uD83D\uDFE2 EXECUTING MAIN TEST ACTION: STEP 13 ---");
        orchestrator.resilientClick(By.id("nav-cart-err"), "Cart Menu", "Test Case 13");
        Thread.sleep(1000);
    }
    @Test(priority = 14)
    @Description("Independent Optimized Execution of Step 14")
    public void test14_CheckoutMenu() throws Exception {
        navigateTo("/app/cart");
        System.out.println("\n--- \uD83D\uDFE2 EXECUTING MAIN TEST ACTION: STEP 14 ---");
        orchestrator.resilientClick(By.cssSelector(".checkout-err"), "Checkout Menu", "Test Case 14");
        Thread.sleep(1000);
    }
    @Test(priority = 15)
    @Description("Independent Optimized Execution of Step 15")
    public void test15_SettingsMenu() throws Exception {
        navigateTo("/app/checkout");
        System.out.println("\n--- \uD83D\uDFE2 EXECUTING MAIN TEST ACTION: STEP 15 ---");
        orchestrator.resilientClick(By.xpath("//a[@class='fail-setting']"), "Settings Menu", "Test Case 15");
        Thread.sleep(1000);
    }
    @Test(priority = 16)
    @Description("Independent Optimized Execution of Step 16")
    public void test16_DashboardMenu() throws Exception {
        navigateTo("/app/settings");
        System.out.println("\n--- \uD83D\uDFE2 EXECUTING MAIN TEST ACTION: STEP 16 ---");
        orchestrator.resilientClick(By.id("dash-err"), "Dashboard Menu", "Test Case 16");
        Thread.sleep(1000);
    }
    @Test(priority = 17)
    @Description("Independent Optimized Execution of Step 17")
    public void test17_Orders() throws Exception {
        navigateTo("/app/dashboard");
        System.out.println("\n--- \uD83D\uDFE2 EXECUTING MAIN TEST ACTION: STEP 17 ---");
        orchestrator.resilientClick(By.cssSelector(".broken-orders"), "Orders", "Test Case 17");
        Thread.sleep(1000);
    }
    @Test(priority = 18)
    @Description("Independent Optimized Execution of Step 18")
    public void test18_Activity() throws Exception {
        navigateTo("/app/dashboard");
        System.out.println("\n--- \uD83D\uDFE2 EXECUTING MAIN TEST ACTION: STEP 18 ---");
        orchestrator.resilientClick(By.id("bad-activity"), "Activity", "Test Case 18");
        Thread.sleep(1000);
    }
    @Test(priority = 19)
    @Description("Independent Optimized Execution of Step 19")
    public void test19_Revenue() throws Exception {
        navigateTo("/app/dashboard");
        System.out.println("\n--- \uD83D\uDFE2 EXECUTING MAIN TEST ACTION: STEP 19 ---");
        orchestrator.resilientClick(By.className("missing-revenue"), "Revenue", "Test Case 19");
        Thread.sleep(1000);
    }
    @Test(priority = 20)
    @Description("Independent Optimized Execution of Step 20")
    public void test20_Refunds() throws Exception {
        navigateTo("/app/dashboard");
        System.out.println("\n--- \uD83D\uDFE2 EXECUTING MAIN TEST ACTION: STEP 20 ---");
        orchestrator.resilientClick(By.id("missing-refund"), "Refunds", "Test Case 20");
        Thread.sleep(1000);
    }
    @Test(priority = 21)
    @Description("Independent Optimized Execution of Step 21")
    public void test21_Operations() throws Exception {
        navigateTo("/app/dashboard");
        System.out.println("\n--- \uD83D\uDFE2 EXECUTING MAIN TEST ACTION: STEP 21 ---");
        orchestrator.resilientClick(By.xpath("//*[@data-testid='ops']"), "Operations", "Test Case 21");
        Thread.sleep(1000);
    }
    @Test(priority = 22)
    @Description("Independent Optimized Execution of Step 22")
    public void test22_ProductsMenu() throws Exception {
        navigateTo("/app/dashboard");
        System.out.println("\n--- \uD83D\uDFE2 EXECUTING MAIN TEST ACTION: STEP 22 ---");
        orchestrator.resilientClick(By.cssSelector(".missing-products"), "Products Menu", "Test Case 22");
        Thread.sleep(1000);
    }
    @Test(priority = 23)
    @Description("Independent Optimized Execution of Step 23")
    public void test23_AddToCartButton() throws Exception {
        navigateTo("/app/products");
        System.out.println("\n--- \uD83D\uDFE2 EXECUTING MAIN TEST ACTION: STEP 23 ---");
        orchestrator.resilientClick(By.xpath("//button[@id='wrong-id']"), "Add To Cart Button", "Test Case 23");
        Thread.sleep(1000);
    }
    @Test(priority = 24)
    @Description("Independent Optimized Execution of Step 24")
    public void test24_DashboardMenu() throws Exception {
        navigateTo("/app/products");
        System.out.println("\n--- \uD83D\uDFE2 EXECUTING MAIN TEST ACTION: STEP 24 ---");
        orchestrator.resilientClick(By.id("missing-dash-link"), "Dashboard Menu", "Test Case 23.5");
        Thread.sleep(1000);
    }
    @Test(priority = 25)
    @Description("Independent Optimized Execution of Step 25")
    public void test25_ReorderwidgetsButton() throws Exception {
        navigateTo("/app/dashboard");
        System.out.println("\n--- \uD83D\uDFE2 EXECUTING MAIN TEST ACTION: STEP 25 ---");
        orchestrator.resilientClick(By.xpath("//button[@class='bad-reorder']"), "Reorder widgets Button", "Test Case 24");
        Thread.sleep(1000);
    }
    @Test(priority = 26)
    @Description("Independent Optimized Execution of Step 26")
    public void test26_SignoutButton() throws Exception {
        navigateTo("/app/dashboard");
        System.out.println("\n--- \uD83D\uDFE2 EXECUTING MAIN TEST ACTION: STEP 26 ---");
        orchestrator.resilientClick(By.id("logout-broken"), "Sign out Button", "Test Case 25");
        Thread.sleep(1000);
    }
*/
}
