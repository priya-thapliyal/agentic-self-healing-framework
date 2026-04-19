# Agentic Self-Healing Selenium Framework

Welcome to the **Agentic Self-Healing Selenium Framework**! This proof-of-concept demonstrates how to dramatically increase UI automation stability by wrapping traditional Selenium tests with an "Agentic" recovery orchestrator.

When a UI selector breaks due to design changes or dynamic classes, traditional tests fail immediately. This framework catches those failures on the fly, analyzes the DOM with intelligent heuristics (which represents LLM capabilities), retrieves a working selector, and auto-heals the test at runtime.

---

## 🏗️ Project Layout

```text
self-healing-selenium/
├── pom.xml                                 # Maven dependencies
├── docs/
│   └── agentic-selenium-framework-design.md # Original Architecture Document
└── src/
    ├── main/java/
    │   ├── core/
    │   │   └── TestOrchestrator.java       # Intercepts failures & routes to agents
    │   ├── agents/
    │   │   └── SelectorRecoveryAgent.java  # Predicts new selectors based on context
    │   └── tools/
    │       └── DomSnapshotTool.java        # Captures fragments of the DOM for analysis
    └── test/java/e2e/
        └── DashboardTest.java              # The main CTO Showcase Demo
```

---

## 📖 Code Walkthrough

### 1. The Core Orchestrator (`TestOrchestrator.java`)
This is the heart of the framework. Instead of calling `driver.findElement(By.id("...")).click()`, tests call `orchestrator.resilientClick()`. 
If the selector works, execution is instantaneous. If it drops an error like a `TimeoutException` or `NoSuchElementException`, the orchestrator **pauses execution, grabs a snapshot of the DOM, queries the Recovery Agent, and recursively retries the action with the newly generated selector.**

### 2. The Recovery Agent (`SelectorRecoveryAgent.java`)
In a full production environment, this agent queries an OpenAI API using a system prompt. For this showcase, we simulate the LLM using a local heuristics engine to guarantee fast, deterministic recovery during your demo. The agent analyzes the DOM structure along with your original "semantic intent" (e.g. *"Click the Sign in button"*), then outputs hypothetical alternative selectors with **Confidence Scores**.

### 3. Context Tooling (`DomSnapshotTool.java`)
Language Model Agents require context. Before delegating to the `SelectorRecoveryAgent`, the orchestrator uses this tool to grab a snapshot of the current application DOM. This tool ensures that only the relevant structural data is sent for recovery.

### 4. The Showcase Test (`DashboardTest.java`)
We built a deliberate self-healing demonstration in JUnit.
1. **Scenario A:** Demonstrates normal Selenium execution. The orchestrator encounters a valid selector and passes it almost instantly.
2. **Scenario B:** The test explicitly passes a **Broken Selector** (`button.old-legacy-btn`). The script intentionally fails but triggers the `Orchestrator`. It evaluates hypotheses, yields an intelligent updated selector (`//button[contains(., 'Sign in')]`), performs the surgical heal with JavaScript, and successfully passes the assertion!

---

## 🚀 How to Run the Showcase

### Prerequisites

You need **Java 17+** and **Maven** installed on your system.

If you don't have Maven installed, you can simply run (on macOS):
```bash
brew install maven
```

### Execution

To run the framework and watch the agentic healing in text-logs during execution, run the following command in the root of the project:

```bash
mvn test
```

### Expected Output
When you run the command, your terminal will produce the following showcase output:

```text
==================================================
🚀 STARTING AGENTIC SELF-HEALING SHOWCASE 🚀
==================================================

--- 🟢 SCENARIO A: STABLE SELECTOR ---
✅ [Orchestrator] Step 'step-assert-container' passed deterministically on By.className: rw-title

--- 🔴 SCENARIO B: BROKEN SELECTOR ---
Intent: Clicking 'Sign in' button using old CSS selector 'button.old-legacy-btn'
❌ [Orchestrator] Step 'step-click-signin' failed! Error: TimeoutException
🔄 [Orchestrator] Initiating Self-Healing Fallback...
🤖 [SelectorRecoveryAgent] Activated! Analyzing telemetry for failed target: 'Sign in'
🤖 [SelectorRecoveryAgent] Error received: Expected condition failed: waiting for presence of element
🤖 [SelectorRecoveryAgent] Analyzing DOM snapshot...
🤖 [SelectorRecoveryAgent] Generating hypotheses...
🤖 [SelectorRecoveryAgent] Detected intent 'Sign In' action. Scanning for relevant structural elements...
⚖️  [PolicyEngine] Auto-heal approved for candidate: //button[contains(., 'Sign in')] (Confidence: 0.98)
🔄 [Orchestrator] Retrying action with new selector...
🏥 [Orchestrator] SURGERY SUCCESSFUL! Action completed using AI healed selector.

--- ✅ TEST PASSED ---
The script completed successfully despite the broken selector!
==================================================
```
*(Notice that Maven will report `BUILD SUCCESS` at the end, proving the script healed and naturally continued.)*


CI/CD demo trigger