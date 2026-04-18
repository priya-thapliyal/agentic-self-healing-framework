package core;

import agents.SelectorRecoveryAgent;
import tools.DomSnapshotTool;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
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

    private boolean isSessionActive() {
        if (driver == null) return false;
        try {
            driver.getTitle();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void highlightElement(WebElement element, String color) {
        if (!isSessionActive()) return;
        try {
            if (driver instanceof JavascriptExecutor) {
                JavascriptExecutor js = (JavascriptExecutor) driver;
                js.executeScript("arguments[0].setAttribute('style', 'border: 4px solid " + color + " !important; box-shadow: 0 0 15px " + color + " !important; background-color: rgba(0,255,0,0.1);');", element);
            }
        } catch (Exception e) {
            System.err.println("⚠️ [Orchestrator] Warning: Failed to execute overlay JS.");
        }
    }

    private void showUiOverlay(String message, String backgroundColor) {
        if (!isSessionActive()) return;
        try {
            if (driver instanceof JavascriptExecutor) {
                JavascriptExecutor js = (JavascriptExecutor) driver;
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
        } catch (Exception e) {
            System.err.println("⚠️ [Orchestrator] Warning: Failed to execute overlay JS.");
        }
    }
    
    public void waitForPageLoad() {
        if(!isSessionActive()) return;
        wait.until(webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));
    }
    
    public void waitForUrlContains(String text) {
        if(!isSessionActive()) return;
        try {
            new WebDriverWait(driver, Duration.ofSeconds(15)).until(ExpectedConditions.urlContains(text));
        } catch (Exception e) {
            System.err.println("⚠️ [Orchestrator] URL did not contain " + text + " in time.");
        }
    }

    public void resilientClick(By locator, String targetDescription, String stepId) {
        io.qameta.allure.Allure.step(stepId + ": Clicking " + targetDescription, () -> {
            if (!isSessionActive()) return;
            io.qameta.allure.Allure.step("Step execution started at: " + java.time.LocalDateTime.now());
            showUiOverlay("🖱️ Action: Clicking " + targetDescription, "#007bff");
            int maxRetries = 1;
            int attempt = 0;

            while (attempt <= maxRetries) {
                if (!isSessionActive()) return;
                try {
                    if (attempt > 0) {
                        io.qameta.allure.Allure.step("Retry attempt triggered: " + attempt);
                        showUiOverlay("⚠️ Fail retrying locator... (Attempt " + attempt + ")", "#f39c12");
                        System.out.println("⚠️ [Orchestrator] Element not found. Retrying... (Attempt " + attempt + ")");
                    }
                    WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
                    element = wait.until(ExpectedConditions.elementToBeClickable(element));
                    highlightElement(element, "blue");

                    try {
                        element.click();
                    } catch (Exception clickEx) {
                        System.out.println("⚠️ [Orchestrator] Native click intercepted/failed. Falling back to JS Click...");
                        if (driver instanceof JavascriptExecutor) {
                            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
                        } else {
                            throw clickEx;
                        }
                    }
                    showUiOverlay("✅ Success! Moving to next test case...", "#28a745");
                    System.out.println("✅ [Orchestrator] Step '" + stepId + "' click passed deterministically on " + locator);
                    HealingReportWriter.logStable(stepId, locator.toString());
                    return; // Success! Exit method
                } catch (Exception e) {
                    attempt++;
                    if (attempt > maxRetries) {
                        io.qameta.allure.Allure.step("Max retries exceeded. Initiating Self-Healing fallback at: " + java.time.LocalDateTime.now());
                        System.err.println("❌ [Orchestrator] Step '" + stepId + "' click failed! Error: " + e.getClass().getSimpleName());
                        io.qameta.allure.Allure.addAttachment("Defect Exception Details", e.getClass().getSimpleName() + ": " + e.getMessage());
                        handleRecovery("click", targetDescription, null, e.getMessage(), stepId, locator.toString());
                        return;
                    }
                }
            }
        });
    }

    public void resilientFill(By locator, String text, String targetDescription, String stepId) {
        io.qameta.allure.Allure.step(stepId + ": Filling " + targetDescription, () -> {
            if (!isSessionActive()) return;
            io.qameta.allure.Allure.step("Step execution started at: " + java.time.LocalDateTime.now());
            showUiOverlay("⌨️ Action: Filling " + targetDescription, "#007bff");
            int maxRetries = 1;
            int attempt = 0;

            while (attempt <= maxRetries) {
                if (!isSessionActive()) return;
                try {
                    if (attempt > 0) {
                        io.qameta.allure.Allure.step("Retry attempt triggered: " + attempt);
                        showUiOverlay("⚠️ Fail retrying locator... (Attempt " + attempt + ")", "#f39c12");
                    }
                    WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
                    highlightElement(element, "blue");

                    element.sendKeys(org.openqa.selenium.Keys.chord(org.openqa.selenium.Keys.COMMAND, "a"));
                    element.sendKeys(org.openqa.selenium.Keys.chord(org.openqa.selenium.Keys.CONTROL, "a"));
                    element.sendKeys(org.openqa.selenium.Keys.BACK_SPACE);
                    try { element.clear(); } catch(Exception e){}

                    element.sendKeys(text);

                    if (driver instanceof JavascriptExecutor) {
                        try {
                            ((JavascriptExecutor) driver).executeScript(
                                    "arguments[0].dispatchEvent(new Event('input', { bubbles: true }));" +
                                            "arguments[0].dispatchEvent(new Event('change', { bubbles: true }));",
                                    element
                            );
                        } catch (Exception e) {}
                    }

                    showUiOverlay("✅ Success! Moving to next test case...", "#28a745");
                    System.out.println("✅ [Orchestrator] Step '" + stepId + "' fill passed deterministically on " + locator);
                    HealingReportWriter.logStable(stepId, locator.toString());
                    return; // Success! Exit method
                } catch (Exception e) {
                    attempt++;
                    if (attempt > maxRetries) {
                        io.qameta.allure.Allure.step("Max retries exceeded. Initiating Self-Healing fallback at: " + java.time.LocalDateTime.now());
                        System.err.println("❌ [Orchestrator] Step '" + stepId + "' fill failed! Error: " + e.getClass().getSimpleName());
                        io.qameta.allure.Allure.addAttachment("Defect Exception Details", e.getClass().getSimpleName() + ": " + e.getMessage());
                        handleRecovery("fill", targetDescription, text, e.getMessage(), stepId, locator.toString());
                        return;
                    }
                }
            }
        });
    }

    private void handleRecovery(String action, String targetDescription, String fillText, String exceptionContext, String stepId, String oldLocator) {
        if (!isSessionActive()) return;
        System.out.println("🔄 [Orchestrator] Initiating Self-Healing Fallback for action: " + action + "...");

        showUiOverlay("❌ Locator Broken! Initiating AI Healing...", "#cc0000");
        System.out.println("❌ Failed locator: " + oldLocator);

        String domExcerpt = domTool.captureCompactDom();
        io.qameta.allure.Allure.addAttachment("🤖 AI Extracted DOM Context", "text/html", domExcerpt);

        showUiOverlay("🤖 AI Agent analyzing DOM to recover intent...", "#cc5500");

        List<SelectorRecoveryAgent.SelectorCandidate> candidates = recoveryAgent.recoverSelectors(targetDescription, exceptionContext, domExcerpt);

        if (candidates == null || candidates.isEmpty()) {
            System.err.println("⚠️ [Orchestrator] Step '" + stepId + "' Healing definitively failed (No candidates returned for " + targetDescription + ")! Continuing test flow...");
            return;
        }

        System.out.println("🔍 Attempting healing with candidate locators:");
        candidates.forEach(c -> System.out.println("   - " + c.selector + " (Confidence: " + c.confidence + ")"));

        boolean healed = false;
        Exception lastException = null;

        for (SelectorRecoveryAgent.SelectorCandidate candidate : candidates) {
            if (!isSessionActive()) return;
            if (candidate.confidence < 0.70) {
                continue; // Skip low confidence alternatives
            }

            System.out.println("⚖️  [PolicyEngine] Attempting candidate: " + candidate.selector + " (Confidence: " + candidate.confidence + ")");

            try {
                By healedLocator;
                if (candidate.strategy.equals("css")) healedLocator = By.cssSelector(candidate.selector);
                else healedLocator = By.xpath(candidate.selector);

                WebDriverWait healWait = new WebDriverWait(driver, Duration.ofSeconds(15));
                WebElement element = healWait.until(ExpectedConditions.visibilityOfElementLocated(healedLocator));
                
                if (action.equals("click")) {
                     element = healWait.until(ExpectedConditions.elementToBeClickable(element));
                }

                highlightElement(element, "lime");
                showUiOverlay("🏥 HEAL SUCCESS! Found: " + candidate.selector, "#28a745");
                io.qameta.allure.Allure.addAttachment("🏥 Healed Selector", "Replaced broken locator with: " + candidate.selector + "\nConfidence Engine Metric: " + candidate.confidence);

                if (action.equals("fill")) {
                    try { element.clear(); } catch(Exception e){}
                    element.sendKeys(fillText);
                    if (driver instanceof JavascriptExecutor) {
                        try {
                            ((JavascriptExecutor) driver).executeScript(
                                    "arguments[0].dispatchEvent(new Event('input', { bubbles: true }));" +
                                            "arguments[0].dispatchEvent(new Event('change', { bubbles: true }));",
                                    element
                            );
                        } catch (Exception e) {}
                    }
                } else {
                    try {
                        element.click();
                    } catch (Exception clickEx) {
                        if (driver instanceof JavascriptExecutor) {
                            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
                        } else {
                            throw clickEx;
                        }
                    }
                }

                HealingReportWriter.logHealed(stepId, oldLocator, candidate.selector, candidate.confidence);
                System.out.println("\n✅ Recovered locator used: " + candidate.selector);
                healed = true;
                break; // Stop retrying if successful
            } catch (Exception ex) {
                System.err.println("⚠️ Candidate " + candidate.selector + " failed: " + ex.getClass().getSimpleName());
                lastException = ex;
            }
        }

        if (!healed) {
            HealingReportWriter.logFailed(stepId, oldLocator);
            System.err.println("⚠️ [Orchestrator] Step '" + stepId + "' Healing definitively failed! Continuing test flow...");
        }
    }
}
