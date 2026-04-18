package e2e;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.testng.annotations.Test;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import static org.testng.Assert.*;
public class TempTest {
    @Test
    void runDump() throws Exception {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--incognito");
        options.addArguments("--disable-features=PasswordManager");
        options.addArguments("--disable-save-password-bubble");
        options.addArguments("--disable-autofill");
        WebDriver driver = new ChromeDriver(options);
        
        try {
            driver.get("https://retail-website-two.vercel.app/");
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
            
            WebElement email = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@type='email']")));
            email.clear();
            email.sendKeys("test@demo.com");
            
            WebElement pw = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@type='password']")));
            pw.clear();
            pw.sendKeys("password123");
            
            WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[@type='submit']")));
            btn.click();
            
            wait.until(ExpectedConditions.urlContains("dashboard"));
            
            try {
                WebElement productsNav = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(@href, 'products')]")));
                productsNav.click();
                wait.until(ExpectedConditions.urlContains("products"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            JavascriptExecutor js = (JavascriptExecutor) driver;
            String text = (String) js.executeScript(
                "return Array.from(document.querySelectorAll('a, button, div.rw-card, section, form, tr, td, input, h1, h2, span')).map(e => e.tagName + ' class=' + e.className + ' text=' + e.innerText.substring(0, 50).replace(/\\n/g, ' ')).join('\\n');"
            );
            Files.write(Paths.get("/Users/priyakumari/self-healing-selenium/dump.txt"), text.getBytes());
        } finally {
            driver.quit();
        }
    }
}
