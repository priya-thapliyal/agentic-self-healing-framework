package core;

import agents.SelectorRecoveryAgent;
import tools.DomSnapshotTool;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * Wraps each Selenium action in a recoverable execution unit.
 * Routes failures to correct agent type.
 */
public class TestOrchestrator {

    private final WebDriver driver;
    private final WebDriverWait wait;
    private final SelectorRecoveryAgent recoveryAgent;
    private final DomSnapshotTool domTool;

    public TestOrchestrator(WebDriver driver, int timeoutInSeconds) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds));
        this.recoveryAgent = new SelectorRecoveryAgent();
        this.domTool = new DomSnapshotTool(driver);
        HealingReportWriter.initializeReport();
    }

    /**
     * Helper to visually highlight elements during execution for the CTO demonstration.
     */
    private void highlightElement(WebElement element, String color) {
        if (driver instanceof org.openqa.selenium.JavascriptExecutor) {
            org.openqa.selenium.JavascriptExecutor js = (org.openqa.selenium.JavascriptExecutor) driver;
            // Draw a thick glowing border around the element
            js.executeScript("arguments[0].setAttribute('style', 'border: 4px solid " + color + " !important; box-shadow: 0 0 15px " + color + " !important; background-color: rgba(0,255,0,0.1);');", element);
        }
    }

    /**
     * Helper to inject an on-screen text banner so the CTO can read what the framework is thinking directly on the UI!
     */
    private void showUiOverlay(String message, String backgroundColor) {
        if (driver instanceof org.openqa.selenium.JavascriptExecutor) {
            org.openqa.selenium.JavascriptExecutor js = (org.openqa.selenium.JavascriptExecutor) driver;
            String script = "var existing = document.getElementById('ai-healer-overlay');" +
                            "if (!existing) {" +
                            "  existing = document.createElement('div');" +
                            "  existing.id = 'ai-healer-overlay';" +
                            "  existing.style.position = 'fixed';" +
                            "  existing.style.bottom = '20px';" +
                            "  existing.style.left = '50%';" +
                            "  existing.style.transform = 'translateX(-50%)';" +
                            "  existing.style.padding = '15px 30px';" +
                            "  existing.style.zIndex = '999999';" +
                            "  existing.style.fontSize = '20px';" +
                            "  existing.style.fontWeight = 'bold';" +
                            "  existing.style.borderRadius = '10px';" +
                            "  existing.style.color = '#fff';" +
                            "  existing.style.boxShadow = '0px 10px 30px rgba(0,0,0,0.5)';" +
                            "  existing.style.fontFamily = 'monospace';" +
                            "  document.body.appendChild(existing);" +
                            "}" +
                            "existing.style.backgroundColor = '" + backgroundColor + "';" +
                            "existing.innerText = '" + message.replace("'", "\\'") + "';";
            js.executeScript(script);
        }
    }

    public void resilientClick(By locator, String targetDescription, String stepId) {
        showUiOverlay("🖱️ Action: Clicking " + targetDescription, "#007bff");
        int maxRetries = 2;
        int attempt = 0;
        
        while (attempt <= maxRetries) {
            try {
                if (attempt > 0) {
                    showUiOverlay("⚠️ Fail retrying locator... (Attempt " + attempt + ")", "#f39c12");
                    System.out.println("⚠️ [Orchestrator] Element not found. Retrying... (Attempt " + attempt + ")");
                }
                WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
                element = wait.until(ExpectedConditions.elementToBeClickable(element));
                highlightElement(element, "blue");
                
                Thread.sleep(800); // Brief pause
                element.click();
                showUiOverlay("✅ Success! Moving to next test case...", "#28a745");
                System.out.println("✅ [Orchestrator] Step '" + stepId + "' click passed deterministically on " + locator);
                HealingReportWriter.logStable(stepId, locator.toString());
                return; // Success! Exit method
            } catch (Exception e) {
                attempt++;
                if (attempt <= maxRetries) {
                    try { Thread.sleep(1500); } catch (Exception ignored) {} // Wait for network to catch up
                } else {
                    System.err.println("❌ [Orchestrator] Step '" + stepId + "' click failed after " + maxRetries + " retries! Error: " + e.getClass().getSimpleName());
                    handleRecovery("click", targetDescription, null, e.getMessage(), stepId, locator.toString());
                    return;
                }
            }
        }
    }

    /**
     * Attempts to find an element and enter text, with fallback.
     */
    public void resilientFill(By locator, String text, String targetDescription, String stepId) {
        showUiOverlay("⌨️ Action: Filling " + targetDescription, "#007bff");
        int maxRetries = 2;
        int attempt = 0;
        
        while (attempt <= maxRetries) {
            try {
                if (attempt > 0) {
                    showUiOverlay("⚠️ Fail retrying locator... (Attempt " + attempt + ")", "#f39c12");
                    System.out.println("⚠️ [Orchestrator] Element not found. Retrying... (Attempt " + attempt + ")");
                }
                WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
                highlightElement(element, "blue");
                
                Thread.sleep(800); // Brief pause 
                
                // Deep clean prefilled data to avoid Autofill glitches
                element.sendKeys(org.openqa.selenium.Keys.chord(org.openqa.selenium.Keys.COMMAND, "a"));
                element.sendKeys(org.openqa.selenium.Keys.chord(org.openqa.selenium.Keys.CONTROL, "a"));
                element.sendKeys(org.openqa.selenium.Keys.BACK_SPACE);
                element.clear();
                
                element.sendKeys(text);
                
                // Force React to recognize the change natively
                if (driver instanceof org.openqa.selenium.JavascriptExecutor) {
                    ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                        "arguments[0].dispatchEvent(new Event('input', { bubbles: true }));" +
                        "arguments[0].dispatchEvent(new Event('change', { bubbles: true }));", 
                        element
                    );
                }
                
                showUiOverlay("✅ Success! Moving to next test case...", "#28a745");
                System.out.println("✅ [Orchestrator] Step '" + stepId + "' fill passed deterministically on " + locator);
                HealingReportWriter.logStable(stepId, locator.toString());
                return; // Success! Exit method
            } catch (Exception e) {
                attempt++;
                if (attempt <= maxRetries) {
                    try { Thread.sleep(1500); } catch (Exception ignored) {} // Wait for network to catch up
                } else {
                    System.err.println("❌ [Orchestrator] Step '" + stepId + "' fill failed after " + maxRetries + " retries! Error: " + e.getClass().getSimpleName());
                    handleRecovery("fill", targetDescription, text, e.getMessage(), stepId, locator.toString());
                    return;
                }
            }
        }
    }

    private void handleRecovery(String action, String targetDescription, String fillText, String exceptionContext, String stepId, String oldLocator) {
        System.out.println("🔄 [Orchestrator] Initiating Self-Healing Fallback for action: " + action + "...");
        
        // 🌟 ON-SCREEN ERROR HIGHLIGHT 🌟
        showUiOverlay("❌ Locator Broken! Unable to find: " + targetDescription + ". Initiating AI Healing...", "#cc0000");
        try { Thread.sleep(2500); } catch (Exception e){} // Pause to read it
        
        // 1. Capture context
        String domExcerpt = domTool.captureCompactDom();
        
        showUiOverlay("🤖 AI Agent analyzing DOM to recover intent: '" + targetDescription + "'...", "#cc5500");
        
        // 2. Query Agent
        List<SelectorRecoveryAgent.SelectorCandidate> candidates = recoveryAgent.recoverSelectors(targetDescription, exceptionContext, domExcerpt);
        
        if (candidates.isEmpty()) {
            HealingReportWriter.logFailed(stepId, oldLocator);
            throw new RuntimeException("Healing failed: Agent returned no candidates for " + targetDescription);
        }

        // 3. Evaluate Policy (Simulated: Pick highest confidence > 0.8)
        SelectorRecoveryAgent.SelectorCandidate bestCandidate = candidates.get(0);
        if (bestCandidate.confidence < 0.80) {
            HealingReportWriter.logFailed(stepId, oldLocator);
            // CRITICAL DEBUG: Take a screenshot so we know what's wrong!
            try {
                java.io.File srcFile = ((org.openqa.selenium.TakesScreenshot)driver).getScreenshotAs(org.openqa.selenium.OutputType.FILE);
                java.nio.file.Files.copy(srcFile.toPath(), java.nio.file.Paths.get("/Users/priyakumari/self-healing-selenium/abort-screenshot.png"), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            } catch (Exception ignore) {}
            throw new RuntimeException("Healing aborted by PolicyEngine: Highest confidence is " + bestCandidate.confidence);
        }

        System.out.println("⚖️  [PolicyEngine] Auto-heal approved for candidate: " + bestCandidate.selector + " (Confidence: " + bestCandidate.confidence + ")");
        System.out.println("🔄 [Orchestrator] Retrying action with new selector...");

        try {
            By healedLocator;
            if (bestCandidate.strategy.equals("xpath")) healedLocator = By.xpath(bestCandidate.selector);
            else if (bestCandidate.strategy.equals("css")) healedLocator = By.cssSelector(bestCandidate.selector);
            else healedLocator = By.xpath(bestCandidate.selector); // fallback

            WebDriverWait healWait = new WebDriverWait(driver, Duration.ofSeconds(5));
            WebElement element = healWait.until(ExpectedConditions.presenceOfElementLocated(healedLocator));
            
            System.out.println("⚠️ Locator failed for " + targetDescription);
            System.out.println("🛠️ Healing started...");
            
            // 🌟 VISUAL DEMONSTRATION HIGHLIGHT 🌟
            highlightElement(element, "lime"); // Glow bright green!
            showUiOverlay("🏥 HEAL SUCCESS! AI found new locator: " + bestCandidate.selector, "#28a745");
            Thread.sleep(3000); // Pause for 3 seconds so the CTO explicitly reads the healed locator!
            
            if (action.equals("fill")) {
                element.clear();
                element.sendKeys(fillText);
                // Force React
                if (driver instanceof org.openqa.selenium.JavascriptExecutor) {
                    ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                        "arguments[0].dispatchEvent(new Event('input', { bubbles: true }));" +
                        "arguments[0].dispatchEvent(new Event('change', { bubbles: true }));", 
                        element
                    );
                }
            } else {
                // Perform native execution so React explicitly registers the pointer event
                element.click();
            }
            
            HealingReportWriter.logHealed(stepId, oldLocator, bestCandidate.selector, bestCandidate.confidence);
            System.out.println("\n✅ Recovered locator used: " + bestCandidate.selector);
            System.out.println("🏥 [Orchestrator] SURGERY SUCCESSFUL! Action completed using AI healed selector.");
        } catch (Exception fatal) {
            System.err.println("💀 [Orchestrator] Healed selector also failed. Throwing fatal error.");
            throw new RuntimeException("Healing failed persistently", fatal);
        }
    }
}
