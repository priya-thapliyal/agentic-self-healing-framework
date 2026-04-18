package core;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;

public class HealingReportWriter {
    
    private static final String REPORT_PATH = "healing-report.md";

    public static void initializeReport() {
        try {
            String header = "# 🤖 Self-Healing Execution Report\n" +
                            "**Started at:** " + LocalDateTime.now() + "\n\n" +
                            "| Action / Step | Status | Original Locator | Healed Locator | Confidence |\n" +
                            "|---|---|---|---|---|\n";
            Files.write(Paths.get(REPORT_PATH), header.getBytes());
        } catch (IOException e) {
            System.err.println("Failed to write report header");
        }
    }

    public static void logStable(String stepId, String locator) {
        String md = String.format("| %s | ✅ Pased | `%s` | - | - |\n", stepId, locator);
        append(md);
    }

    public static void logHealed(String stepId, String oldLocator, String newLocator, double confidence) {
        String md = String.format("| %s | 🏥 Auto-Healed | `%s` | `%s` | %.2f |\n", stepId, oldLocator, newLocator, confidence);
        append(md);
    }
    
    public static void logFailed(String stepId, String locator) {
        String md = String.format("| %s | 💀 Failed | `%s` | - | - |\n", stepId, locator);
        append(md);
    }

    private static void append(String content) {
        try {
            Files.write(Paths.get(REPORT_PATH), content.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.err.println("Failed to append to report");
        }
    }
}
