import re

with open("src/test/java/e2e/DashboardTest.java", "r") as f:
    content = f.read()

# Extract orchestrator calls
calls = re.findall(r'(orchestrator\.resilient(?:Click|Fill)\(.*?\);)', content)

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

public class IndependentTests {

    private WebDriver driver;
    private TestOrchestrator orchestrator;

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

    private void performPrequisites(int upToStep) throws Exception {
        if (upToStep >= 1) {
            driver.get("https://retail-website-two.vercel.app/app/dashboard");
            Thread.sleep(1000);
        }
"""

for i, call in enumerate(calls):
    java_code += f"        if (upToStep > {i+1}) {{\n"
    # replace 'Test Case X' with 'Pre-req X'
    prereq_call = re.sub(r'"Test Case [\d\.]+"', f'"Pre-req {i+1}"', call)
    java_code += f"            {prereq_call}\n"
    java_code += f"            Thread.sleep(800);\n"
    java_code += f"        }}\n"

java_code += "    }\n\n"

for i, call in enumerate(calls):
    # Extract the target description to use in the method name
    description_match = re.search(r',\s*"([^"]+)"\s*,', call)
    desc = description_match.group(1).replace(" ", "").replace("-", "") if description_match else f"Action{i+1}"
    
    java_code += f"""    @Test(priority = {i+1})
    @Description("Independent Execution of Step {i+1}")
    public void test{(str(i+1)).zfill(2)}_{desc}() throws Exception {{
        performPrequisites({i+1});
        System.out.println("\\n--- 🟢 EXECUTING MAIN TEST ACTION: STEP {i+1} ---");
        {call}
        Thread.sleep(1000);
    }}
"""

java_code += "}\n"

with open("src/test/java/e2e/IndependentTests.java", "w") as f:
    f.write(java_code)
print("Generated IndependentTests.java successfully!")
