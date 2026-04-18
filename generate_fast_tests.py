import re

# Read original test calls (we assume the ones from TempTest or DashboardTest that break)
# But I already have the 26 calls from the previous script! I can just hardcode them or parse them.
calls = [
    ('orchestrator.resilientFill(By.cssSelector("input.old-email-field"), "test@demo.com", "Email Input", "Test Case 1");', '/'),
    ('orchestrator.resilientFill(By.xpath("//input[@type=\'password\']"), "password123", "Password Input", "Test Case 2");', '/'),
    ('orchestrator.resilientClick(By.id("legacy-login-btn-99"), "Sign In Button", "Test Case 3");', '/'),
    ('orchestrator.resilientClick(By.xpath("//a[contains(., \'Products\')]"), "Products Menu", "Test Case 4");', '/app/dashboard'),
    ('orchestrator.resilientClick(By.id("btn-legacy-cart-add-42"), "Add to Cart Button", "Test Case 5");', '/app/products'),
    ('orchestrator.resilientClick(By.cssSelector(".missing-cart-nav"), "Cart Menu", "Test Case 6");', '/app/products'),
    ('orchestrator.resilientClick(By.id("checkout-link-404"), "Checkout Menu", "Test Case 7");', '/app/cart'),
    ('orchestrator.resilientClick(By.xpath("//nav/a[@href=\'broken-settings\']"), "Settings Menu", "Test Case 8");', '/app/checkout'),
    ('orchestrator.resilientClick(By.cssSelector("li.broken-dash"), "Dashboard Menu", "Test Case 9");', '/app/settings'),
    ('orchestrator.resilientClick(By.id("bad-widget-btn"), "Reorder widgets Button", "Test Case 10");', '/app/dashboard'),
    ('orchestrator.resilientClick(By.cssSelector("a.fail-products"), "Products Menu", "Test Case 11");', '/app/dashboard'),
    ('orchestrator.resilientClick(By.xpath("//button[@data-test=\'missing\']"), "Add To Cart Button", "Test Case 12");', '/app/products'),
    ('orchestrator.resilientClick(By.id("nav-cart-err"), "Cart Menu", "Test Case 13");', '/app/products'),
    ('orchestrator.resilientClick(By.cssSelector(".checkout-err"), "Checkout Menu", "Test Case 14");', '/app/cart'),
    ('orchestrator.resilientClick(By.xpath("//a[@class=\'fail-setting\']"), "Settings Menu", "Test Case 15");', '/app/checkout'),
    ('orchestrator.resilientClick(By.id("dash-err"), "Dashboard Menu", "Test Case 16");', '/app/settings'),
    ('orchestrator.resilientClick(By.cssSelector(".broken-orders"), "Orders", "Test Case 17");', '/app/dashboard'),
    ('orchestrator.resilientClick(By.id("bad-activity"), "Activity", "Test Case 18");', '/app/dashboard'),
    ('orchestrator.resilientClick(By.className("missing-revenue"), "Revenue", "Test Case 19");', '/app/dashboard'),
    ('orchestrator.resilientClick(By.id("missing-refund"), "Refunds", "Test Case 20");', '/app/dashboard'),
    ('orchestrator.resilientClick(By.xpath("//*[@data-testid=\'ops\']"), "Operations", "Test Case 21");', '/app/dashboard'),
    ('orchestrator.resilientClick(By.cssSelector(".missing-products"), "Products Menu", "Test Case 22");', '/app/dashboard'),
    ('orchestrator.resilientClick(By.xpath("//button[@id=\'wrong-id\']"), "Add To Cart Button", "Test Case 23");', '/app/products'),
    ('orchestrator.resilientClick(By.id("missing-dash-link"), "Dashboard Menu", "Test Case 23.5");', '/app/products'),
    ('orchestrator.resilientClick(By.xpath("//button[@class=\'bad-reorder\']"), "Reorder widgets Button", "Test Case 24");', '/app/dashboard'),
    ('orchestrator.resilientClick(By.id("logout-broken"), "Sign out Button", "Test Case 25");', '/app/dashboard')
]

java_code = """package e2e;

import core.TestOrchestrator;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.testng.annotations.AfterMethod;
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
        WebDriverManager.chromedriver().setup();
    }

    @BeforeMethod
    public void setupTest() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--incognito");
        options.addArguments("--disable-features=PasswordManager");
        options.addArguments("--disable-save-password-bubble");
        options.addArguments("--disable-autofill");
        
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
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
"""

for i, (call, route) in enumerate(calls):
    description_match = re.search(r',\s*"([^"]+)"\s*,', call)
    desc = description_match.group(1).replace(" ", "").replace("-", "") if description_match else f"Action{i+1}"
    
    java_code += f"""    @Test(priority = {i+1})
    @Description("Independent Optimized Execution of Step {i+1}")
    public void test{(str(i+1)).zfill(2)}_{desc}() throws Exception {{
        navigateTo("{route}");
        System.out.println("\\n--- 🟢 EXECUTING MAIN TEST ACTION: STEP {i+1} ---");
        {call}
        Thread.sleep(1000);
    }}
"""

java_code += "}\n"

with open("src/test/java/e2e/DashboardTest.java", "w") as f:
    f.write(java_code)
print("DashboardTest.java regenerated successfully!")
