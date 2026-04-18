package e2e;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.Test;
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

public class TempTest {
    @Test
    void runDump() throws Exception {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        WebDriver driver = new ChromeDriver(options);
        
        try {
            driver.get("https://retail-website-two.vercel.app/app/dashboard");
            Thread.sleep(4000);
            
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            
            // Try to use JS to set value to trigger React
            WebElement email = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@type='email']")));
            email.clear();
            email.sendKeys("test@demo.com");
            
            WebElement pw = driver.findElement(By.xpath("//input[@type='password']"));
            pw.clear();
            pw.sendKeys("password123");
            
            // Try clicking the button
            WebElement btn = driver.findElement(By.xpath("//button[contains(., 'Sign in')]"));
            btn.click();
            
            Thread.sleep(6000); // Wait for page nav to Dashboard
            
            try {
                // Now click Products
                driver.findElement(By.xpath("//a[contains(., 'Products')]")).click();
                Thread.sleep(3000);
            } catch (Exception ignore) {}
            
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
