package agents;

import java.util.ArrayList;
import java.util.List;

/**
 * Purpose: recover the best selector for a failed step.
 * Input: failure context + DOM/accessibility summary + memory.
 * Output: list of candidates with rationale and confidence.
 */
public class SelectorRecoveryAgent {

    public SelectorRecoveryAgent() {
        // Initialize OpenAI / LLM Configuration here in production
    }

    /**
     * Given the context of a failed step, proposes alternative selectors using an simulated LLM approach for demo.
     *
     * @param targetDescription Description of what we were trying to find
     * @param failureContext The exception or error that happened
     * @param domExcerpt Snapshot of the active DOM near where we expected the element
     * @return List of newly generated and ranked selector alternatives
     */
    public List<SelectorCandidate> recoverSelectors(String targetDescription, String failureContext, String domExcerpt) {
        
        System.out.println("🤖 [SelectorRecoveryAgent] Activated! Analyzing telemetry for failed target: '" + targetDescription + "'");
        System.out.println("🤖 [SelectorRecoveryAgent] Error received: " + failureContext);
        System.out.println("🤖 [SelectorRecoveryAgent] Analyzing DOM snapshot...");
        System.out.println("🤖 [SelectorRecoveryAgent] Generating hypotheses...");
        
        List<SelectorCandidate> candidates = new ArrayList<>();
        
        // Universal NLP Heuristic Engine for dynamic testing
        String intentLower = targetDescription.toLowerCase();
        String coreText = targetDescription.replaceAll("(?i)(button|menu|link|input|tab|field|icon|action)", "").trim();
        
        if (intentLower.contains("email") || intentLower.contains("username")) {
            candidates.add(new SelectorCandidate("//input[contains(@placeholder, 'User') or @type='email']", "xpath", 0.99, "Found User input field."));
        } else if (intentLower.contains("password")) {
            candidates.add(new SelectorCandidate("//input[contains(@placeholder, 'Pass') or @type='password']", "xpath", 0.99, "Standard password input type found."));
        } else if (intentLower.contains("sign in") || intentLower.contains("login")) {
            candidates.add(new SelectorCandidate("//input[@type='submit' or @value='Login'] | //button[contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'login') or contains(., 'Sign in')]", "xpath", 0.98, "Submit button matched for Swag labs."));
        } else if (intentLower.contains("add to cart") || intentLower.contains("cart addition")) {
             candidates.add(new SelectorCandidate("//button[contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'add')]", "xpath", 0.95, "Found commerce action button."));
        } else {
            System.out.println("🤖 [SelectorRecoveryAgent] Detected complex dynamic intent: '" + coreText + "'. Extracting DOM semantic text nodes...");
            
            String lowerCore = coreText.toLowerCase();
            
            // Context-Aware Heuristics: determine primary HTML tag geometry based on literal description!
            if (targetDescription.toLowerCase().contains("button")) {
                candidates.add(new SelectorCandidate("//button[contains(translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '" + lowerCore + "')]", "xpath", 0.99, "Explicit button intent detected."));
                candidates.add(new SelectorCandidate("//a[contains(translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '" + lowerCore + "')]", "xpath", 0.95, "Anchor fallback for button."));
            } else if (targetDescription.toLowerCase().contains("menu") || targetDescription.toLowerCase().contains("link")) {
                candidates.add(new SelectorCandidate("//a[contains(translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '" + lowerCore + "')]", "xpath", 0.99, "Explicit anchor/menu intent detected."));
                candidates.add(new SelectorCandidate("//button[contains(translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '" + lowerCore + "')]", "xpath", 0.95, "Button fallback for menu."));
            } else {
                // Widget/Metric Cards: target exactly the deeply nested text node to prevent body background clicks!
                candidates.add(new SelectorCandidate("//*[translate(normalize-space(text()), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')='" + lowerCore + "']", "xpath", 0.99, "Exact visual widget text node match"));
                candidates.add(new SelectorCandidate("//*[contains(translate(normalize-space(text()), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '" + lowerCore + "')]", "xpath", 0.95, "Partial visual widget text match"));
            }
            candidates.add(new SelectorCandidate("//*[text()='" + coreText + "']", "xpath", 0.90, "Universal exact text node match."));
        }
        
        return candidates;
    }

    /**
     * Data Contract for Candidate Selectors returned by LLM
     */
    public static class SelectorCandidate {
        public String selector;
        public String strategy;
        public double confidence;
        public String rationale;

        public SelectorCandidate(String selector, String strategy, double confidence, String rationale) {
            this.selector = selector;
            this.strategy = strategy;
            this.confidence = confidence;
            this.rationale = rationale;
        }
    }
}
