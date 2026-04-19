import os
import json
import re
import xml.etree.ElementTree as ET
from datetime import datetime

def parse_healing_report(file_path):
    selector_heals = 0
    flow_heals = 0
    failures = 0
    events = []

    if not os.path.exists(file_path):
        return selector_heals, flow_heals, failures, events

    with open(file_path, 'r') as f:
        lines = f.readlines()

    for line in lines:
        if '|' in line and not line.startswith('| Action') and not line.startswith('|---'):
            parts = [p.strip() for p in line.split('|')]
            if len(parts) >= 6:
                step_id = parts[1]
                status = parts[2]
                original = parts[3]
                healed = parts[4]
                confidence = parts[5]

                event = {
                    "step": step_id,
                    "status": status,
                    "original_locator": original,
                    "healed_locator": healed,
                    "confidence": confidence
                }
                events.append(event)

                if "Auto-Healed" in status:
                    selector_heals += 1
                elif "Failed" in status:
                    failures += 1
    
    return selector_heals, flow_heals, failures, events

def parse_testng_results(file_path):
    total = 0
    passed = 0
    failed = 0
    skipped = 0

    if not os.path.exists(file_path):
        return total, passed, failed, skipped

    tree = ET.parse(file_path)
    root = tree.getroot()

    total = int(root.attrib.get('total', 0))
    passed = int(root.attrib.get('passed', 0))
    failed = int(root.attrib.get('failed', 0))
    skipped = int(root.attrib.get('skipped', 0))

    return total, passed, failed, skipped

def main():
    # GitHub Environment Variables
    run_id = os.getenv('GITHUB_RUN_ID', 'local')
    run_number = os.getenv('GITHUB_RUN_NUMBER', '0')
    branch_name = os.getenv('GITHUB_REF_NAME', 'local')
    commit_sha = os.getenv('GITHUB_SHA', 'local')
    workflow_name = os.getenv('GITHUB_WORKFLOW', 'CI Self-Healing Test Suite')
    repository = os.getenv('GITHUB_REPOSITORY', 'local/repo')
    event_name = os.getenv('GITHUB_EVENT_NAME', 'push')
    
    # Paths
    healing_report_path = 'healing-report.md'
    testng_results_path = 'target/surefire-reports/testng-results.xml'
    
    # Parse data
    selector_heals, flow_heals, heal_failures, healing_events = parse_healing_report(healing_report_path)
    total_tests, tests_passed, tests_failed, tests_skipped = parse_testng_results(testng_results_path)
    
    # Estimated time saved (Matching teammate: 0.5 mins per heal?)
    # 11 heals * 0.5 = 5.5
    time_saved_per_heal = 0.5 
    estimated_time_saved = (selector_heals + flow_heals) * time_saved_per_heal
    
    # Success rate
    total_heal_attempts = selector_heals + flow_heals + heal_failures
    heal_success_rate = ( (selector_heals + flow_heals) / total_heal_attempts * 100) if total_heal_attempts > 0 else 100

    # Match teammate's structure exactly
    run_data = {
        "runId": run_id,
        "runNumber": run_number,
        "workflowName": workflow_name,
        "repository": repository,
        "branch": branch_name,
        "commitSha": commit_sha,
        "event": event_name,
        "generatedAt": datetime.utcnow().isoformat() + "Z", # Match Z format
        "totalRuns": total_tests, # Teammate seems to use total tests as total runs?
        "totalTestsCompleted": total_tests,
        "totalSelectorHeals": selector_heals,
        "totalFlowHeals": flow_heals,
        "totalFailures": heal_failures,
        "estimatedTimeSaved": str(round(estimated_time_saved, 1)),
        "healSuccessRate": str(round(heal_success_rate, 1)),
        "file": f"runs/{run_id}.json",
        "events": healing_events # Full data kept here
    }

    # Save run data
    os.makedirs('dashboard-data/runs', exist_ok=True)
    run_file = f"dashboard-data/runs/{run_id}.json"
    with open(run_file, 'w') as f:
        json.dump(run_data, f, indent=2)

    # Update latest.json
    with open('dashboard-data/latest.json', 'w') as f:
        json.dump(run_data, f, indent=2)

    # Update history.json
    history_file = 'dashboard-data/history.json'
    history = []
    if os.path.exists(history_file):
        with open(history_file, 'r') as f:
            try:
                history = json.load(f)
                if not isinstance(history, list):
                    history = []
            except:
                history = []
    
    # Teammate's history summary is the same object but usually minus 'events' to save space?
    # Let's keep it clean like their history.json
    summary = run_data.copy()
    if "events" in summary:
        del summary["events"]
    
    # Check if runId already exists
    history = [h for h in history if h.get('runId') != run_id]
    history.insert(0, summary) 
    
    with open(history_file, 'w') as f:
        json.dump(history, f, indent=2)

    print(f"Successfully generated telemetry data for run {run_id}")

if __name__ == "__main__":
    main()
