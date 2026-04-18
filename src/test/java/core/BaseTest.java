package core;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

public class BaseTest {
    protected WebDriver driver;
    protected TestOrchestrator orchestrator;

    @BeforeClass
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--incognito");
        options.addArguments("--disable-features=PasswordManager");
        options.addArguments("--disable-save-password-bubble");
        options.addArguments("--disable-autofill");
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        orchestrator = new TestOrchestrator(driver, 10);
    }

    @AfterClass
    public void tearDown() {
        if (driver != null && ((RemoteWebDriver) driver).getSessionId() != null) {
            driver.quit();
        }
    }

    protected boolean isSessionActive() {
        return driver != null && ((RemoteWebDriver) driver).getSessionId() != null;
    }
}
