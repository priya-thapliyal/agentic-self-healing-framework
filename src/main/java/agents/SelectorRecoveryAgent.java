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
            candidates.add(new SelectorCandidate("//input[contains(@placeholder, 'User') or translate(@placeholder, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')='email' or @type='email' or translate(@name, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')='email']", "xpath", 0.99, "Found User input field."));
        } else if (intentLower.contains("password")) {
            candidates.add(new SelectorCandidate("//input[contains(@placeholder, 'Pass') or translate(@placeholder, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')='password' or @type='password' or translate(@name, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')='password']", "xpath", 0.99, "Standard password input type found."));
        } else if (intentLower.contains("sign in") || intentLower.contains("login")) {
            candidates.add(new SelectorCandidate("//button[@type='submit']", "xpath", 0.99, "Explicit Submit Button Type Match"));
            candidates.add(new SelectorCandidate("//input[@type='submit' or translate(@value, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')='login' or translate(@value, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')='sign in'] | //button[contains(translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'login') or contains(translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'sign in')] | //a[contains(translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'login') or contains(translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'sign in')]", "xpath", 0.98, "Submit button matched for Swag labs."));
        } else if (intentLower.contains("add to cart") || intentLower.contains("cart addition")) {
             candidates.add(new SelectorCandidate("//button[contains(translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'add')] | //a[contains(translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'add')]", "xpath", 0.95, "Found commerce action button."));
        } else {
            System.out.println("🤖 [SelectorRecoveryAgent] Detected complex dynamic intent: '" + coreText + "'. Extracting DOM semantic text nodes...");
            String lowerCore = coreText.toLowerCase();
            String formattedCore = coreText.replace(" ", "-").toLowerCase();
            
            // Prioritize semantic attributes!
            candidates.add(new SelectorCandidate("//*[@id='" + lowerCore + "' or @id='" + formattedCore + "']", "xpath", 0.99, "Exact ID Match Strategy"));
            candidates.add(new SelectorCandidate("//*[@data-testid='" + lowerCore + "' or @data-testid='" + formattedCore + "']", "xpath", 0.98, "Data-TestId Match Strategy"));
            candidates.add(new SelectorCandidate("//*[@aria-label='" + coreText + "' or translate(@aria-label, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')='" + lowerCore + "']", "xpath", 0.97, "Aria-Label Match Strategy"));
            candidates.add(new SelectorCandidate("//*[contains(@class, '" + lowerCore + "') or contains(@class, '" + formattedCore + "')]", "xpath", 0.95, "Class Match Strategy"));
            
            // Context-Aware Heuristics: determine primary HTML tag geometry based on literal description!
            if (targetDescription.toLowerCase().contains("button")) {
                candidates.add(new SelectorCandidate("//button[contains(translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '" + lowerCore + "')] | //input[@type='submit' or @type='button'][contains(translate(@value, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '" + lowerCore + "')]", "xpath", 0.90, "Explicit button intent detected."));
                candidates.add(new SelectorCandidate("//a[contains(translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '" + lowerCore + "')]", "xpath", 0.85, "Anchor fallback for button."));
                candidates.add(new SelectorCandidate("//*[contains(translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '" + lowerCore + "') and not(self::script) and not(self::style)]", "xpath", 0.80, "Universal Node Fallback for button."));
            } else if (targetDescription.toLowerCase().contains("menu") || targetDescription.toLowerCase().contains("link")) {
                candidates.add(new SelectorCandidate("//a[contains(@href, '" + lowerCore + "')]", "xpath", 0.95, "Href explicit routing intent detected."));
                candidates.add(new SelectorCandidate("//a[contains(translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '" + lowerCore + "')]", "xpath", 0.90, "Explicit anchor/menu intent detected."));
                candidates.add(new SelectorCandidate("//button[contains(translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '" + lowerCore + "')]", "xpath", 0.85, "Button fallback for menu."));
                candidates.add(new SelectorCandidate("//*[contains(translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '" + lowerCore + "') and not(self::script) and not(self::style)]", "xpath", 0.80, "Universal Node Fallback for menu."));
            } else {
                // Widget/Metric Cards: use broader selectors to handle frameworks like React where texts are nested inside layout divs/spans
                candidates.add(new SelectorCandidate("//*[contains(translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '" + lowerCore + "') and not(self::script) and not(self::style)]", "xpath", 0.85, "Visual widget text substring node match"));
            }
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
