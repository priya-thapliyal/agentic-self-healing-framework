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
    public String captureCompactDom() {
        if (driver instanceof JavascriptExecutor) {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            // Simple approach: get outerHTML of body
            // Real Agent framework should prune <style>, <script>, <svg>, etc.
            String script = "return document.body.outerHTML;";
            String rawHtml = (String)js.executeScript(script);
            
            // Limit length for baseline
            return rawHtml.length() > 5000 ? rawHtml.substring(0, 5000) + "...[TRUNCATED]" : rawHtml;
        }
        return "DOM Tree Unavailable";
    }
}
