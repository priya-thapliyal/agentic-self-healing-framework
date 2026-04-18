package tools;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

/**
 * Produces compact, token-efficient context for agents by capturing fragments of the DOM.
 */
public class DomSnapshotTool {

    private final WebDriver driver;

    public DomSnapshotTool(WebDriver driver) {
        this.driver = driver;
    }

    /**
     * Captures a simplified version of the current page DOM to feed to an LLM.
     * In a full implementation, this should prune unneeded tags (script, style, svg) 
     * and mask sensitive PII data to save tokens and improve privacy.
     *
     * @return Compact HTML string representation of the body
     */
    private boolean isDriverActive() {
        if (driver == null) return false;
        try {
            driver.getTitle();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String captureCompactDom() {
        if (!isDriverActive()) {
            System.err.println("⚠️ [DomSnapshotTool] Driver session invalid, skipping DOM capture");
            return "DOM Tree Unavailable - Session Closed";
        }
        try {
            if (driver instanceof JavascriptExecutor) {
                JavascriptExecutor js = (JavascriptExecutor) driver;
                String script = "return document.body.outerHTML;";
                String rawHtml = (String)js.executeScript(script);
                return rawHtml != null && rawHtml.length() > 5000 ? rawHtml.substring(0, 5000) + "...[TRUNCATED]" : rawHtml;
            }
        } catch (Exception e) {
            System.err.println("⚠️ [DomSnapshotTool] Error capturing DOM: " + e.getMessage());
        }
        return "DOM Tree Unavailable";
    }
}
